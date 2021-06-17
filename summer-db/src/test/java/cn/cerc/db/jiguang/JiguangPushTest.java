package cn.cerc.db.jiguang;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.TDateTime;
import cn.cerc.db.core.StubSession;

public class JiguangPushTest {
    private StubSession handle;
    private static final Logger log = LoggerFactory.getLogger(JiguangPushTest.class);

    @Before
    public void setUp() throws Exception {
        handle = new StubSession();
    }

    @Test
    public void test() {
        // 初始化极光推送
        JiguangPush push = new JiguangPush(handle);

        // 消息标题，仅安卓机型有效，IOS设备忽略
        push.setTitle("消息推送测试");

        // 通知栏消息内容
        String message = TDateTime.now().toString() + "这是系统向您发送的测试消息，如有打扰，请您忽略，谢谢！";
        log.info(message);
        push.setMessage(message);

        // 附加的消息id
        push.setMsgId("3707");

        // 发送给指定的设备Id
//        push.send(ClientType.Android, "n_f526c72cf13474d7");// itjun-xiaomi-mix2
        push.send(ClientType.Android, "n_d4ffa59ea6d1d9f3");// joylee-xiaomi-mix2
//        push.send(ClientType.Android, "n_a86ddf4df465a83f");// weibo-xiaomi-5c
//        push.send(ClientType.IOS, "i_82D7A281FC37497F810085A6D7EFB1B2");// ly-iPhone-SE2
//        push.send(ClientType.IOS, "i_A1688A8910B04499ABC64B035B559921");// wsg-iPhone-xr
//        push.send(ClientType.IOS, "i_82D7A281FC37497F810085A6D7EFB1B2");// itjun-iPhone-SE2
//        push.send(ClientType.IOS, "i_87E2C6FB4D2347F49FC17CD564B07AEE", sound);// HuangRongjun-iPhone

        // 发送给指定的设备类型
        // push.send(ClientType.IOS, null);
        // push.send(ClientType.Android, null);

        // 发送给所有的用户
        // push.send();
    }

}
