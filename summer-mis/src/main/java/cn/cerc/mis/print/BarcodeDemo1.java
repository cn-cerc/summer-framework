package cn.cerc.mis.print;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import javax.imageio.ImageIO;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.BarcodeEAN;
import com.itextpdf.text.pdf.BarcodePDF417;

public class BarcodeDemo1 {
    private static final String codeString = "9780201615883X";

    public static void createBarcode128() throws IOException {
        Barcode128 code = new Barcode128();
        code.setCode(codeString);
        code.setAltText(codeString);
        Image codeImg = code.createAwtImage(Color.black, Color.white);
        BufferedImage img = new BufferedImage(450, 250, BufferedImage.TYPE_INT_RGB);
        // BufferedImage img = new BufferedImage(codeImg.getWidth(null),
        // codeImg.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, 500, 300);
        // g.fillRect(0,0,codeImg.getWidth(null), codeImg.getHeight(null));
        g.drawImage(codeImg, 25, 10, 400, 200, Color.white, null);
        // g.drawImage(codeImg, 0, 0, codeImg.getWidth(null),
        // codeImg.getHeight(null), Color.white, null);

        /**** 为图片添加条形码（文字），位置为条形码图片的下部居中 ****/
        Font font = new java.awt.Font("", java.awt.Font.PLAIN, 30);
        AttributedString ats = new AttributedString(codeString);
        ats.addAttribute(TextAttribute.FONT, font, 0, codeString.length());
        AttributedCharacterIterator iter = ats.getIterator();
        // 设置条形码（文字）的颜色为蓝色
        g.setColor(Color.BLUE);
        // 绘制条形码（文字）
        FontRenderContext fontRenderContext = ((Graphics2D) g).getFontRenderContext();
        int codeWidth = (int) font.getStringBounds(codeString, fontRenderContext).getWidth();
        g.drawString(iter, 25 + (400 - codeWidth) / 2, 235);
        /*************/
        OutputStream os = new BufferedOutputStream(new FileOutputStream("d:/code128.png"));
        ImageIO.write(img, "PNG", os);
    }

    public static void createPdf417() throws IOException {
        BarcodePDF417 pdf = new BarcodePDF417();
        pdf.setText(codeString);
        Image pdfImg = pdf.createAwtImage(Color.black, Color.white);
        BufferedImage img = new BufferedImage((int) pdfImg.getWidth(null), (int) pdfImg.getHeight(null),
                BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();
        g.drawImage(pdfImg, 0, 0, Color.white, null);
        OutputStream os = new BufferedOutputStream(new FileOutputStream("d:/pdf417.png"));
        ImageIO.write(img, "PNG", os);
    }

    public static void createPdfEan13() throws IOException {
        String barcode = "2222222220020";
        BarcodeEAN pdf = new BarcodeEAN();
        pdf.setCode(barcode);
        pdf.setAltText("altText");
        Image pdfImg = pdf.createAwtImage(Color.black, Color.white);
        BufferedImage img = new BufferedImage(pdfImg.getWidth(null), pdfImg.getHeight(null),
                BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();
        g.drawImage(pdfImg, 0, 0, Color.white, null);
        OutputStream os = new BufferedOutputStream(new FileOutputStream("d:/ean-" + barcode + ".png"));
        ImageIO.write(img, "PNG", os);
    }

    public static void main(String[] args) throws IOException, BadElementException {
        // createBarcode128();
        // createPdf417();
        createPdfEan13();
    }

}