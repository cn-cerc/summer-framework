package cn.cerc.ui.phone;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.ext.UISpan;

public class Block117 extends UIComponent {
    private List<UISpan> addBlock = new ArrayList<>();

    /**
     * 以span显示内容块
     * 
     * @param owner 内容显示区
     * 
     */
    public Block117(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.print("<div class='block117'>");
        if (addBlock.size() == 0) {
            addBlock("(addBlock)");
            addBlock("(addBlock)");
        }
        for (UISpan span : addBlock)
            span.output(html);
        html.println("</div>");
    }

    public UISpan addBlock(String text) {
        UISpan span = new UISpan(this);
        span.setText(text);
        addBlock.add(span);
        return span;
    }

    public UISpan addBlock(String format, Object... args) {
        UISpan span = new UISpan(this);
        span.setText(String.format(format, args));
        addBlock.add(span);
        return span;
    }

    public UISpan addUrl(String text, String onclick) {
        UISpan span = new UISpan(this);
        span.setText(text);
        span.setOnclick(onclick);
        addBlock.add(span);
        return span;
    }

}
