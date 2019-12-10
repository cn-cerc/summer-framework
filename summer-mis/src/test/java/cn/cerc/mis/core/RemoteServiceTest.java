package cn.cerc.mis.core;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import cn.cerc.core.DataSet;
import cn.cerc.core.Record;
import cn.cerc.mis.client.RemoteService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RemoteServiceTest {

    @Test
    public void test() {
        // http://127.0.0.1/911001/proxyService?sid=f4761c4d332440f3859f8fb1bd19496a&service=TAppTranDE.search

        RemoteService svr = new RemoteService("911001", "TAppTranDE.search");
        svr.setToken("f4761c4d332440f3859f8fb1bd19496a");

        DataSet dataIn = svr.getDataIn();
        Record headIn = dataIn.getHead();
        headIn.setField("CusCorpNo_", "155174");
        headIn.setField("MaxRecord_", "10");

        log.info("{}", svr.getUrl());

        boolean result = svr.exec();
        assertTrue(svr.getMessage(), result);
    }
}
