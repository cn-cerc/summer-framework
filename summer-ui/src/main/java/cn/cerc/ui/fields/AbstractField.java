package cn.cerc.ui.fields;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.Record;
import cn.cerc.core.TDate;
import cn.cerc.core.TDateTime;
import cn.cerc.mis.cdn.CDN;
import cn.cerc.mis.core.IForm;
import cn.cerc.ui.SummerUI;
import cn.cerc.ui.core.DataSource;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IField;
import cn.cerc.ui.core.INameOwner;
import cn.cerc.ui.core.IReadonlyOwner;
import cn.cerc.ui.core.UIOriginComponent;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UIText;

public abstract class AbstractField extends UIOriginComponent implements IField, INameOwner, IReadonlyOwner {
    private static final ClassConfig config = new ClassConfig(AbstractField.class, SummerUI.ID);
    // 数据源
    private DataSource dataSource;
    // 数据字段
    protected String field;
    private String name;
    private String shortName;
    private String htmType;
    // 数据隐藏
    private boolean hidden;
    private String align;
    private int width;
    // value
    private String value;
    // 只读否
    private boolean readonly;
    // 栏位说明
    private UIText mark;

    public AbstractField(UIComponent owner, String name, int width) {
        super(owner);
        if (owner != null) {
            if ((owner instanceof DataSource)) {
                this.dataSource = (DataSource) owner;
                dataSource.addField(this);
            }
            if (owner instanceof IReadonlyOwner) {
                this.setReadonly(((IReadonlyOwner) owner).isReadonly());
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

    @Override
    public int getWidth() {
        return width;
    }

    public AbstractField setWidth(int width) {
        this.width = width;
        return this;
    }

    public String getShortName() {
        if (this.shortName != null) {
            return this.shortName;
        }
        return this.getName();
    }

    public AbstractField setShortName(String shortName) {
        this.shortName = shortName;
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

    public AbstractField setAlign(String align) {
        this.align = align;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    public AbstractField setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getField() {
        return field;
    }

    public AbstractField setField(String field) {
        this.field = field;
        if (this.getId() == null || this.getId().startsWith("component")) {
            this.setId(field);
            return this;
        } else {
            return this;
        }
    }

    public abstract String getText(Record record);

    /**
     * @param record 当前记录集
     * @return 返回输出文本
     */
    protected String getDefaultText(Record record) {
        if (record != null) {
            if (this instanceof IFieldBuildText) {
                IFieldBuildText obj = (IFieldBuildText) this;
                if (obj.getBuildText() != null) {
                    HtmlWriter html = new HtmlWriter();
                    obj.getBuildText().outputText(record, html);
                    return html.toString();
                }
            }
            return record.getString(getField());
        } else {
            return null;
        }
    }

    @Deprecated
    public void setCSSClass_phone(String cSSClass_phone) {
        this.setCssClass(cSSClass_phone);
    }

    @Override
    public boolean isReadonly() {
        return readonly;
    }

    @Override
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
            if (this.getOrigin() instanceof IForm) {
                IForm form = (IForm) this.getOrigin();
                if (form.getClient().isPhone()) {
                    if (this.readonly) {
                        html.print(this.getName() + "：");
                        html.print(this.getText(record));
                        return;
                    }
                }
            }
            html.print("<label for=\"%s\">%s</label>", this.getId(), this.getName() + "：");
            outputInput(html, record);
            if (this instanceof IFieldShowStar) {
                IFieldShowStar obj = (IFieldShowStar) this;
                if (obj.isShowStar()) {
                    html.println("<font>*</font>");
                }
            }
            if (this instanceof IFieldDialog) {
                IFieldDialog obj = (IFieldDialog) this;
                DialogField dialog = obj.getDialog();
                if (dialog != null && dialog.isOpen()) {
                    html.print("<span>");
                    html.print("<a href=\"%s\">", dialog.getUrl());

                    if (obj.getIcon() != null) {
                        html.print("<img src=\"%s\">", obj.getIcon());
                    } else {
                        html.print("<img src=\"%s\">", CDN.get(config.getClassProperty("icon", "")));
                    }

                    html.print("</a>");
                    html.println("</span>");
                    return;
                }
            }

            html.println("<span></span>");
        }
    }

    protected void outputInput(HtmlWriter html, Record dataSet) {
        if (this.hidden) {
            html.print("<input");
            html.print(" type=\"hidden\"");
            html.print(" name=\"%s\"", this.getId());
            html.print(" id=\"%s\"", this.getId());
            String value = this.getText(dataSet);
            if (value != null) {
                html.print(" value=\"%s\"", value);
            }
            html.println("/>");
        } else {
            html.print("<input");
            if (htmType != null) {
                html.print(" type=\"%s\"", this.getHtmType());
            } else {
                html.print(" type=\"text\"");
            }
            html.print(" id=\"%s\"", this.getId());
            html.print(" name=\"%s\"", this.getId());
            String value = this.getText(dataSet);
            if (value != null) {
                html.print(" value=\"%s\"", value);
            }
            if (this.getValue() != null) {
                html.print(" value=\"%s\"", this.getValue());
            }
            if (this.isReadonly()) {
                html.print(" readonly=\"readonly\"");
            }
            if (this.getCssClass() != null) {
                html.print(" class=\"%s\"", this.getCssClass());
            }
            if (this instanceof IFieldAutocomplete) {
                IFieldAutocomplete obj = (IFieldAutocomplete) this;
                if (obj.isAutocomplete()) {
                    html.print(" autocomplete=\"on\"");
                } else {
                    html.print(" autocomplete=\"off\"");
                }
            }
            if (this instanceof IFieldAutofocus) {
                IFieldAutofocus obj = (IFieldAutofocus) this;
                if (obj.isAutofocus()) {
                    html.print(" autofocus");
                }
            }
            if (this instanceof IFieldRequired) {
                IFieldRequired obj = (IFieldRequired) this;
                if (obj.isRequired()) {
                    html.print(" required");
                }
            }
            if (this instanceof IFieldMultiple) {
                IFieldMultiple obj = (IFieldMultiple) this;
                if (obj.isMultiple()) {
                    html.print(" multiple");
                }
            }
            if (this instanceof IFieldPlaceholder) {
                IFieldPlaceholder obj = (IFieldPlaceholder) this;
                if (obj.getPlaceholder() != null) {
                    html.print(" placeholder=\"%s\"", obj.getPlaceholder());
                }
            }
            if (this instanceof IFieldPattern) {
                IFieldPattern obj = (IFieldPattern) this;
                if (obj.getPattern() != null) {
                    html.print(" pattern=\"%s\"", obj.getPattern());
                }
            }
            if (this instanceof IFieldEvent) {
                IFieldEvent event = (IFieldEvent) this;
                if (event.getOninput() != null) {
                    html.print(" oninput=\"%s\"", event.getOninput());
                }
                if (event.getOnclick() != null) {
                    html.print(" onclick=\"%s\"", event.getOnclick());
                }
            }
            html.println("/>");
        }
    }

    public FieldTitle createTitle() {
        FieldTitle title = new FieldTitle();
        title.setName(this.getField());
        return title;
    }

    public void updateField() {
        if (dataSource != null) {
            String field = this.getId();
            if (field != null && !"".equals(field)) {
                dataSource.updateValue(this.getId(), this.getField());
            }
        }
    }

    public void setDataView(DataSource dataView) {
        this.dataSource = dataView;
    }

    @Override
    public String getTitle() {
        return this.getName();
    }

    public String getString() {
        if (dataSource == null) {
            throw new RuntimeException("owner is null.");
        }
        if (dataSource.getDataSet() == null) {
            throw new RuntimeException("owner.dataSet is null.");
        }
        return dataSource.getDataSet().getString(this.getField());
    }

    public boolean getBoolean() {
        String val = this.getString();
        return "1".equals(val) || "true".equals(val);
    }

    public boolean getBoolean(boolean def) {
        String val = this.getString();
        if (val == null) {
            return def;
        }
        return "1".equals(val) || "true".equals(val);
    }

    public int getInt() {
        String val = this.getString();
        if (val == null || "".equals(val)) {
            return 0;
        }
        return Integer.parseInt(val);
    }

    public int getInt(int def) {
        String val = this.getString();
        if (val == null || "".equals(val)) {
            return def;
        }
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            return def;
        }
    }

    public double getDouble() {
        String val = this.getString();
        if (val == null || "".equals(val)) {
            return 0;
        }
        return Double.parseDouble(val);
    }

    public double getDouble(double def) {
        String val = this.getString();
        if (val == null || "".equals(val)) {
            return def;
        }
        try {
            return Double.parseDouble(val);
        } catch (Exception e) {
            return def;
        }
    }

    public TDateTime getDateTime() {
        String val = this.getString();
        if (val == null) {
            return null;
        }
        return TDateTime.fromDate(val);
    }

    public TDate getDate() {
        String val = this.getString();
        if (val == null) {
            return null;
        }
        TDateTime obj = TDateTime.fromDate(val);
        if (obj == null) {
            return null;
        }
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

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

}
