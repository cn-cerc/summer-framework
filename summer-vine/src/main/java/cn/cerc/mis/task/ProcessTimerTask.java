package cn.cerc.mis.task;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import cn.cerc.core.ClassResource;
import cn.cerc.core.ISession;
import cn.cerc.core.LanguageResource;
import cn.cerc.core.Record;
import cn.cerc.core.TDateTime;
import cn.cerc.core.Utils;
import cn.cerc.db.cache.Redis;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.mis.SummerMIS;
import cn.cerc.mis.config.AccessLevel;
import cn.cerc.mis.core.AppClient;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.CenterService;
import cn.cerc.mis.custom.SessionDefault;
import cn.cerc.mis.other.MemoryBuffer;
import cn.cerc.mis.rds.StubHandle;

@Component
public class ProcessTimerTask extends TimerTask implements ApplicationContextAware, IHandle {
    private static final Logger log = LoggerFactory.getLogger(ProcessTimerTask.class);
    private static final ClassResource res = new ClassResource(ProcessTimerTask.class, SummerMIS.ID);
    private static boolean isRunning = false;
    private ISession session;
    private int sessionTimes = 0;

    // 循环反复执行
    @Override
    public void run() {
        if (!isRunning) {
            isRunning = true;
            if (ServerConfig.enableTaskService()) {
                try {
                    runTask();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            isRunning = false;
        } else {
            log.info("last task has not ended");
        }
    }

    private void runTask() {
        if (sessionTimes == 0) {
            try {
                if (session != null) {
                    this.session.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
            sessionTimes = 3600;
            this.session = Application.getSession();
            init();
        }
        try {
            // 同一秒内，只允许执行1个任务
            ApplicationContext context = Application.getContext();
            for (String beanId : context.getBeanNamesForType(AbstractTask.class)) {
                AbstractTask task = (AbstractTask) Application.getBean(this, beanId);
                if (task == null) {
                    continue;
                }
                try {
                    String timeNow = TDateTime.now().getTime().substring(0, 5);
                    if (!"".equals(task.getTime()) && !task.getTime().equals(timeNow)) {
                        continue;
                    }

                    // 标识为已执行
                    String buffKey = MemoryBuffer.buildObjectKey(task.getClass());
                    if (Redis.get(buffKey) != null) {
                        continue;
                    }
                    Redis.set(buffKey, "ok", task.getInterval());

                    if (task.getInterval() > 1) {
                        log.debug("execute task: {}", task.getClass().getName());
                    }

                    task.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                }
            }
        } finally {
            sessionTimes--;
        }
    }

    /**
     * 初始化特殊用户的 handle
     */
    private void init() {
        // 创建token
        String token = getAuthToken(StubHandle.DefaultUser, StubHandle.password, StubHandle.machineCode, this);
        if (Utils.isEmpty(token)) {
            return;
        }

        // 将用户信息赋值到句柄
        CenterService svr = new CenterService(this);
        svr.setService("SvrSession.byUserCode");
        if (!svr.exec("CorpNo_", StubHandle.DefaultBook, "UserCode_", StubHandle.DefaultUser)) {
            throw new RuntimeException(svr.getMessage());
        }

        Record record = svr.getDataOut().getHead();

        session.setProperty(SessionDefault.TOKEN_CREATE_ENTER, "start");
        try {
            session.setProperty(ISession.TOKEN, token);
            session.setProperty(ISession.CORP_NO, StubHandle.DefaultBook);
            session.setProperty(ISession.USER_CODE, StubHandle.DefaultUser);
            session.setProperty(Application.ClientIP, "0.0.0.0");
            session.setProperty(Application.UserId, record.getString("UserID_"));
            session.setProperty(Application.LoginTime, record.getDateTime("LoginTime_"));
            session.setProperty(Application.ProxyUsers, record.getString("ProxyUsers_"));
            session.setProperty(ISession.USER_NAME, record.getString("UserName_"));
            session.setProperty(ISession.LANGUAGE_ID, record.getString("Language_"));
        } finally {
            session.setProperty(SessionDefault.TOKEN_CREATE_ENTER, null);
        }
    }

    /**
     * 向public服务器获取授权令牌
     *
     * @param userCode    用户代码
     * @param password    用户密码
     * @param machineCode 设备代码
     * @return 用户授权令牌 token
     */
    private static String getAuthToken(String userCode, String password, String machineCode, IHandle handle) {
        if (Utils.isEmpty(userCode)) {
            throw new RuntimeException(String.format(res.getString(1, "%s 不允许为空"), "userCode"));
        }
        if (Utils.isEmpty(password)) {
            throw new RuntimeException(String.format(res.getString(1, "%s 不允许为空"), "password"));
        }
        if (Utils.isEmpty(machineCode)) {
            throw new RuntimeException(String.format(res.getString(1, "%s 不允许为空"), "machineCode"));
        }

        CenterService svr = new CenterService(handle);
        svr.setService("ApiToken.getToken");
        Record headIn = svr.getDataIn().getHead();
        headIn.setField("userCode", userCode);
        headIn.setField("password", password);
        headIn.setField("clientId", machineCode);
        headIn.setField("device", AppClient.pc);
        headIn.setField("languageId", LanguageResource.appLanguage);
        headIn.setField("access", AccessLevel.Access_Task);// 访问层级获取队列授权
        if (!svr.exec()) {
            throw new RuntimeException(svr.getMessage());
        }
        String token = svr.getDataOut().getHead().getString("token");
        log.debug("userCode {} token {}", userCode, token);
        if (Utils.isEmpty(token)) {
            throw new RuntimeException(res.getString(3, "服务器没有返回token"));
        }
        return token;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        Application.setContext(context);
    }

    @Override
    public ISession getSession() {
        return session;
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;
    }

}
