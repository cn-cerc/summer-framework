package cn.cerc.mis.tools;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import cn.cerc.core.ClassResource;
import cn.cerc.core.DataSet;
import cn.cerc.core.Utils;
import cn.cerc.db.core.IHandle;
import cn.cerc.mis.SummerMIS;
import cn.cerc.mis.core.AbstractForm;
import cn.cerc.mis.core.SystemBuffer;
import cn.cerc.mis.core.SystemBufferType;
import cn.cerc.mis.excel.output.AccreditException;
import cn.cerc.mis.excel.output.ExportExcel;
import cn.cerc.mis.other.MemoryBuffer;
import cn.cerc.vine.core.PartnerService;
import jxl.write.WriteException;

public class ExportService extends ExportExcel {
    private static final ClassResource res = new ClassResource(ExportService.class, SummerMIS.ID);

    private String service;
    private String exportKey;
    private String corpNo;

    public String getCorpNo() {
        return corpNo;
    }

    public void setCorpNo(String corpNo) {
        this.corpNo = corpNo;
    }

    public ExportService(AbstractForm owner) {
        super(owner.getResponse());
        this.setHandle(owner);
        HttpServletRequest request = owner.getRequest();
        service = request.getParameter("service");
        exportKey = request.getParameter("exportKey");
    }

    @Override
    public void export() throws WriteException, IOException, AccreditException {
        if (service == null || "".equals(service)) {
            throw new RuntimeException(String.format(res.getString(1, "错误的调用：%s"), "service is null"));
        }
        if (exportKey == null || "".equals(exportKey)) {
            throw new RuntimeException(String.format(res.getString(1, "错误的调用：%s"), "exportKey is null"));
        }

        IHandle handle = (IHandle) this.getHandle();
        if (Utils.isEmpty(this.corpNo)) {
            this.corpNo = handle.getCorpNo();
        }
        PartnerService app = new PartnerService(handle);
        app.setCorpNo(this.corpNo);
        app.setService(service);
        try (MemoryBuffer buff = new MemoryBuffer(SystemBuffer.User.ExportKey, handle.getUserCode(), exportKey)) {
            app.getDataIn().close();
            app.getDataIn().setJSON(buff.getString("data"));
        }
        if (!app.exec()) {
            this.export(app.getMessage());
            return;
        }

        DataSet dataOut = app.getDataOut();
        // 对分类进行处理
        dataOut.first();
        while (dataOut.fetch()) {
            if (dataOut.getBoolean("IsType_")) {
                dataOut.delete();
            }
        }
        this.getTemplate().setDataSet(dataOut);
        super.export();
    }
}
