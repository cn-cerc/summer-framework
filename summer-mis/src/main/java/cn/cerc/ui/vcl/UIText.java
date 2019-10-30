package cn.cerc.ui.vcl;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

/*
 * 专用于简单或原始文字输出
 */
public class UIText extends UIComponent {
    private String content;
    private List<String> lines;

    public UIText() {
        super();
    }

    public UIText(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        if (content != null)
            html.print(content);
        if (lines != null) {
            for (String line : lines)
                html.println("<p>%s</p>", line);
        }
    }

    public String getContent() {
        return content;
    }

    public UIText setContent(String content) {
        this.content = content;
        return this;
    }

    public List<String> getLines() {
        if (lines == null)
            lines = new ArrayList<>();
        return lines;
    }

    public UIText setLines(List<String> lines) {
        this.lines = lines;
        return this;
    }
}
