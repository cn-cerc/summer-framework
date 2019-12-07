package cn.cerc.mis.sms;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JuheSMSTest {
    private static final Logger log = LoggerFactory.getLogger(JuheSMSTest.class);

    @Before
    public void setUp() throws Exception {
    }

    @Test
    @Ignore
    public void test() {
        JuheSMS sms = new JuheSMS("13510862605");
        String templateId = "50324";
        String templateValues = "#code#=222222";
        if (sms.sendByTemplateId(templateId, templateValues)) {
            log.info("ok:");
            log.info(sms.getMessage());
        } else {
            log.info("error: ");
            log.info(sms.getMessage());
        }
    }
}
