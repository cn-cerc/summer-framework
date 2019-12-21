package cn.cerc.mis.print;

import java.io.IOException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

import cn.cerc.core.DataSet;

public class Barcode128Template extends PrintTemplate {
    private float fontSize;
    private float barHeight;

    public Barcode128Template() {
        this.setPageWidth(50);
        this.setPageHeight(30);
        this.setMarginTop(12);
        this.setMarginBottom(5);
        this.setMarginLeft(12);
        this.setMarginRight(12);
        fontSize = 8;
        barHeight = 30;
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
            // 条码信息
            Barcode128 code128 = new Barcode128();
            code128.setBarHeight(barHeight);
            String code = dataSet.getString("Code_");
            code128.setCode(code);

            // 反算条码宽度
            int length = code.length();
            float x = 125 / ((length + 2) * 11 + 2f);
            code128.setX(x);

            document.add(code128.createImageWithBarcode(cb, null, null));

            // 描述信息
            Paragraph paragraph = new Paragraph(dataSet.getString("Name_"), f8);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
        }
    }

    public Barcode128Template add(String barcode, String description) {
        DataSet dataSet = this.getDataSet();
        if (dataSet == null) {
            dataSet = new DataSet();
            this.setDataSet(dataSet);
        }
        dataSet.append();
        dataSet.setField("Code_", barcode);
        dataSet.setField("Name_", description);
        return this;
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
