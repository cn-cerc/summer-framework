package cn.cerc.mis.print;

import cn.cerc.core.IHandle;
import cn.cerc.core.Record;
import cn.cerc.db.queue.QueueMode;
import cn.cerc.db.queue.QueueQuery;

public class PrintQueue {
    // 设置共享打印服务的设置记录之UID
    private String printerId = "";
    // 要打印的模版编号
    private String reportId = "";
    // 要打印的报表调用参数
    private String reportParams = "";
    // 要打印的份数
    private int reportNum = 1;
    // 打印机账号
    private String userCode;
    // 打印行高
    private double reportLineHeight = 1;
    // 报表抬头
    private String reportRptHead = "";

    public PrintQueue() {
    }

    public PrintQueue(String userCode) {
        this.userCode = userCode;
    }

    public void sendAliMessage(IHandle handle) {
        if ("".equals(printerId)) {
            throw new RuntimeException("PrinterId is null");
        }
        if ("".equals(reportId)) {
            throw new RuntimeException("ReportId is null");
        }
        if ("".equals(reportParams)) {
            throw new RuntimeException("ReportParams is null");
        }
        if (userCode == null || "".equals(userCode)) {
            throw new RuntimeException("用户代码不允许为空");
        }

        String queueCode = buildQueue();
        // 将消息发送至阿里云MNS
        QueueQuery query = new QueueQuery(handle);
        query.setQueueMode(QueueMode.append);
        query.add("select * from %s", queueCode);
        query.open();
        if (!query.isExistQueue()) {
            query.create(queueCode);
        }

        // 设置参数
        Record headIn = query.getHead();
        headIn.setJSON(reportParams);
        headIn.setField("_printerId_", printerId);
        headIn.setField("_reportId_", reportId);
        headIn.setField("_reportNum_", reportNum);
        headIn.setField("_reportLineHeight_", reportLineHeight);
        headIn.setField("_reportRptHead_", reportRptHead);
        query.save();
    }

    private String buildQueue() {
        return "print-" + userCode;
    }

    public String getPrinterId() {
        return printerId;
    }

    public void setPrinterId(String printerId) {
        this.printerId = printerId;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getReportParams() {
        return reportParams;
    }

    public void setReportParams(String reportParams) {
        this.reportParams = reportParams;
    }

    public int getReportNum() {
        return reportNum;
    }

    public void setReportNum(int reportNum) {
        this.reportNum = reportNum;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public double getReportLineHeight() {
        return reportLineHeight;
    }

    public void setReportLineHeight(double reportLineHeight) {
        this.reportLineHeight = reportLineHeight;
    }

    public String getReportRptHead() {
        return reportRptHead;
    }

    public void setReportRptHead(String reportRptHead) {
        this.reportRptHead = reportRptHead;
    }
}
