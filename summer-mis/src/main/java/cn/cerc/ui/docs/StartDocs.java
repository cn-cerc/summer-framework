package cn.cerc.ui.docs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.cerc.db.core.ServerConfig;

@Deprecated // 请改使用 StartDocDefault
public class StartDocs extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public StartDocs() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!"1".equals(ServerConfig.getInstance().getProperty("docs.service", "0"))) {
            throw new RuntimeException("该功能暂不开放");
        }
        String uri = req.getRequestURI();
        if ("/docs".equals(uri)) {
            uri += "/index.html";
        } else if ("/docs/".equals(uri)) {
            uri += "index.html";
        }

        if (uri.endsWith(".html")) {
            processHtml(req, resp, uri);
        } else if (uri.endsWith(".md")) {
            processMD(req, resp, uri);
        } else if (uri.endsWith(".png") || uri.endsWith(".jpg")) {
            processImage(req, resp, uri);
        } else {
            resp.getOutputStream().print("unknow!");
        }
    }

    private void processHtml(HttpServletRequest req, HttpServletResponse resp, String uri) throws IOException {
        MarkdownDoc mdm = new MarkdownDoc(req.getServletContext());
        mdm.setOutHtml(true);
        uri = uri.substring(0, uri.length() - 5) + ".md";

        String context = mdm.getContext(uri, "not found file: " + uri);
        String title = mdm.getFirstLine();
        if (title.startsWith("#")) {
            title = title.substring(title.indexOf(" "), title.length()).trim();
        }

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.append("<!DOCTYPE html>\n");
        out.append("<html>\n");
        out.append("<head>\n");
        out.append(String.format("<title>%s</title>\n", title));
        out.append("</title>\n");
        out.append("</head>\n");
        out.append("<body>\n");
        out.append(context);
        out.append("</body>\n</html>");
    }

    private void processMD(HttpServletRequest req, HttpServletResponse resp, String uri) throws IOException {
        MarkdownDoc mdm = new MarkdownDoc(req.getServletContext());

        String context = mdm.getContext(uri, "not found file: " + uri);
        String title = mdm.getFirstLine();
        if (title.startsWith("#")) {
            title = title.substring(title.indexOf(" "), title.length()).trim();
        }

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.append("<!DOCTYPE html>\n");
        out.append("<html>\n");
        out.append("<head>\n");
        out.append(String.format("<title>%s</title>\n", title));
        out.append("</title>\n");
        out.append("</head>\n");
        out.append("<body>\n");
        out.append("<pre>\n");
        out.append(context);
        out.append("\n</pre>\n");
        out.append("</body>\n</html>");
    }

    private void processImage(HttpServletRequest req, HttpServletResponse resp, String uri) throws IOException {
        resp.setHeader("Content-Type", "image/jped");// 设置响应的媒体类型，这样浏览器会识别出响应的是图片
        InputStream fos = req.getServletContext().getResourceAsStream("/WEB-INF/" + uri);
        if (fos == null)
            return;
        OutputStream os = resp.getOutputStream();// 获得servlet的servletoutputstream对象
        byte[] buffer = new byte[2048];
        int count;
        while ((count = fos.read(buffer)) > 0) {
            os.write(buffer, 0, count);
        }
        fos.close();
        os.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    public static void main(String[] args) {
        String path = StartDocs.class.getClass().getResource("/").getPath();
        System.out.println(path);
    }
}
