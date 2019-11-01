package cn.cerc.db.jiguang;

import cn.cerc.db.core.StubHandle;
import org.junit.Before;
import org.junit.Test;

public class JiguangPushTest {
    private static final String sound = "trade_mall.wav";
    private StubHandle handle;

    @Before
    public void setUp() throws Exception {
        handle = new StubHandle();
    }

    @Test
    public void test() {
        // 初始化极光推送
        JiguangPush push = new JiguangPush(handle);

        // 消息标题，仅安卓机型有效，IOS设备忽略
        push.setTitle("消息推送测试");

        // 通知栏消息内容
        push.setMessage("这是系统向您发送的测试消息，如有打扰，请您忽略，谢谢！");

        // 附加的消息id
        push.setMsgId("3707");

        // 发送给指定的设备Id
        push.send(ClientType.Android, "n_868568025516789");// ly-Android
        push.send(ClientType.IOS, "i_77598CF689994EE3B110D6B20F1368B7");// HuangRongjun-iPhone
        push.send(ClientType.IOS, "i_77598CF689994EE3B110D6B20F1368B7", sound);// HuangRongjun-iPhone

        // 发送给指定的设备类型
        // push.send(ClientType.IOS, null);
        // push.send(ClientType.Android, null);

        // 发送给所有的用户
        // push.send();
    }

}
