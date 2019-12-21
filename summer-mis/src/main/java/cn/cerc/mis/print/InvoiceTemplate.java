package cn.cerc.mis.print;

import java.io.IOException;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import cn.cerc.core.Record;
import cn.cerc.mis.excel.output.Column;

public class InvoiceTemplate extends PrintTemplate {
    public InvoiceTemplate() {
        this.setPageWidth(58);
        this.setMarginTop(12);
        this.setMarginBottom(12);
        this.setMarginLeft(12);
        this.setMarginRight(12);
    }

    @Override
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
        PdfPTable pdfTable = new PdfPTable(2);

        // 设置报表为无边框
        pdfTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

        // 设置表格占PDF文档100%宽度
        pdfTable.setWidthPercentage(100);

        // 水平方向表格控件左对齐
        pdfTable.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        // 创建一个表格的表头单元格
        PdfPCell pdfTableHeaderCell = new PdfPCell();

        // 设置表格的表头单元格颜色
        pdfTableHeaderCell.setBackgroundColor(new BaseColor(240, 240, 240));
        pdfTableHeaderCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);

        // 设置表头栏位
        pdfTableHeaderCell.setPhrase(new Paragraph("名称", f8));
        pdfTable.addCell(pdfTableHeaderCell);
        pdfTableHeaderCell.setPhrase(new Paragraph("信息", f8));
        pdfTable.addCell(pdfTableHeaderCell);

        // 创建一个表格的正文内容单元格
        PdfPCell pdfTableContentCell_1 = new PdfPCell();
        pdfTableContentCell_1.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        pdfTableContentCell_1.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);

        PdfPCell pdfTableContentCell_2 = new PdfPCell();
        pdfTableContentCell_2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        pdfTableContentCell_2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);

        // 表格内容行数的填充
        dataSet.first();
        while (dataSet.fetch()) {
            Record record = dataSet.getCurrent();
            for (Column column : this.getColumns()) {
                pdfTableContentCell_1.setPhrase(new Phrase(column.getName(), f8));
                pdfTable.addCell(pdfTableContentCell_1);

                String field = column.getCode();
                pdfTableContentCell_2.setPhrase(new Paragraph(record.getString(field), f8));
                pdfTable.addCell(pdfTableContentCell_2);
            }
        }
        document.add(pdfTable);
    }

}
