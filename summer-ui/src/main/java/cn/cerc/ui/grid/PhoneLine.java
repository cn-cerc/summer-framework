package cn.cerc.ui.grid;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.core.DataSet;
import cn.cerc.core.Record;
import cn.cerc.ui.core.DataSource;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IField;
import cn.cerc.ui.core.UrlRecord;
import cn.cerc.ui.fields.AbstractField;
import cn.cerc.ui.fields.ExpendField;
import cn.cerc.ui.other.BuildUrl;
import cn.cerc.ui.parts.UIComponent;

public class PhoneLine extends UIComponent implements DataSource {
    private DataSource dataSource;
    private boolean Table = false;
    private String style;
    private ExpendField expender;

    private List<AbstractField> columns = new ArrayList<>();

    public PhoneLine(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getStyle() {
        return style;
    }

    public PhoneLine setStyle(String style) {
        this.style = style;
        return this;
    }

    public List<AbstractField> getColumns() {
        return columns;
    }

    public boolean isTable() {
        return Table;
    }

    public PhoneLine setTable(boolean table) {
        Table = table;
        return this;
    }

    private void outputTableString(HtmlWriter html) {
        if (dataSource == null) {
            throw new RuntimeException("dataView is null");
        }
        if (dataSource.getDataSet() == null) {
            throw new RuntimeException("dataSet is null");
        }
        Record record = dataSource.getDataSet().getCurrent();
        html.print("<tr");
        if (this.expender != null) {
            html.print(String.format(" role=\"%s\" style=\"display: none;\"", expender.getHiddenId()));
        }
        html.print(">");
        for (AbstractField field : columns) {
            html.print("<td");
            if (columns.size() == 1) {
                html.print(" colspan=2");
            }
            html.print(">");

            BuildUrl build = field.getBuildUrl();
            if (build != null) {
                String name = field.getShortName();
                if (!"".equals(name)) {
                    html.print(name + ": ");
                }
                UrlRecord url = new UrlRecord();
                build.buildUrl(record, url);
                if (!"".equals(url.getUrl())) {
                    html.println("<a href=\"%s\">", url.getUrl());
                    html.print(field.getText(record));
                    html.println("</a>");
                } else {
                    html.println(field.getText(record));
                }
            } else {
                outputColumn(field, html);
            }

            html.print("</td>");
        }
        html.print("</tr>");
    }

    public void outputListString(HtmlWriter html) {
        html.print("<section>");
        for (AbstractField field : columns) {
            html.print("<span");
            if (field.getCSSClass_phone() != null) {
                html.print(String.format(" class=\"%s\"", field.getCSSClass_phone()));
            }
            html.print(">");
            BuildUrl build = field.getBuildUrl();
            if (build != null) {
                Record record = dataSource != null ? dataSource.getDataSet().getCurrent() : null;
                UrlRecord url = new UrlRecord();
                build.buildUrl(record, url);
                if (!"".equals(url.getUrl())) {
                    html.println("<a href=\"%s\">", url.getUrl());
                    outputColumn(field, html);
                    html.println("</a>");
                } else {
                    html.println(field.getText(record));
                }
            } else {
                outputColumn(field, html);
            }
            html.print("</span>");
        }
        html.print("</section>");
    }

    private void outputColumn(AbstractField field, HtmlWriter html) {
        DataSet dataSet = dataSource != null ? dataSource.getDataSet() : null;
        String name = field.getShortName();
        if (!"".equals(name)) {
            html.print(name + ": ");
        }
        html.print(field.getText(dataSet.getCurrent()));
    }

    @Override
    public void output(HtmlWriter html) {
        if (this.Table) {
            outputTableString(html);
        } else {
            outputListString(html);
        }
    }

    @Override
    public void addField(IField field) {
        if (field instanceof AbstractField) {
            columns.add((AbstractField) field);
        }
    }

    public PhoneLine addItem(AbstractField... fields) {
        for (AbstractField field : fields) {
            addField(field);
        }
        return this;
    }

    public ExpendField getExpender() {
        return expender;
    }

    public void setExpender(ExpendField expender) {
        this.expender = expender;
    }

    @Override
    public DataSet getDataSet() {
        return dataSource.getDataSet();
    }

    @Override
    public boolean isReadonly() {
        return dataSource.isReadonly();
    }

    @Override
    public void updateValue(String id, String code) {
        dataSource.updateValue(id, code);
    }
}
