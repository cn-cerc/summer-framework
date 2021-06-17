package cn.cerc.mis.core;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.cerc.core.DataSet;

public class RemoteServiceTest {
    private static final Logger log = LoggerFactory.getLogger(RemoteServiceTest.class);

    @Test
    public void test() {
        // http://127.0.0.1/911001/proxyService?sid=f4761c4d332440f3859f8fb1bd19496a&service=TAppTranDE.search

//        RemoteService svr = new RemoteService("000000", "ApiUserInfo.getUserInfo");
//        svr.setToken("fe7795f9d65740f4b520c6e5cd842786");
//
//        DataSet dataIn = svr.getDataIn();
//        Record headIn = dataIn.getHead();
//        headIn.setField("UserCode_", "91100124");
//
//        log.info("{}", svr.getUrl());
//        boolean result = svr.exec();
//
//        log.info("{}", svr.getDataOut().toString());
//        assertTrue(svr.getMessage(), result);
    }

    @Test
    public void test_dataOut() throws JsonProcessingException {
        String response = "{\"result\":true,\"dataOut\":\"{\\\"head\\\":{\\\"_message_\\\":\\\"\\\",\\\"_result_\\\":true},\\\"dataset\\\":[[\\\"TBNo_\\\",\\\"TBDate_\\\",\\\"CusCode_\\\",\\\"TOriAmount_\\\",\\\"ManageNo_\\\",\\\"Remark_\\\",\\\"Status_\\\",\\\"Final_\\\",\\\"Process_\\\",\\\"UpdateUser_\\\",\\\"UpdateDate_\\\",\\\"AppUser_\\\",\\\"AppDate_\\\",\\\"AppName_\\\",\\\"UpdateName_\\\",\\\"CorpName_\\\"],[\\\"DE161101001-155174\\\",\\\"2016-11-01 00:00:00\\\",\\\"C00243\\\",43.5,\\\"{}\\\",\\\"{}\\\",-1,false,0,\\\"91100124\\\",\\\"2016-11-02 09:27:53\\\",\\\"91100124\\\",\\\"2016-11-01 15:19:36\\\",\\\"AppUser_\\\",\\\"UpdateUser_\\\",\\\"中国渔具\\\"],[\\\"DE161102002-155174\\\",\\\"2016-11-03 00:00:00\\\",\\\"C00243\\\",36.0,\\\"\\\",\\\"\\\",1,true,0,\\\"91100124\\\",\\\"2016-11-03 17:32:11\\\",\\\"91100124\\\",\\\"2016-11-02 14:55:32\\\",\\\"AppUser_\\\",\\\"UpdateUser_\\\",\\\"中国渔具\\\"],[\\\"DE161102003-155174\\\",\\\"2016-11-02 00:00:00\\\",\\\"C00243\\\",30.0,\\\"\\\",\\\"\\\",1,true,0,\\\"15517401\\\",\\\"2016-11-04 14:10:01\\\",\\\"91100124\\\",\\\"2016-11-02 15:25:34\\\",\\\"AppUser_\\\",\\\"UpdateUser_\\\",\\\"中国渔具\\\"]]}\",\"message\":\"\"}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(response);
        String dataJson = json.get("dataOut").asText();
        DataSet dataOut = new DataSet();
        if (dataJson != null) {
            dataOut.setJSON(dataJson);
        }
        log.info("{}", dataOut.size());

        while (dataOut.fetch()) {
            log.info("{}", dataOut.getCurrent());
        }
    }

    public static void main(String[] args) {
        DataSet dataIn = new DataSet();
        dataIn.getHead().setField("CusCorpNo_", "155174");
        dataIn.getHead().setField("MaxRecord_", "3");
        System.out.println(dataIn.getJSON());
    }

}
