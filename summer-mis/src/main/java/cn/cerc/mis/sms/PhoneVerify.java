package cn.cerc.mis.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.IHandle;
import cn.cerc.core.Utils;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.ISystemTable;
import cn.cerc.mis.other.BufferType;
import cn.cerc.mis.other.MemoryBuffer;
import cn.cerc.mis.language.R;

public class PhoneVerify {
    private static final Logger log = LoggerFactory.getLogger(PhoneVerify.class);

    public static final String ERROR_1 = " 分钟内验证码有效，可继续使用，请勿頻繁发送";
    public static final String ERROR_2 = "没有发送验证码";
    public static final String ERROR_3 = "验证码内容为空";

    private String nationalCode = "+86";
    private String mobile;
    private String phone; // 用户注册的手机号

    private IHandle handle;
    private String verifyCode;
    private String message = "";
    private int expires = 900; // 验证码有效时间，单位：秒

    public PhoneVerify(IHandle handle) {
        this.handle = handle;
    }

    public PhoneVerify(IHandle handle, String mobile) {
        this.handle = handle;
        this.init(mobile);
    }

    public PhoneVerify init(String mobile) {
        this.mobile = mobile;

        ISystemTable systemTable = Application.getBean("systemTable", ISystemTable.class);
        // 取安全手机号，若取不到则默认等于帐号
        SqlQuery ds = new SqlQuery(handle);
        ds.add("select UID_,countryCode_,securityMobile_ from %s", systemTable.getUserInfo());
        ds.add("where mobile_='%s'", Utils.safeString(mobile));
        ds.open();
        if (!ds.eof()) {
            this.nationalCode = ds.getString("countryCode_");
            String securityMobile = ds.eof() ? "" : ds.getString("securityMobile_");
            if (!"".equals(securityMobile)) {
                this.mobile = securityMobile;
            } else if (!mobile.startsWith("+")) {
                this.mobile = this.nationalCode + this.mobile;
            }
        } else {
            this.mobile = this.nationalCode + this.mobile;
        }
        return this;
    }

    public PhoneVerify init() {
        ISystemTable systemTable = Application.getBean("systemTable", ISystemTable.class);
        // 取安全手机号，若取不到则默认等于帐号
        SqlQuery ds = new SqlQuery(handle);
        ds.add("select UID_,countryCode_,mobile_,securityMobile_ from %s", systemTable.getUserInfo());
        ds.add("where id_='%s'", handle.getUserCode());
        ds.open();
        if (ds.eof()) {
            throw new RuntimeException("当前用户不存在");
        }
        this.mobile = ds.getString("mobile_");
        this.phone = mobile;
        this.nationalCode = ds.getString("countryCode_");
        String securityMobile = ds.eof() ? "" : ds.getString("securityMobile_");
        if (!"".equals(securityMobile)) {
            this.mobile = securityMobile;
        } else if (!mobile.startsWith("+")) {
            this.mobile = this.nationalCode + this.mobile;
        }
        return this;
    }

    // 发送验证码，result = 0：不需要；1：成功；２：失败
    public SendStatus sendVerify() {
        if (isSecurity()) {
            return SendStatus.UNWANTED; // 不需要
        }
        if (sendVerifyCode()) {
            return SendStatus.OK; // 成功
        } else {
            return SendStatus.ERROR; // 失败
        }
    }

    // 需要检验的的验证码 0: 通过; 1.不通过；2.无法读取
    public CheckStatus checkVerify(String value) {
        if (isSecurity()) {
            return CheckStatus.PASS; // 校验通过
        }
        if (readVerifyCode()) {
            String data = value == null ? "" : value;
            if (data.equals(this.verifyCode)) {
                return CheckStatus.PASS; // 检验通过
            } else {
                return CheckStatus.DIFFERENCE; // 检验不通过
            }
        } else {
            return CheckStatus.ERROR; // 无法读取等其它原因
        }
    }

    // 创建并发送效验码，请改使用sendVerify函数
    public boolean sendVerifyCode() {
        verifyCode = createRandomNum(6); // 生成6位数字随机码
        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getObject, "code" + mobile)) {
            if (!buff.isNull()) {
                log.info("SMS verifyCode: " + buff.getString("code"));
                this.message = String.format("%s %s", "" + (this.expires / 60), R.asString(handle, ERROR_1));
                return false;
            }
            // 组装发送内容
            String text, templateId, templateValues;
            if (nationalCode.equals("+86")) {
                text = "【MIUGROUP】由账号" + this.phone + "发送的验证码是" + verifyCode; // 发短信调用
                templateId = "51591";
            } else if (nationalCode.equals("+852")) {
                text = "【MIUGROUP】由賬號" + this.phone + "發送的驗證碼是" + verifyCode; // 发短信调用
                templateId = "51592";
            } else {
                text = "【MIUGROUP】The verification code sent by " + this.phone + " is " + verifyCode; // 发短信调用
                templateId = "51593";
            }
            templateValues = "#account#=" + this.phone + "&#code#=" + verifyCode;
            // 开始发送讯息
            String no = this.mobile;
            if (!no.startsWith("+"))
                no = nationalCode + no;
            YunpianSMS obj1 = new YunpianSMS(no);
            obj1.sendText(text);
            // if (!obj1.sendText(text)) {
            // this.message = obj1.getMessage();
            // return false;
            // }
            if (no.startsWith("+86")) {
                JuheSMS obj2 = new JuheSMS(no.substring(3));
                obj2.sendByTemplateId(templateId, templateValues);
                // if (!obj2.sendByTemplateId(templateId, templateValues)) {
                // this.message = obj2.getMessage();
                // return false;
                // }
            }
            // 存入到缓存
            buff.setExpires(this.expires);
            buff.setField("code", verifyCode);
        }
        return true;
    }

    /**
     * 检测当前环境是否安全，若不安全则需要显示验证码输入框并进行检查，如果所绑定的帐号、设备码、IP均未发生变化，且时间在4小时内，则视为安全
     * 
     * @return true: 安全（不需要进行手机验证）；false: 不安全
     */
    private boolean isSecurity() {
        return false;
    }

    // 取验证码，请改使用 requestVerify
    public boolean readVerifyCode() {
        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getObject, "code" + mobile)) {
            if (buff.isNull()) {
                this.message = R.asString(handle, ERROR_2);
                return false;
            }
            verifyCode = buff.getString("code").trim();
            if ("".equals(verifyCode)) {
                this.message = R.asString(handle, ERROR_3);
                return false;
            }
            return true;
        }
    }

    public boolean sendMessage(String text) {
        String no = this.mobile;
        if (!no.startsWith("+"))
            no = nationalCode + no;
        YunpianSMS sms = new YunpianSMS(no);
        boolean result = sms.sendText(text);
        if (!result)
            this.message = sms.getMessage();
        return result;
    }

    public void clearBuffer() {
        MemoryBuffer.delete(BufferType.getObject, "code" + mobile);
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public String getMobile() {
        return mobile;
    }

    public int getExpires() {
        return expires;
    }

    public void setExpires(int expires) {
        this.expires = expires;
    }

    public String getMessage() {
        return message;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode;
    }

    public enum SendStatus {
        UNWANTED, OK, ERROR
    }

    public enum CheckStatus {
        PASS, DIFFERENCE, ERROR
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public static String createRandomNum(int count) {
        // 验证码
        String vcode = "";
        for (int i = 0; i < count; i++) {
            vcode = vcode + (int) (Math.random() * 9);
        }
        return vcode;
    }
}
