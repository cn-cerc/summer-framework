package cn.cerc.mis.mail;

public class HtmlCol extends HtmlControl {
    private int colSpan = 1;
    private int width = 0;
    private String style = null;
    private boolean header = false;
    private String align = null;

    private StringBuffer text = new StringBuffer();

    public HtmlCol(HtmlControl owner) {
        super(owner);

    }

    public StringBuffer getText() {
        return text;
    }

    public HtmlCol setText(String text) {
        this.text = new StringBuffer(text);
        return this;
    }

    @Override
    public void getHtml(StringBuffer html) {
        if (this.header)
            html.append("<th");
        else
            html.append("<td");
        if (this.style != null) {
            html.append(String.format(" style='%s'", this.style));
        }
        if (this.colSpan > 1) {
            html.append(String.format(" colspan=%d", this.colSpan));
        }
        if (this.width > 0) {
            html.append(String.format(" width='%d'", this.width));
        }
        if (this.align != null) {
            html.append(String.format(" align='%s'", this.align));
        }
        html.append(">");
        html.append(this.text);
        if (this.header)
            html.append("</th>");
        else
            html.append("</td>");
    }

    public StringBuffer append(String text) {
        return this.text.append(text);
    }

    public int getColSpan() {
        return colSpan;
    }

    public HtmlCol setColSpan(int colSpan) {
        this.colSpan = colSpan;
        return this;
    }

    public boolean isHeader() {
        return header;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }

    public int getWidth() {
        return width;
    }

    public HtmlCol setWidth(int width) {
        this.width = width;
        return this;
    }

    public String getStyle() {
        return style;
    }

    public HtmlCol setStyle(String style) {
        this.style = style;
        return this;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

}
