package cn.cerc.ui.docs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.cerc.db.core.ServerConfig;

//@Controller
//@Scope(WebApplicationContext.SCOPE_REQUEST)
//@RequestMapping("/docs")
public class StartDocDefault {
    @Autowired
    private HttpServletRequest req;
    @Autowired
    private HttpServletResponse resp;

    @RequestMapping("/")
    @ResponseBody
    public String doGet() {
        return execute("index.html");
    }

    @RequestMapping("/{uri}")
    @ResponseBody
    public String doGet(@PathVariable String uri) {
        return execute(uri);
    }

    private String execute(String uri) {
        ServerConfig config = ServerConfig.getInstance();
        if (!"1".equals(config.getProperty("docs.service", "0"))) {
            outputHtml("sorry", "该功能暂不开放");
            return null;
        }
        if (uri.endsWith(".html")) {
            processHtml(uri);
        } else if (uri.endsWith(".md")) {
            processMD(uri);
        } else if (uri.endsWith(".png") || uri.endsWith(".jpg")) {
            processImage(uri);
        } else {
            try {
                resp.getOutputStream().print("unknow!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void processHtml(String uri) {
        MarkdownDoc mdm = new MarkdownDoc(req.getServletContext());
        mdm.setOutHtml(true);
        uri = uri.substring(0, uri.length() - 5) + ".md";

        String context = mdm.getContext(uri, "not found file: " + uri);
        String title = mdm.getFirstLine();
        if (title.startsWith("#")) {
            title = title.substring(title.indexOf(" "), title.length()).trim();
        }

        outputHtml(title, context);
    }

    private void processMD(String uri) {
        MarkdownDoc mdm = new MarkdownDoc(req.getServletContext());

        String context = mdm.getContext(uri, "not found file: " + uri);
        String title = mdm.getFirstLine();
        if (title.startsWith("#")) {
            title = title.substring(title.indexOf(" "), title.length()).trim();
        }

        outputMDFile(title, context);
    }

    private void processImage(String uri) {
        resp.setHeader("Content-Type", "image/jped");// 设置响应的媒体类型，这样浏览器会识别出响应的是图片
        InputStream fos = req.getServletContext().getResourceAsStream("/WEB-INF/" + uri);
        if (fos == null)
            return;
        try {
            OutputStream os = resp.getOutputStream();// 获得servlet的servletoutputstream对象
            byte[] buffer = new byte[2048];
            int count;
            while ((count = fos.read(buffer)) > 0) {
                os.write(buffer, 0, count);
            }
            fos.close();
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void outputMDFile(String title, String context) {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out;
        try {
            out = resp.getWriter();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void outputHtml(String title, String context) {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out;
        try {
            out = resp.getWriter();
            out.append("<!DOCTYPE html>\n");
            out.append("<html>\n");
            out.append("<head>\n");
            out.append(String.format("<title>%s</title>\n", title));
            out.append("</title>\n");
            out.append("</head>\n");
            out.append("<body>\n");
            out.append(context);
            out.append("</body>\n</html>");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

}
