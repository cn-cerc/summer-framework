package cn.cerc.mis.print;

import java.io.IOException;
import java.util.List;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import cn.cerc.core.DataSet;
import cn.cerc.core.Record;
import cn.cerc.mis.excel.output.Column;

public class PrintTemplate {
    private String fileName;
    private String header;
    private ReportHeaderFooter headerFooter;
    private List<Column> columns;
    private int pageWidth = 210; // 默认为 A4 纸的宽度，单位为 mm
    private int pageHeight = 297; // 默认为 A4 纸的高度，单位为 mm
    protected DataSet dataSet;
    protected Document document;
    // 设置页边距
    private float marginLeft = 36;
    private float marginRight = 36;
    private float marginTop = 36;
    private float marginBottom = 36;
    private boolean broadwise = false; // 是否横向打印
    /**
     * 定义输出设备，默认为屏幕
     */
    private String outputDevice = "screen";

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

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    public void output(Document document, PdfWriter writer) throws DocumentException, IOException {
        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        // 设置中文字体和字体样式
        Font f8 = new Font(bfChinese, 8, Font.NORMAL);
        Font f18 = new Font(bfChinese, 18, Font.NORMAL);
        document.addTitle(this.getFileName());
        // 页标题
        Paragraph title = new Paragraph(this.getFileName(), f18);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        // 空一行
        document.add(new Paragraph(" ", f18));

        // 创建一个N列的表格控件
        PdfPTable pdfTable = new PdfPTable(this.getColumns().size());
        // 设置表格占PDF文档100%宽度
        pdfTable.setWidthPercentage(100);
        // 水平方向表格控件左对齐
        pdfTable.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
        // 创建一个表格的表头单元格
        PdfPCell pdfTableHeaderCell = new PdfPCell();
        // 设置表格的表头单元格颜色
        pdfTableHeaderCell.setBackgroundColor(new BaseColor(240, 240, 240));
        pdfTableHeaderCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        for (Column column : this.getColumns()) {
            Paragraph item = new Paragraph(column.getName(), f8);
            pdfTableHeaderCell.setPhrase(item);
            pdfTable.addCell(pdfTableHeaderCell);
        }

        // 创建一个表格的正文内容单元格
        PdfPCell pdfTableContentCell = new PdfPCell();
        pdfTableContentCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        pdfTableContentCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        // 表格内容行数的填充
        dataSet.first();
        while (dataSet.fetch()) {
            Record record = dataSet.getCurrent();
            for (Column column : this.getColumns()) {
                String field = column.getCode();
                pdfTableContentCell.setPhrase(new Paragraph(record.getString(field), f8));
                pdfTable.addCell(pdfTableContentCell);
            }
        }
        document.add(pdfTable);

        // //将表格添加到新的文档
        // doc.add(table);
        // //创建新的一页
        // doc.newPage();
        // //添加图片
        // Image image = Image.getInstance(
        // "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png");
        // //添加到文档
        // doc.add(image);
        // //设置对象方式
        // image.setAlignment(Element.ALIGN_CENTER);
    }

    public Document getDocument() {
        if (document == null) {
            // document = new Document(PageSize.A4.rotate());
            // 分辨率是72像素/英寸，A4纸的尺寸的图像的像素是595×842
            Rectangle rectangle = createRectangle(pageWidth, pageHeight);
            if (broadwise) {
                rectangle = rectangle.rotate();
            }
            document = new Document(rectangle, marginLeft, marginRight, marginTop,
                    marginBottom);
        }
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    // 返回纸张尺寸， 以毫米为单位，定义宽度与高度
    protected Rectangle createRectangle(int width, int height) {
        double x = width * 72 / 25.4;
        double y = height * 72 / 25.4;
        return new Rectangle((int) x, (int) y);
    }

    public int getPageHeight() {
        return pageHeight;
    }

    public void setPageHeight(int height) {
        this.pageHeight = height;
    }

    public int getPageWidth() {
        return pageWidth;
    }

    public void setPageWidth(int pageWidth) {
        this.pageWidth = pageWidth;
    }

    public float getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(float marginLeft) {
        this.marginLeft = marginLeft;
    }

    public float getMarginRight() {
        return marginRight;
    }

    public void setMarginRight(float marginRight) {
        this.marginRight = marginRight;
    }

    public float getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(float marginTop) {
        this.marginTop = marginTop;
    }

    public float getMarginBottom() {
        return marginBottom;
    }

    public void setMarginBottom(float marginBottom) {
        this.marginBottom = marginBottom;
    }

    public String getOutputDevice() {
        return outputDevice;
    }

    public void setOutputDevice(String outputDevice) {
        if (null == outputDevice || "".equals(outputDevice))
            throw new RuntimeException("输出设备不允许为空！");
        if ("screen".equals(outputDevice) || "printer".equals(outputDevice) || "file".equals(outputDevice))
            this.outputDevice = outputDevice;
        else
            throw new RuntimeException("输出设备只能为 screen(默认)、printer、file三者之一!");
    }

    public ReportHeaderFooter getHeaderFooter() {
        return headerFooter;
    }

    public void setHeaderFooter(ReportHeaderFooter headerFooter) {
        this.headerFooter = headerFooter;
    }

    public boolean isBroadwise() {
        return broadwise;
    }

    public void setBroadwise(boolean broadwise) {
        this.broadwise = broadwise;
    }

}
