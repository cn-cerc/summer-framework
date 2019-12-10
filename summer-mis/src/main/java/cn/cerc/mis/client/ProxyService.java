package cn.cerc.mis.client;

import org.springframework.stereotype.Component;

import cn.cerc.core.DataSet;
import cn.cerc.mis.core.AbstractForm;
import cn.cerc.mis.core.BookHandle;
import cn.cerc.mis.core.IPage;
import cn.cerc.mis.core.LocalService;
import cn.cerc.mis.page.JsonPage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProxyService extends AbstractForm {

    @Override
    public IPage execute() throws Exception {
        JsonPage jsonPage = new JsonPage(this);
        String service = getRequest().getParameter("service");
        String dataIn = getRequest().getParameter("dataIn");

        log.info("token User corpNo: " + this.getCorpNo());

        String[] uri = this.getRequest().getRequestURI().split("/");
        String curBookNo = uri[1];
        // /911001/proxyService
        
        BookHandle bHandle = new BookHandle(this, curBookNo);
        try {
            LocalService svr = new LocalService(bHandle, service);
            svr.getDataIn().setJSON(dataIn);
            jsonPage.put("result", svr.exec());
            jsonPage.put("message", svr.getMessage());
            jsonPage.put("dataOut", svr.getDataOut().toString());
        } catch (Exception e) {
            jsonPage.setResultMessage(false, e.getMessage());
        }
        return jsonPage;
    }

    public static void main(String[] args) {
        DataSet dataIn = new DataSet();
        dataIn.getHead().setField("CusCorpNo_", "911001");
        System.out.println(dataIn.getJSON());
    }
    
}
