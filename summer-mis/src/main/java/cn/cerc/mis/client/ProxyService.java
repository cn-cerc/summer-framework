package cn.cerc.mis.client;

import org.springframework.stereotype.Component;

import cn.cerc.core.Utils;
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
        if (Utils.isEmpty(service)) {
            return jsonPage.setResultMessage(false, "service 不允许为空");
        }

        String dataIn = getRequest().getParameter("dataIn");
        if (Utils.isEmpty(dataIn)) {
            return jsonPage.setResultMessage(false, "dataIn 不允许为空");
        }

        log.info("token User corpNo: " + this.getCorpNo());

        String[] uri = this.getRequest().getRequestURI().split("/");
        String curBookNo = uri[1];
        if (Utils.isEmpty(curBookNo)) {
            return jsonPage.setResultMessage(false, "目标帐套  不允许为空");
        }

        if (curBookNo.equals(this.getCorpNo())) {
            return jsonPage.setResultMessage(false, "服务调用错误");
        }

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

}
