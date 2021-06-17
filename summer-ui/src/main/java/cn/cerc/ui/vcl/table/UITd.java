package cn.cerc.ui.vcl.table;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.parts.UICssComponent;

public class UITd extends UICssComponent {
    private int colspan;
    private int rowspan;
    private String text;

    public UITd() {
        super();
    }

    public UITd(UIComponent component) {
        super(component);
    }

    @Override
    public void output(HtmlWriter html) {
        html.print("<td");
        if (this.getCssClass() != null)
            html.print(" class=\"%s\"", this.getCssClass());
        if (this.rowspan != 0)
            html.print(" rowspan=\"%s\"", this.rowspan);
        if (this.colspan != 0)
            html.print(" colspan=\"%s\"", this.colspan);
        html.print(">");
        if (this.text != null) {
            html.print(text);
        }
        for (UIComponent item : this)
            item.output(html);
        html.print("</td>");
    }

    public int getColspan() {
        return colspan;
    }

    public UITd setColspan(int colspan) {
        this.colspan = colspan;
        return this;
    }

    public int getRowspan() {
        return rowspan;
    }

    public UITd setRowspan(int rowspan) {
        this.rowspan = rowspan;
        return this;
    }

    public String getText() {
        return text;
    }

    public UITd setText(String text) {
        this.text = text;
        return this;
    }

}
