package cn.cerc.ui.parts;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import cn.cerc.ui.core.HtmlContent;
import cn.cerc.ui.core.HtmlWriter;

public class UIContent extends UIComponent {
    private List<HtmlContent> contents = new ArrayList<>();
    private HttpServletRequest request;

    public UIContent(UIDocument owner) {
        super(owner);
    }

    public void append(HtmlContent content) {
        contents.add(content);
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<section role='content'>");
        super.output(html);
        // 输出追加过来的内容
        for (HtmlContent content : contents) {
            content.output(html);
        }
        html.println("</section>");
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
}
