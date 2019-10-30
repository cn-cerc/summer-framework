package cn.cerc.ui.fields;

import cn.cerc.core.Record;
import cn.cerc.core.TDate;
import cn.cerc.core.TDateTime;
import cn.cerc.ui.core.DataSource;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IField;
import cn.cerc.ui.other.BuildText;
import cn.cerc.ui.other.BuildUrl;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UIText;
import net.sf.json.JSONObject;

public abstract class AbstractField extends UIComponent implements IField {
    private String htmlTag = "input";
    private String htmType;
    private String name;
    private String shortName;
    private String align;
    private int width;
    // 数据库相关
    protected String field;
    // 自定义取值
    protected BuildText buildText;
    // 手机专用样式
    private String CSSClass_phone;
    // value
    private String value;
    // 只读否
    private boolean readonly;
    // 自动完成（默认为 off）
    private boolean autocomplete = false;
    // 焦点否
    protected boolean autofocus;
    //
    protected boolean required;

    // 用于文件上传是否可以选则多个文件
    protected boolean multiple = false;
    //
    protected String placeholder;
    // 正则过滤
    protected String pattern;
    //
    protected boolean hidden;
    // 角色
    protected String role;
    //
    protected DialogField dialog;
    // dialog 小图标
    protected String icon;
    // 栏位说明
    private UIText mark;
    //
    protected BuildUrl buildUrl;
    //
    protected DataSource dataSource;

    private boolean visible = true;

    protected String oninput;

    protected String onclick;

    // TODO 专用于textarea标签，需要拆分该标签出来，黄荣君 2016-05-31
    // 最大字符串数
    private int maxlength;
    // 可见行数
    private int rows;
    // 可见宽度
    private int cols;
    // 是否禁用
    private boolean resize = true;
    // 是否显示*号
    private boolean showStar = false;

    public AbstractField(UIComponent owner, String name, int width) {
        super(owner);
        if (owner != null) {
            if ((owner instanceof DataSource)) {
                this.dataSource = (DataSource) owner;
                dataSource.addField(this);
                this.setReadonly(dataSource.isReadonly());
            }
        }
        this.name = name;
        this.width = width;
    }

    public UIText getMark() {
        return mark;
    }

