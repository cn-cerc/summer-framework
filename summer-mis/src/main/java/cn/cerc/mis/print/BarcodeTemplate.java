package cn.cerc.mis.print;

import java.io.IOException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BarcodeEAN;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

import cn.cerc.core.DataSet;

public class BarcodeTemplate extends PrintTemplate {

    private float fontSize;
    private float barHeight;

    public BarcodeTemplate() {
        this.setPageWidth(40);
        this.setPageHeight(30);
        this.setMarginTop(12);
        this.setMarginBottom(12);
        this.setMarginLeft(12);
        this.setMarginRight(12);
        fontSize = 8;
        barHeight = 24;
    }

    @Override
    public void output(Document document, PdfWriter writer) throws DocumentException, IOException {
        PdfContentByte cb = writer.getDirectContent();
        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        // 设置中文字体和字体样式
        Font f8 = new Font(bfChinese, fontSize, Font.NORMAL);
        DataSet dataSet = this.getDataSet();
        dataSet.first();
        while (dataSet.fetch()) {
            // 商品名称
            if (dataSet.getCurrent().hasValue("Name_"))
                document.add(new Paragraph(dataSet.getString("Name_"), f8));
            // 商品条码
            BarcodeEAN codeEAN = new BarcodeEAN();
            codeEAN.setBarHeight(barHeight);
            codeEAN.setCode(dataSet.getString("Code_"));
            document.add(codeEAN.createImageWithBarcode(cb, null, null));
            codeEAN.setGuardBars(false);
        }
    }

    public BarcodeTemplate add(String barcode, String descspec) {
        DataSet ds = this.getDataSet();
        if (ds == null) {
            ds = new DataSet();
            this.setDataSet(ds);
        }
        ds.append();
        ds.setField("Code_", barcode);
        ds.setField("Name_", descspec);
        return this;
    }

    public static void main(String[] args) {
        // Servlet sample:
        // ExportPdf export = new ExportPdf(getResponse());
        // export.setTemplateId("BarcodeEAN");
        // BarcodeTemplate bt = (BarcodeTemplate) export.getTemplate();
        // bt.add("2222222220013", "赠品切换条码");
        // bt.add("2222222220020", "会员切换条码");
        // bt.add("2222222220037", "单据结帐条码");
        // export.export();
    }

    public float getBarHeight() {
        return barHeight;
    }

    public void setBarHeight(float barHeight) {
        this.barHeight = barHeight;
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }
}
