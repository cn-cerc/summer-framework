package cn.cerc.ui.parts;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.other.UrlMenu;

public class UISheetHelp extends UISheet {
    private String content;
    private UrlMenu operaUrl;
    private List<String> lines = new ArrayList<>();

    @Deprecated
    public UISheetHelp() {
        super();
        this.setCaption("操作提示");
    }

    public UISheetHelp(UIToolBar owner) {
        super(owner);
        this.setCaption("操作提示");
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void addLine(String value) {
        lines.add(value);
    }

    public void addLine(String format, Object... args) {
        lines.add(String.format(format, args));
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<section");
        if (this.cssStyle != null) {
            html.print(" style=\"%s\"", this.cssStyle);
        }
        if (this.cssClass != null) {
            html.print(" class=\"%s\"", this.cssClass);
        }
        if (this.getId() != null) {
            html.print(" id=\"%s\"", this.getId());
        }
        html.println(">");
        html.print("<div class=\"title\">");
        html.print(this.getCaption());
        if (operaUrl != null) {
            operaUrl.output(html);
        }
        html.println("</div>");
        html.println("<div class=\"contents\">");
        if (this.content != null)
            html.println("<p>%s</p>", this.content);
        for (String line : lines)
            html.println("<p>%s</p>", line);
        html.println("</div>");
        html.println("</section>");
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public UrlMenu getOperaUrl() {
        if (operaUrl == null) {
            operaUrl = new UrlMenu(null);
            operaUrl.setCssStyle("float:right;margin-bottom:0.25em");
        }
        return operaUrl;
    }

    public void setOperaUrl(UrlMenu operaUrl) {
        this.operaUrl = operaUrl;
    }

}
