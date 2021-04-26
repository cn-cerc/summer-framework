package cn.cerc.mis.custom;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import cn.cerc.core.ISession;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.SummerConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SummerConfiguration.class)
//@ContextConfiguration(locations = { "classpath:summer-mis-spring.xml" })
@WebAppConfiguration
public class SessionDefaultTest {
    @Autowired
    ISession handle;

    @Test
    public void test() {
        System.out.println(handle);
        System.out.println(Application.createSession());
        System.out.println(Application.createSession());
        System.out.println(Application.createSession());
    }

    public static void main(String[] args) {
        MockHttpServletRequest request = new MockHttpServletRequest();

        Application.initOnlyFramework();

        System.out.println(Application.createSession());
        System.out.println(Application.createSession());

        System.out.println(Application.createSession(request));
        System.out.println(Application.createSession(request));
    }
}
