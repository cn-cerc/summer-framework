package cn.cerc.mis.mail;

public class HtmlRow extends HtmlControl {
    private boolean header = false;
    private String style = null;

    public HtmlRow(HtmlControl owner) {
        super(owner);
    }

    public HtmlCol addCol() {
        return addCol(null);
    }

    public HtmlCol addCol(Object text) {
        HtmlCol col = new HtmlCol(this);
        col.setHeader(header);
        if (this.header) {
            col.setStyle("background-color: #CDFFCD;");
        }
        if (text != null) {
            col.append(text == null ? "" : text.toString());
        }
        return col;
    }

    @Override
    public void getHtml(StringBuffer html) {
        html.append("<tr");
        if (this.style != null) {
            html.append(String.format(" style='%s'", this.style));
        }
        html.append(">");
        super.getHtml(html);
        html.append("</tr>");
    }

    public static void main(String[] args) {
        StringBuffer html = new StringBuffer();
        HtmlRow row = new HtmlRow(null);
        row.addCol().setText("this is GridRow.");
        row.getHtml(html);
        // log.info(html.toString());
    }

    public boolean isHeader() {
        return header;
    }

    public HtmlRow setHeader(boolean header) {
        this.header = header;
        return this;
    }

    public String getStyle() {
        return style;
    }

    public HtmlRow setStyle(String style) {
        this.style = style;
        return this;
    }
}
