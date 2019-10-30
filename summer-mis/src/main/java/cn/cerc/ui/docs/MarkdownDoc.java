package cn.cerc.ui.docs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletContext;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.options.MutableDataSet;

import cn.cerc.mis.core.IForm;

public class MarkdownDoc {
    private ServletContext servletContext;
    // 原样输出还是html输出
    private boolean outHtml = false;
    // 文件内容
    private String firstLine = null;

    public MarkdownDoc() {
    }

    public MarkdownDoc(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public MarkdownDoc(IForm form) {
        this.servletContext = form.getRequest().getServletContext();
    }

    /**
     * @param fileName
     *            带绝对路径的文件名
     * @param def
     *            默认文件路径
     * @return 返回文件内容，若文件不存在，则返回null
     */
    public String getContext(String fileName, String def) {
        InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/" + fileName);
        String context;
        try {
            context = inputStream != null ? asString(inputStream) : def;
        } catch (IOException e) {
            context = e.getMessage();
        }
        return outHtml ? mdToHtml(context) : context;
    }

    /**
     * @param inputText
     *            传入的md字符串
     * @return 返回经转化后的html
     */
    public String mdToHtml(String inputText) {
        MutableDataSet options = new MutableDataSet();
        // 使用github的markdown扩展语法
        options.setFrom(ParserEmulationProfile.GITHUB_DOC);
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        Node document = parser.parse(inputText);
        return renderer.render(document);
    }

    private String asString(InputStream in) throws IOException {
        firstLine = null;
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line + System.lineSeparator());
                if (firstLine == null)
                    firstLine = line;
            }
        }
        return builder.toString();
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public boolean isOutHtml() {
        return outHtml;
    }

    public void setOutHtml(boolean outHtml) {
        this.outHtml = outHtml;
    }

    public String getFirstLine() {
        return firstLine;
    }
}