package cn.cerc.ui.parts;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.core.DataSet;
import cn.cerc.core.Record;
import cn.cerc.ui.UIConfig;
import cn.cerc.ui.core.Component;
import cn.cerc.ui.core.DataSource;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IField;
import cn.cerc.ui.fields.AbstractField;
import cn.cerc.ui.vcl.UIText;

public class UIFormVertical extends UIComponent implements DataSource {
    private UIContent content;
    protected String CSSClass = "info";
    protected String method = "post";
    protected DataSet dataSet;
    protected List<AbstractField> fields = new ArrayList<>();
    protected String action;
    private String enctype;
    private String submit;
    private boolean readAll;

    public UIFormVertical(UIContent owner) {
        super(owner);
        this.setId("form1");
        this.dataSet = new DataSet();
        this.content = owner;
        dataSet.append();
    }

    @Override
    @Deprecated
    public void setOwner(Component owner) {
        super.setOwner(owner);
    }

    public String getCSSClass() {
        return CSSClass;
    }

    public void setCSSClass(String cSSClass) {
        CSSClass = cSSClass;
    }

    @Override
    public void addField(IField field) {
        if (field instanceof AbstractField)
            fields.add((AbstractField) field);
        else
            throw new RuntimeException("不支持的数据类型：" + field.getClass().getName());
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public void output(HtmlWriter html) {
        html.print("<form method=\"%s\" id=\"%s\"", this.method, this.getId());
        if (this.action != null) {
            html.print(String.format(" action=\"%s\"", this.action));
        }
        if (this.enctype != null) {
            html.print(" enctype=\"%s\"", this.enctype);
        }
        html.println(">");

        int i = 0;
        for (AbstractField field : fields) {
            if (field.isHidden()) {
                field.output(html);
            } else {
                i++;
            }
        }

        if (i > 0) {
            outputFields(html);
        }

        html.println("</form>");
    }

    private void outputFields(HtmlWriter html) {
        html.print("<ul");
        if (this.CSSClass != null)
            html.print(" class=\"%s\"", this.CSSClass);
        html.println(">");

        for (AbstractField field : fields) {
            if (!field.isHidden()) {
                html.print("<li");
                if (field.getRole() != null)
                    html.print(" role='%s'", field.getRole());
                html.print(">");
                field.output(html);

                UIText mark = field.getMark();
                if (mark != null) {
                    html.println("<a href=\"javascript:displaySwitch('%s')\">", field.getId());
                    html.println("<img src=\"%s\" />", UIConfig.GUIDE);
                    html.println("</a>");
                    html.println("</li>");
                    html.println("<li role=\"%s\" style=\"display: none;\">", field.getId());
                    html.print("<mark>");
                    if (mark.getContent() != null)
                        html.println("%s", mark.getContent());
                    for (String line : mark.getLines())
                        html.println("<p>%s</p>", line);
                    html.println("</mark>");
                    html.println("</li>");
                } else {
                    html.println("</li>");
                }
            }
        }
        html.println("</ul>");
    }

    public String readAll() {
        if (readAll)
            return submit;

        submit = content.getRequest().getParameter("opera");

        // 将用户值或缓存值存入到dataSet中
        for (AbstractField field : this.fields) {
            field.updateField();
        }

        readAll = true;
        return submit;
    }

    @Override
    public void updateValue(String id, String code) {
        String val = content.getRequest().getParameter(id);
        if (submit != null) {
            dataSet.setField(code, val);
        } else {
            if (val != null)
                dataSet.setField(code, val);
        }
    }

    public String getExtGrid() {
        return null;
    }

    public void setRecord(Record record) {
        dataSet.getCurrent().copyValues(record, record.getFieldDefs());
        dataSet.setRecNo(dataSet.size());
    }

    @Override
    public DataSet getDataSet() {
        return dataSet;
    }

    public Record getRecord() {
        return dataSet.getCurrent();
    }

    @Override
    public boolean isReadonly() {
        return false;
    }

    public String getEnctype() {
        return enctype;
    }

    public void setEnctype(String enctype) {
        this.enctype = enctype;
    }
}
