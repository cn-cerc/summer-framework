package cn.cerc.ui.phone;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.UrlRecord;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UIImage;
import cn.cerc.ui.vcl.ext.UISpan;

/**
 * 三行文字列表显示，右侧带导航栏箭头
 * <p>
 * 标题 + 3行信息说明
 * 
 * @author HuangRongjun
 *
 */
public class Block307 extends UIComponent {
    private UISpan title;
    private UrlRecord url;
    private UIImage icon = new UIImage();
    private List<String> items = new ArrayList<>();

    public Block307(UIComponent owner) {
        super(owner);
        title = new UISpan();
        title.setText("(title)");
        title.setRole("title");

        url = new UrlRecord();
        url.setName("(url)");

        icon.setSrc("jui/phone/block301-rightIcon.png");
        icon.setRole("right");
    }

    @Override
    public void output(HtmlWriter html) {
        if (items.size() == 0) {
            for (int i = 0; i < 3; i++) {
                items.add("line" + i);
            }
        }

        html.println("<!-- %s -->", this.getClass().getName());
        html.print("<div class='block307'>");
        html.print("<a href='%s'>", url.getUrl());

        title.output(html);

        for (String line : items) {
            html.print("<div role='line'>%s</div>", line);
        }

        icon.output(html);

        html.print("</a>");
        html.print("</div>");
    }

    public UISpan getTitle() {
        return title;
    }

    public UrlRecord getUrl() {
        return url;
    }

    public void setUrl(UrlRecord url) {
        this.url = url;
    }

    public UIImage getIcon() {
        return icon;
    }

    public void setIcon(UIImage icon) {
        this.icon = icon;
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
}
