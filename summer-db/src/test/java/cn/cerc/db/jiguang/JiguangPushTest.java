package cn.cerc.db.jiguang;

import cn.cerc.core.TDateTime;
import cn.cerc.db.core.StubHandleText;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class JiguangPushTest {
    private static final String sound = "trade_mall.wav";
    private StubHandleText handle;

    @Before
    public void setUp() throws Exception {
        handle = new StubHandleText();
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
        push.send(ClientType.Android, "n_e201ff3670a4174b");// itjun-xiaomi-mix2
        push.send(ClientType.Android, "n_a86ddf4df465a83f");// weibo-xiaomi-5c
        push.send(ClientType.IOS, "i_0C005500702F4F97AD81C2E992E36108");// ly-iPhone-SE2
        push.send(ClientType.IOS, "i_A1688A8910B04499ABC64B035B559921");// wsg-iPhone-xr
        push.send(ClientType.IOS, "i_87E2C6FB4D2347F49FC17CD564B07AEE");// itjun-iPhone-SE2
//        push.send(ClientType.IOS, "i_87E2C6FB4D2347F49FC17CD564B07AEE", sound);// HuangRongjun-iPhone

        // 发送给指定的设备类型
        // push.send(ClientType.IOS, null);
        // push.send(ClientType.Android, null);

        // 发送给所有的用户
        // push.send();
    }

}
