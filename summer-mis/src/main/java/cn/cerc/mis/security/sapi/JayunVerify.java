package cn.cerc.mis.security.sapi;

import javax.servlet.http.HttpServletRequest;

public class JayunVerify {

    private HttpServletRequest request;
    private String message;

    public JayunVerify(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * 身份证校验
     *
     * @param user     应用用户帐号
     * @param realName 用户真实姓名
     * @param idCard   用户身份证号
     * @return 调用成功时返回 true
     */
    public boolean idCard(String user, String realName, String idCard) {
        JayunAPI api = new JayunAPI(request);
        api.put("ip", api.getRemoteIP());
        api.put("user", user);
        api.put("realName", realName);
        api.put("idCard", idCard);
        api.post("verify.idCard");
        this.setMessage(api.getMessage());
        return api.isResult();
    }

    /**
     * 银行卡4元素校验
     *
     * @param user     应用用户帐号
     * @param realName 用户真实姓名
     * @param idCard   用户身份证号
     * @param bankCard 用户银行卡号
     * @param mobile   用户手机号码
     * @return 调用成功时返回 true
     */
    public boolean bankCard(String user, String realName, String idCard, String bankCard, String mobile) {
        JayunAPI api = new JayunAPI(request);
        api.put("ip", api.getRemoteIP());
        api.put("user", user);
        api.put("realName", realName);
        api.put("idCard", idCard);
        api.put("bankCard", bankCard);
        api.put("mobile", mobile);
        api.post("verify.bankCard");
        this.setMessage(api.getMessage());
        return api.isResult();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
