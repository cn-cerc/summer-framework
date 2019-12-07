package cn.cerc.ui.phone;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.ext.UISpan;

/**
 * 三行文字列表显示
 * <p>
 * 标题 + 3行信息说明
 * 
 * @author HuangRongjun
 *
 */
public class Block306 extends UIComponent {
    private UISpan title;
    private List<String> items = new ArrayList<>();

    public Block306(UIComponent owner) {
        super(owner);
        title = new UISpan();
        title.setText("(title)");
        title.setRole("title");
    }

    @Override
    public void output(HtmlWriter html) {
        if (items.size() == 0) {
            for (int i = 0; i < 3; i++) {
                items.add("line" + i);
            }
        }

        html.println("<!-- %s -->", this.getClass().getName());
        html.print("<div class='block306'>");

        title.output(html);

        for (String line : items) {
            html.print("<div role='line'>%s</div>", line);
        }
        html.print("</div>");
    }

    public UISpan getTitle() {
        return title;
    }

    public int size() {
        return items.size();
    }

    public void addItem(String line) {
        if (items.size() > 2) {
            throw new RuntimeException("最多只能放3行信息");
        }
        items.add(line);
    }

    public void addItem(String format, Object... args) {
        if (items.size() > 2) {
            throw new RuntimeException("最多只能放3行信息");
        }
        items.add(String.format(format, args));
    }

}