    public AbstractField setMark(UIText mark) {
        this.mark = mark;
        return this;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public int getWidth() {
        return width;
    }

    public String getShortName() {
        if (this.shortName != null)
            return this.shortName;
        return this.getName();
    }

    public AbstractField setWidth(int width) {
        this.width = width;
        return this;
    }

    public AbstractField setShortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

    public AbstractField setAlign(String align) {
        this.align = align;
        return this;
    }

    public AbstractField setName(String name) {
        this.name = name;
        return this;
    }

    public String getHtmType() {
        return htmType;
    }

    public AbstractField setHtmType(String htmType) {
        this.htmType = htmType;
        return this;
    }

    @Override
    public String getAlign() {
        return align;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getField() {
        return field;
    }

    public AbstractField setField(String field) {
        this.field = field;
        if (this.getId() == null || this.getId().startsWith("component"))
            this.setId(field);
        return this;
    }

    public abstract String getText(Record ds);

    /**
     * 
     * @param rs 当前记录集
     * @return 返回输出文本
     */
    protected String getDefaultText(Record rs) {
        if (rs == null)
            return null;
        if (buildText != null) {
            HtmlWriter html = new HtmlWriter();
            buildText.outputText(rs, html);
            return html.toString();
        }
        return rs.getString(getField());
    }

    public BuildText getBuildText() {
        return buildText;
    }

    public AbstractField createText(BuildText buildText) {
        this.buildText = buildText;
        return this;
    }

    public String getCSSClass_phone() {
        return CSSClass_phone;
    }

    public void setCSSClass_phone(String cSSClass_phone) {
        CSSClass_phone = cSSClass_phone;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public AbstractField setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    public String getValue() {
        return value;
    }

    public AbstractField setValue(String value) {
        this.value = value;
        return this;
    }

    public boolean isAutocomplete() {
        return autocomplete;
    }

    public AbstractField setAutocomplete(boolean autocomplete) {
        this.autocomplete = autocomplete;
        return this;
    }

    public boolean isAutofocus() {
        return autofocus;
    }

    public AbstractField setAutofocus(boolean autofocus) {
        this.autofocus = autofocus;
        return this;
    }

    public boolean isRequired() {
        return required;
    }

    public AbstractField setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public AbstractField setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public String getPattern() {
        return pattern;
    }

    public AbstractField setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    public boolean isHidden() {
        return hidden;
    }

    public AbstractField setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    @Override
    public void output(HtmlWriter html) {
        Record record = dataSource != null ? dataSource.getDataSet().getCurrent() : null;
        if (this.hidden) {
            outputInput(html, record);
        } else {
            html.println("<label for=\"%s\">%s</label>", this.getId(), this.getName() + "：");
            outputInput(html, record);
            if (this.showStar) {
                html.println("<font>*</font>");
            }
            if (this.dialog != null && this.dialog.isOpen()) {
                html.print("<span>");
                html.print("<a href=\"%s\">", dialog.getUrl());

                if (this.icon != null)
                    html.print("<img src=\"%s\">", this.icon);
                else
                    html.print("<img src=\"images/searchIocn.png\">");

                html.print("</a>");
                html.println("</span>");
            } else {
                html.println("<span></span>");
            }
        }
    }

    protected void outputInput(HtmlWriter html, Record dataSet) {
        if ("textarea".equals(htmlTag)) {
            outputTextArea(html, dataSet);
            return;
        }

        if (this.hidden) {
            html.print("<input");
            html.print(" type=\"hidden\"");
            html.print(" name=\"%s\"", this.getId());
            html.print(" id=\"%s\"", this.getId());
            String value = this.getText(dataSet);
            if (value != null)
                html.print(" value=\"%s\"", value);
            html.println("/>");
        } else {
            html.print("<input");
            if (htmType != null) {
                html.print(" type=\"%s\"", this.getHtmType());
            } else {
                html.print(" type=\"text\"");
            }
            html.print(" name=\"%s\"", this.getId());
            html.print(" id=\"%s\"", this.getId());
            String value = this.getText(dataSet);
            if (value != null)
                html.print(" value=\"%s\"", value);
            if (this.getValue() != null) {
                html.print(" value=\"%s\"", this.getValue());
            }
            if (this.isReadonly())
                html.print(" readonly=\"readonly\"");
            if (this.autocomplete) {
                html.print(" autocomplete=\"on\"");
            } else {
                html.print(" autocomplete=\"off\"");
            }
            if (this.autofocus)
                html.print(" autofocus");
            if (this.required)
                html.print(" required");
            if (this.multiple)
                html.print(" multiple");
            if (this.placeholder != null)
                html.print(" placeholder=\"%s\"", this.placeholder);
            if (this.pattern != null)
                html.print(" pattern=\"%s\"", this.pattern);
            if (this.CSSClass_phone != null)
                html.print(" class=\"%s\"", this.CSSClass_phone);
            if (this.oninput != null)
                html.print(" oninput=\"%s\"", this.oninput);
            if (this.onclick != null)
                html.print(" onclick=\"%s\"", this.onclick);
            html.println("/>");
        }
    }

    private void outputTextArea(HtmlWriter html, Record dataSet) {
        html.print("<textarea");
        html.print(" id=\"%s\"", this.getId());
        html.print(" name=\"%s\"", this.getId());
        String value = this.getText(dataSet);

        if (readonly) {
            html.print(" readonly=\"readonly\"");
        }
        if (autofocus) {
            html.print(" autofocus");
        }
        if (required) {
            html.print(" required");
        }
        if (placeholder != null) {
            html.print(" placeholder=\"%s\"", placeholder);
        }
        if (maxlength > 0) {
            html.print(" maxlength=\"%s\"", maxlength);
        }
        if (rows > 0) {
            html.print(" rows=\"%s\"", rows);
        }
        if (cols > 0) {
            html.print(" cols=\"%s\"", cols);
        }
        if (resize) {
            html.println("style=\"resize: none;\"");
        }
        html.println(">");

        if (value != null)
            html.print("%s", value);
        if (this.getValue() != null) {
            html.print("%s", this.getValue());
        }
        html.println("</textarea>");
    }

    public DialogField getDialog() {
        return dialog;
    }

    public AbstractField setDialog(String dialogfun) {
        this.dialog = new DialogField(dialogfun);
        dialog.setInputId(this.getId());
        return this;
    }

    public AbstractField setDialog(String dialogfun, String... params) {
        this.dialog = new DialogField(dialogfun);
        dialog.setInputId(this.getId());
        for (String string : params) {
            this.dialog.add(string);
        }
        return this;
    }

    public void createUrl(BuildUrl build) {
        this.buildUrl = build;
    }

    public BuildUrl getBuildUrl() {
        return buildUrl;
    }

    public Title createTitle() {
        Title title = new Title();
        title.setName(this.getField());
        return title;
    }

    public void updateField() {
        if (dataSource != null) {
            String field = this.getId();
            if (field != null && !"".equals(field))
                dataSource.updateValue(this.getId(), this.getField());
        }
    }

    public void setDataView(DataSource dataView) {
        this.dataSource = dataView;
    }

    public String getOninput() {
        return oninput;
    }

    public AbstractField setOninput(String oninput) {
        this.oninput = oninput;
        return this;
    }

    public String getOnclick() {
        return onclick;
    }

    public AbstractField setOnclick(String onclick) {
        this.onclick = onclick;
        return this;
    }

    @Override
    public String getTitle() {
        return this.getName();
    }

    public class Editor {
        private String xtype;

        public Editor(String xtype) {
            super();
            this.xtype = xtype;
        }

        public String getXtype() {
            return xtype;
        }

        public void setXtype(String xtype) {
            this.xtype = xtype;
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public AbstractField setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public boolean isShowStar() {
        return showStar;
    }

    public AbstractField setShowStar(boolean showStar) {
        this.showStar = showStar;
        return this;
    }

    public String getString() {
        if (dataSource == null)
            throw new RuntimeException("owner is null.");
        if (dataSource.getDataSet() == null)
            throw new RuntimeException("owner.dataSet is null.");
        return dataSource.getDataSet().getString(this.getField());
    }

    public boolean getBoolean() {
        String val = this.getString();
        return "1".equals(val) || "true".equals(val);
    }

    public boolean getBoolean(boolean def) {
        String val = this.getString();
        if (val == null)
            return def;
        return "1".equals(val) || "true".equals(val);
    }

    public int getInt() {
        String val = this.getString();
        if (val == null || "".equals(val))
            return 0;
        return Integer.parseInt(val);
    }

    public int getInt(int def) {
        String val = this.getString();
        if (val == null || "".equals(val))
            return def;
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            return def;
        }
    }

    public double getDouble() {
        String val = this.getString();
        if (val == null || "".equals(val))
            return 0;
        return Double.parseDouble(val);
    }

    public double getDouble(double def) {
        String val = this.getString();
        if (val == null || "".equals(val))
            return def;
        try {
            return Double.parseDouble(val);
        } catch (Exception e) {
            return def;
        }
    }

    public TDateTime getDateTime() {
        String val = this.getString();
        if (val == null)
            return null;
        return TDateTime.fromDate(val);
    }

    public TDate getDate() {
        String val = this.getString();
        if (val == null)
            return null;
        TDateTime obj = TDateTime.fromDate(val);
        if (obj == null)
            return null;
        return new TDate(obj.getData());
    }

    public String getString(String def) {
        String result = this.getString();
        return result != null ? result : def;
    }

    public TDate getDate(TDate def) {
        TDate result = this.getDate();
        return result != null ? result : def;
    }

    public TDateTime getDateTime(TDateTime def) {
        TDateTime result = this.getDateTime();
        return result != null ? result : def;
    }

    public class Title {
        private String name;
        private String type;
        private String dateFormat;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDateFormat() {
            return dateFormat;
        }

        public void setDateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
        }

        @Override
        public String toString() {
            JSONObject json = new JSONObject();
            json.put("name", this.name);
            if (this.type != null)
                json.put("type", this.type);
            if (this.dateFormat != null)
                json.put("dateFormat", this.dateFormat);
            return json.toString().replace("\"", "'");
        }
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getHtmlTag() {
        return htmlTag;
    }

    public AbstractField setHtmlTag(String htmlTag) {
        this.htmlTag = htmlTag;
        return this;
    }

    public int getMaxlength() {
        return maxlength;
    }

    public AbstractField setMaxlength(int maxlength) {
        this.maxlength = maxlength;
        return this;
    }

    public int getRows() {
        return rows;
    }

    public AbstractField setRows(int rows) {
        this.rows = rows;
        return this;
    }

    public int getCols() {
        return cols;
    }

    public AbstractField setCols(int cols) {
        this.cols = cols;
        return this;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

}
