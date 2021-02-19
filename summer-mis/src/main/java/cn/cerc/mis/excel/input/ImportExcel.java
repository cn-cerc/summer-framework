package cn.cerc.mis.excel.input;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import cn.cerc.core.DataSet;
import cn.cerc.core.Record;
import cn.cerc.core.Utils;
import jxl.Cell;
import jxl.CellType;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ImportExcel extends ImportExcelFile {
    private static ApplicationContext app;
    private static String xmlFile = "classpath:import-excel.xml";
    private HttpServletResponse response;
    private String templateId;
    private ImportExcelTemplate template;
    private ImportError errorHandle;
    private ImportRecord readHandle;

    public ImportExcel(HttpServletRequest request, HttpServletResponse response) {
        super();
        this.setRequest(request);
        this.response = response;
    }

    public void exportTemplate() throws IOException, WriteException {
        DataSet dataOut = getDataSet();
        this.setResponse(response);
        OutputStream os = response.getOutputStream();// 取得输出流
        response.reset();// 清空输出流

        template = this.getTemplate();
        // 下面是对中文文件名的处理
        response.setCharacterEncoding("UTF-8");// 设置相应内容的编码格式
        String fname = URLEncoder.encode(template.getFileName(), "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fname + ".xls");
        response.setContentType("application/msexcel");// 定义输出类型

        List<ImportColumn> columns = template.getColumns();

        // 创建工作薄
        WritableWorkbook workbook = Workbook.createWorkbook(os);

        // 创建新的一页
        WritableSheet sheet = workbook.createSheet("First Sheet", 0);

        // 输出列头
        int row = 0;
        for (int col = 0; col < columns.size(); col++) {
            ImportColumn column = columns.get(col);
            Label item = new Label(col, row, column.getName());
            sheet.addCell(item);
        }

        // 输出列数据
        if (dataOut != null) {
            dataOut.first();
            while (dataOut.fetch()) {
                row++;
                for (int col = 0; col < columns.size(); col++) {
                    ImportColumn column = columns.get(col);
                    column.setRecord(dataOut.getCurrent());
                    if (column instanceof ImportNumberColumn) {
                        jxl.write.Number item = new jxl.write.Number(col, row, (double) column.getValue());
                        sheet.addCell(item);
                    } else {
                        Label item = new Label(col, row, (String) column.getValue());
                        sheet.addCell(item);
                    }
                }
            }
        }

        // 把创建的内容写入到输出流中，并关闭输出流
        workbook.write();
        workbook.close();
        os.close();
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public ImportExcelTemplate getTemplate() {
        if (template == null) {
            if (templateId == null)
                throw new RuntimeException("templateId is null");
            if (app == null)
                app = new FileSystemXmlApplicationContext(xmlFile);
            template = app.getBean(templateId, ImportExcelTemplate.class);
        }
        return template;
    }

    public void setTemplate(ImportExcelTemplate template) {

        this.template = template;
    }

    public String getTemplateId() {
        return templateId;
    }

    public ImportExcel setTemplateId(String templateId) {
        this.templateId = templateId;
        return this;
    }

    public DataSet readFileData(Record record) throws Exception {
        FileItem file = this.getFile(record);
        // 获取Excel文件对象
        Workbook rwb = Workbook.getWorkbook(file.getInputStream());
        // 获取文件的指定工作表 默认的第一个
        Sheet sheet = rwb.getSheet(0);

        ImportExcelTemplate template = this.getTemplate();
        if (template.getColumns().size() != sheet.getColumns())
            throw new RuntimeException(String.format("导入的文件：<b>%s</b>, 其总列数为 %d，而模版总列数为  %d 二者不一致，无法导入！",
                    file.getName(), sheet.getColumns(), template.getColumns().size()));

        DataSet ds = new DataSet();
        for (int row = 0; row < sheet.getRows(); row++) {
            if (row == 0) {
                for (int col = 0; col < sheet.getColumns(); col++) {
                    Cell cell = sheet.getCell(col, row);
                    String value = cell.getContents();
                    String title = template.getColumns().get(col).getName();
                    if (!title.equals(value))
                        throw new RuntimeException(
                                String.format("导入的文件：<b>%s</b>，其标题第 %d 列为【 %s】, 模版中为【%s】，二者不一致，无法导入！", file.getName(),
                                        col + 1, value, title));
                }
            } else {
                ds.append();
                for (int col = 0; col < sheet.getColumns(); col++) {
                    Cell cell = sheet.getCell(col, row);
                    String value = cell.getContents();
                    if (cell.getType() == CellType.NUMBER) {
                        NumberCell numberCell = (NumberCell) cell;
                        double d = numberCell.getValue();
                        value = Utils.formatFloat("0.######", d);
                    }
                    ImportColumn column = template.getColumns().get(col);
                    if (!column.validate(row, col, value)) {
                        ColumnValidateException err = new ColumnValidateException("其数据不符合模版要求，当前值为：" + value);
                        err.setTitle(column.getName());
                        err.setValue(value);
                        err.setCol(col);
                        err.setRow(row);
                        if (errorHandle == null || !errorHandle.process(err))
                            throw err;
                    }
                    ds.setField(column.getCode(), value);
                }
                if (readHandle != null && !readHandle.process(ds.getCurrent()))
                    break;
            }
        }
        return ds;
    }

    public ImportError getErrorHandle() {
        return errorHandle;
    }

    public void setErrorHandle(ImportError errorHandle) throws Exception {
        this.errorHandle = errorHandle;
    }

    public ImportRecord getReadHandle() {
        return readHandle;
    }

    public void setReadHandle(ImportRecord readHandle) {
        this.readHandle = readHandle;
    }

    public void readRecords(ImportRecord readHandle) throws Exception {
        this.setReadHandle(readHandle);
        DataSet ds = getDataSet();
        while (ds.fetch()) {
            readFileData(ds.getCurrent());
        }
    }
}
