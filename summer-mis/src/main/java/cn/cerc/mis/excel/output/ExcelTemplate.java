package cn.cerc.mis.excel.output;

import cn.cerc.core.DataSet;
import cn.cerc.core.LanguageResource;
import cn.cerc.core.TDate;
import cn.cerc.core.TDateTime;
import cn.cerc.core.Utils;
import cn.cerc.db.oss.OssConnection;
import cn.cerc.mis.config.ApplicationConfig;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WriteException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

public class ExcelTemplate {
    private String fileName;
    private List<Column> columns;
    private IAccreditManager accreditManager;
    private HistoryWriter historyWriter;
    private DataSet dataSet;
    private final DateFormat df1 = new DateFormat("yyyy-MM-dd");
    private final DateFormat df2 = new DateFormat("yyyy-MM-dd HH:mm:ss");
    private int row = 0;
    private DecimalFormat decimalformat = new DecimalFormat(ApplicationConfig.getPattern());

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public void addColumn(Column column) {
        columns.add(column);
    }

    public IAccreditManager getAccreditManager() {
        return accreditManager;
    }

    public void setAccreditManager(IAccreditManager accredit) {
        this.accreditManager = accredit;
    }

    public HistoryWriter getHistoryWriter() {
        return historyWriter;
    }

    public void setHistoryWriter(HistoryWriter historyWriter) {
        this.historyWriter = historyWriter;
    }

    public void output(WritableSheet sheet) throws WriteException {
        boolean changeRowHeight = false;
        // 输出列头
        for (int col = 0; col < columns.size(); col++) {
            Column column = columns.get(col);
            // 若导出模板中包含图片列，则调整行高
            if (column instanceof ImageColumn) {
                changeRowHeight = true;
            }
            Label item = new Label(col, row, column.getName());
            sheet.addCell(item);
        }

        OssConnection oss = null;
        if (changeRowHeight) {
            oss = new OssConnection();
            // 先初始化一次
            oss.getClient();
        }
        // 输出列数据
        if (dataSet != null) {
            dataSet.first();
            // FIXME: 2021/3/11 目前仅台湾使用负数括号的千分位格式 -1502 显示为 (1,502)
            NumberFormat nf = new NumberFormat(ApplicationConfig.NEGATIVE_PATTERN_TW);
            WritableCellFormat wc = new WritableCellFormat(nf);
            while (dataSet.fetch()) {
                row++;
                for (int col = 0; col < columns.size(); col++) {
                    Column column = columns.get(col);
                    column.setRecord(dataSet.getCurrent());
                    // 650像素大约每一行占2.5行
                    if (changeRowHeight) {
                        sheet.setRowView(row, 650, false);
                    }
                    writeColumn(sheet, col, row, column, oss, wc);
                }
            }
        }
    }

    protected void writeColumn(WritableSheet sheet, int col, int row, Column column, OssConnection oss, WritableCellFormat wc) throws WriteException {
        if (column instanceof NumberColumn) {
            if (LanguageResource.isLanguageTW()) {
                Label item = new Label(col, row, decimalformat.format(column.getValue()));
                sheet.addCell(item);
            } else {
                jxl.write.Number item = new jxl.write.Number(col, row, (double) column.getValue());
                sheet.addCell(item);
            }
        } else if (column instanceof NumberFormatColumn) {
            jxl.write.Number item;
            if (wc != null) {
                item = new jxl.write.Number(col, row, (double) column.getValue(), wc);
            } else {
                item = new jxl.write.Number(col, row, (double) column.getValue());
            }
            sheet.addCell(item);
        } else if (column instanceof DateColumn) {
            Object value = column.getValue();
            if (value instanceof String) {
                Label item = new Label(col, row, value.toString());
                sheet.addCell(item);
            } else {
                TDate day = (TDate) column.getValue();
                DateTime item = new DateTime(col, row, day.getData(), new WritableCellFormat(df1));
                sheet.addCell(item);
            }
        } else if (column instanceof DateTimeColumn) {
            DateTime item = new DateTime(col, row, (Date) column.getValue(), new WritableCellFormat(df2));
            sheet.addCell(item);
        } else if (column instanceof ImageColumn) {
            if (oss != null && !Utils.isEmpty(column.getValue().toString())) {
                String imageUrl = column.getValue().toString();
                try {
                    // 截取https://ossBucket.ossSite后面的部分
                    if (imageUrl.startsWith("https://")) {
                        if (imageUrl.contains("com/")) {
                            imageUrl = imageUrl.substring(imageUrl.indexOf("com/") + 4);
                        } else if (imageUrl.contains("site/")) {
                            imageUrl = imageUrl.substring(imageUrl.indexOf("site/") + 5);
                        }
                    }
                    GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(oss.getBucket(), imageUrl);
                    // 设置失效时间
                    req.setExpiration(TDateTime.now().incMinute(5).getData());
                    // 压缩方式，长宽80，png格式
                    req.setProcess("image/resize,m_lfit,h_80,w_80/format,png");
                    InputStream inputStream = oss.getClient().generatePresignedUrl(req).openStream();
                    byte[] bytes = new byte[1024];
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    int n;
                    while ((n = inputStream.read(bytes)) != -1) {
                        output.write(bytes, 0, n);
                    }
                    WritableImage item = new WritableImage(col, row, 1, 1, output.toByteArray());
                    sheet.addImage(item);
                    inputStream.close();
                    output.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    // 图片解析错误默认输出空白，保证正常导出
                    Label item = new Label(col, row, "");
                    sheet.addCell(item);
                }
            } else {
                Label item = new Label(col, row, "");
                sheet.addCell(item);
            }
        } else {
            Label item = new Label(col, row, column.getValue().toString());
            sheet.addCell(item);
        }
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }
}
