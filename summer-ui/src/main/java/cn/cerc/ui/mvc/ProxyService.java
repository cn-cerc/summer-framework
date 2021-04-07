package cn.cerc.ui.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import cn.cerc.core.ClassResource;
import cn.cerc.core.Utils;
import cn.cerc.mis.core.AbstractForm;
import cn.cerc.mis.core.BookHandle;
import cn.cerc.mis.core.IPage;
import cn.cerc.mis.core.JsonPage;
import cn.cerc.mis.core.LocalService;
import cn.cerc.ui.SummerUI;

@Component
public class ProxyService extends AbstractForm {
    private static final Logger log = LoggerFactory.getLogger(ProxyService.class);
    public static final String BEAN_ID = "proxyService";

    private static final ClassResource res = new ClassResource(ProxyService.class, SummerUI.ID);

    @Override
    public IPage execute() throws Exception {
        JsonPage jsonPage = new JsonPage(this);
        String service = getRequest().getParameter("service");
        if (Utils.isEmpty(service)) {
            return jsonPage.setResultMessage(false, String.format("%s 不允许为空", "service"));
        }
        log.info("url {}", getRequest().getRequestURL());

        String dataIn = getRequest().getParameter("dataIn");
        if (Utils.isEmpty(dataIn)) {
            return jsonPage.setResultMessage(false, String.format("%s 不允许为空", "dataIn"));
        }
        log.info("dataIn {}", dataIn);
        log.info("request corpNo {} ", this.getCorpNo());

        String[] uri = this.getRequest().getRequestURI().split("/");
        String curBookNo = uri[1];
        if (Utils.isEmpty(curBookNo)) { // 131001
            return jsonPage.setResultMessage(false, String.format("%s 不允许为空", "目标帐套"));
        }
        log.info("url corpNo {}", curBookNo);

        try {
            LocalService svr;
            if ("public".equals(curBookNo)) {
                svr = new LocalService(this, service);
            } else {
                if (curBookNo.equals(this.getCorpNo())) {
                    return jsonPage.setResultMessage(false, res.getString(2, "服务调用错误"));
                }

                BookHandle bHandle = new BookHandle(this, curBookNo);
                svr = new LocalService(bHandle, service);
            }
            svr.getDataIn().setJSON(dataIn);
            jsonPage.put("result", svr.exec());
            jsonPage.put("message", svr.getMessage());
            jsonPage.put("data", svr.getDataOut().toString());
            log.debug("response {}", new Gson().toJson(jsonPage.getItems()));
        } catch (Exception e) {
            jsonPage.setResultMessage(false, e.getMessage());
        }
        return jsonPage;
    }

}
