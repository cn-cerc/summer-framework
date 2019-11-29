package cn.cerc.mis.print;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

public class ExportPdf {
    private static ApplicationContext app;
    private static String xmlFile = "classpath:export-pdf.xml";
    private HttpServletResponse response;
    private String templateId;
    private PrintTemplate template;

    public ExportPdf(HttpServletResponse response) {
        this.response = response;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public PrintTemplate getTemplate() {
        if (template == null) {
            if (templateId == null)
                throw new RuntimeException("templateId is null");
            if (app == null)
                app = new FileSystemXmlApplicationContext(xmlFile);
            template = app.getBean(templateId, PrintTemplate.class);
        }
        return template;
    }

    public void setTemplate(PrintTemplate template) {
        this.template = template;
    }

    public void export() throws IOException, DocumentException {
        PrintTemplate template = this.getTemplate();

        // 清空输出流
        response.reset();
        if ("file".equals(template.getOutputDevice())) {
            response.setCharacterEncoding("UTF-8");// 设置相应内容的编码格式
            String fname = new String(template.getFileName().getBytes(), "ISO-8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fname + ".pdf");
            response.setContentType("application/pdf");// 定义输出类型
        }

        // 第一步
        Document doc = template.getDocument();

        // 第二步
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(doc, pdfStream);

        // 第三步
        if (template.getHeaderFooter() != null) {
            writer.setPageEvent(template.getHeaderFooter());
        } else if (template.getHeader() != null) {
            ReportHeaderFooter headerFooter = new ReportHeaderFooter();
            headerFooter.setHeader(template.getHeader());
            writer.setBoxSize("art", doc.getPageSize());
            writer.setPageEvent(headerFooter);
        }
        doc.open();

        // 第四步
        doc.addAuthor("地藤系统");
        doc.addSubject("地藤系统报表文件");
        doc.addCreationDate();
        template.output(doc, writer);

        // 设置是否自动显示打印对话框
        if ("printer".equals(template.getOutputDevice())) {
            // writer.addJavaScript("this.print(false);", false);
            writer.addJavaScript("this.print({bUI: true, bSilent: true,bShrinkToFit:true});", false);
            // document.add(new Chunk("Silent Auto Print"));
        }

        // 第五步
        doc.close();

        // 第六步
        response.setContentType("application/pdf");
        response.setContentLength(pdfStream.size());

        ServletOutputStream out = response.getOutputStream();
        pdfStream.writeTo(out);
        out.flush();
        response.flushBuffer();
    }

    public void export(String message) throws DocumentException, IOException {
        response.reset();// 清空输出流

        // 第一步
        Document document = new Document(PageSize.A4.rotate());

        // 第二步
        // PdfWriter.getInstance(pdf, new FileOutputStream("Hello.pdf"));
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();

        // 第三步
        document.open();

        // 第四步
        document.addAuthor("地藤系统");
        document.addSubject("地藤系统报表文件");
        document.addCreationDate();
        document.add(new Chunk(message));

        // 第五步
        document.close();

        // 第六步
        response.setContentType("application/pdf");
        response.setContentLength(pdfStream.size());

        ServletOutputStream out = response.getOutputStream();
        pdfStream.writeTo(out);
        out.flush();
        response.flushBuffer();
    }

}