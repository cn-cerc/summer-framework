package cn.cerc.mis.client;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;

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
        log.info("url {}", getRequest().getRequestURL());

        String dataIn = getRequest().getParameter("dataIn");
        if (Utils.isEmpty(dataIn)) {
            return jsonPage.setResultMessage(false, "dataIn 不允许为空");
        }
        log.info("请求者帐套 {} ", this.getCorpNo());

        String[] uri = this.getRequest().getRequestURI().split("/");
        String curBookNo = uri[1];
        if (Utils.isEmpty(curBookNo)) { // 131001
            return jsonPage.setResultMessage(false, "目标帐套 不允许为空");
        }
        log.info("响应者帐套 {}", curBookNo);

        try {
            LocalService svr;
            if ("public".equals(curBookNo)) {
                svr = new LocalService(this, service);
            } else {
                if (curBookNo.equals(this.getCorpNo())) {
                    return jsonPage.setResultMessage(false, "服务调用错误");
                }

                // 若直接传递用户信息，强制刷新当前服务用户代码
                BookHandle bHandle = new BookHandle(this, curBookNo);
                String userCode = getRequest().getParameter("userCode");
                if (Utils.isNotEmpty(userCode)) {
                    bHandle.setUserCode(userCode);
                }
                svr = new LocalService(bHandle, service);
            }
            svr.getDataIn().setJSON(dataIn);
            jsonPage.put("result", svr.exec());
            jsonPage.put("message", svr.getMessage());
            jsonPage.put("data", svr.getDataOut().toString());
            log.debug("json:{}", new Gson().toJson(jsonPage.getItems()));
        } catch (Exception e) {
            jsonPage.setResultMessage(false, e.getMessage());
        }
        return jsonPage;
    }

}
