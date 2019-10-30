package cn.cerc.mis.mail;

import cn.cerc.core.DataSet;

public class HtmlGrid extends HtmlControl {
    private int border = 1;
    private int cellspacing = 0;
    private int cellpadding = 2;
    private String width;
    private String HtmlString;
    private String style = null;
    private String bordercolor = "#FFFFFF";
    private String bgcolor = "#FFFFFF";

    public HtmlGrid(HtmlControl owner) {
        super(owner);
    }

    public HtmlRow addRow() {
        return new HtmlRow(this);
    }

    @Override
    public void getHtml(StringBuffer html) {
        html.append("<table class=\"dbgrid\"");
        if (this.getBorder() > 0) {
            if (this.style != null) {
                html.append(String.format(" style='%s'", this.style));
            }
            html.append(String.format(" border='%d' ", this.getBorder()));
            html.append(String.format(" cellspacing='%d'", cellspacing));
            html.append(String.format(" cellpadding='%d'", cellpadding));
            html.append(String.format(" bordercolor='%s'", bordercolor));
            html.append(String.format(" bgcolor='%s'", bgcolor));
        }
        if (this.width != null && !this.width.equals("")) {
            html.append(String.format(" width='%s'", this.width));
        }
        html.append(">");
        super.getHtml(html);
        html.append("</table>");
    }

    public String getWidth() {
        return width;
    }

    public HtmlGrid setWidth(String width) {
        this.width = width;
        return this;
    }

    public int getBorder() {
        return border;
    }

    public HtmlGrid setBorder(int border) {
        this.border = border;
        return this;
    }

    @Override
    public String toString() {
        if (HtmlString == null) {
            StringBuffer html = new StringBuffer();
            this.getHtml(html);
            HtmlString = html.toString();
        }
        return HtmlString;
    }

    public static void main(String[] args) {
        StringBuffer html = new StringBuffer();
        HtmlGrid grid = new HtmlGrid(null);
        grid.addRow().addCol().setText("hello");
        grid.getHtml(html);
        // log.info(grid.toString());
    }

    public int getCellspacing() {
        return cellspacing;
    }

    public HtmlGrid setCellspacing(int cellspacing) {
        this.cellspacing = cellspacing;
        return this;
    }

    public int getCellpadding() {
        return cellpadding;
    }

    public HtmlGrid setCellpadding(int cellpadding) {
        this.cellpadding = cellpadding;
        return this;
    }

    public String getStyle() {
        return style;
    }

    public HtmlGrid setStyle(String style) {
        this.style = style;
        return this;
    }

    public static String getDataSet(DataSet ds) {
        HtmlRow row;
        StringBuffer sb = new StringBuffer();

        HtmlGrid head = new HtmlGrid(null);
        // 加入单头
        row = head.addRow();
        for (String field : ds.getHead().getFieldDefs().getFields())
            row.addCol(field);
        row = head.addRow();
        for (String field : ds.getHead().getFieldDefs().getFields())
            row.addCol(ds.getHead().getField(field));

        // 加入单身
        HtmlGrid detail = new HtmlGrid(null);
        ds.first();
        row = detail.addRow();
        for (String field : ds.getFieldDefs().getFields())
            row.addCol(field);
        while (ds.fetch()) {
            row = detail.addRow();
            for (String field : ds.getFieldDefs().getFields())
                row.addCol(ds.getField(field));
        }
        sb.append("<!DOCTYPE html>");
        sb.append("<head>");
        sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
        sb.append("</head>");
        sb.append("<body>");

        head.getHtml(sb);
        detail.getHtml(sb);
        sb.append("</body></html>");
        return sb.toString();
    }
}
