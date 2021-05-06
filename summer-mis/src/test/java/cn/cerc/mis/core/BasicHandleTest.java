package cn.cerc.mis.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BasicHandleTest {

    @Test
    public void test() {
        Application.initOnlyFramework();
        try (BasicHandle handle1 = Application.getBean(BasicHandle.class);
                BasicHandle handle2 = Application.getBean(BasicHandle.class)) {
            assertEquals(handle1, handle2);
        }
    }

}
