package cn.cerc.ui.parts;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import cn.cerc.mis.other.MemoryBuffer;
import cn.cerc.ui.core.Component;
import cn.cerc.ui.core.DataSource;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IField;
import cn.cerc.ui.fields.AbstractField;
import cn.cerc.ui.fields.ButtonField;
import cn.cerc.ui.fields.ExpendField;
import cn.cerc.ui.grid.lines.AbstractGridLine;
import cn.cerc.ui.grid.lines.ExpenderGridLine;
import cn.cerc.ui.other.SearchItem;
import cn.cerc.ui.vcl.UILabel;
import cn.cerc.ui.vcl.ext.UISpan;
import cn.cerc.core.DataSet;
import cn.cerc.core.Record;

public class UIFormHorizontal extends UIComponent implements DataSource {
    private DataSet dataSet;
    protected String cssClass = "search";
    protected String method = "post";
    protected HttpServletRequest request;
    protected List<AbstractField> fields = new ArrayList<>();
    protected String action;
    private String enctype;

    private ButtonsFields buttons;
    private MemoryBuffer buff;
    private UIComponent levelSide;
    private ButtonField submit;
    private boolean readAll;
    private AbstractGridLine expender;
    private UILabel title;

    public UIFormHorizontal(UIComponent owner, HttpServletRequest request) {
        super(owner);
        this.request = request;
        this.setId("form1");
        this.setCssClass("search");
        request.setAttribute(this.getId(), this);
        this.dataSet = new DataSet();
        dataSet.append();
        this.title = new UILabel();
        this.setSearchTitle("搜索查询");
    }

    @Override
    public String getCssClass() {
        return cssClass;
    }

    @Override
    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public Record getRecord() {
        return dataSet.getCurrent();
    }

    public void setRecord(Record record) {
        dataSet.getCurrent().copyValues(record, record.getFieldDefs());
        dataSet.setRecNo(dataSet.size());
        // this.record = record;
    }

    @Override
    public void addField(IField field) {
        if (field instanceof SearchItem)
            ((SearchItem) field).setSearch(true);
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

    public void setSearchTitle(String title) {
        UISpan span = new UISpan();
        span.setCssClass("ui-title-operate");
        span.setText("");
        this.title.setCssClass("ui-title");
        this.title.setCaption(String.format("%s%s", title, span.toString()));
    }

    @Override
    public void output(HtmlWriter html) {
        readAll();

        title.output(html);
        html.print("<form method=\"%s\" id=\"%s\"", this.method, this.getId());
        if (this.action != null)
            html.print(" action=\"%s\"", this.action);
        if (this.cssClass != null)
            html.print(" class=\"%s\"", this.cssClass);
        if (this.enctype != null)
            html.print(" enctype=\"%s\"", this.enctype);
        html.println(">");

        // 输出隐藏字段
        for (AbstractField field : fields) {
            if (field.isHidden()) {
                field.output(html);
            }
        }

        html.println("<ul>");
        // 输出正常查询字段
        for (AbstractField field : fields) {
            if (!field.isHidden()) {
                html.print("<li");
                if (field.getRole() != null)
                    html.print(" role='%s'", field.getRole());
                if (field instanceof ExpendField)
                    html.print(" class=\"select\"");
                html.println(">");
                try {
                    field.output(html);
                } catch (Exception e) {
                    html.print("<label>");
                    html.print(e.getMessage());
                    html.print("</label>");
                }
                html.println("</li>");
            }
        }

        // 输出可折叠字段
        String hid = "hidden";
        for (AbstractField field : fields) {
            if (field instanceof ExpendField) {
                hid = ((ExpendField) field).getHiddenId();
                break;
            }
        }
        for (Component component : this.getExpender().getComponents()) {
            if (component instanceof AbstractField) {
                AbstractField field = (AbstractField) component;
                html.print("<li");
                html.print(" role='%s'", hid);
                html.print(" style=\"display: none;\"");
                html.println(">");
                try {
                    field.output(html);
                } catch (Exception e) {
                    html.print("<label>");
                    html.print(e.getMessage());
                    html.print("</label>");
                }
                html.println("</li>");
            }
        }

        html.println("</ul>");
        if (buttons != null) {
            for (AbstractField field : buttons.fields) {
                field.output(html);
            }
        }
        html.println("<div></div>");
        html.println("</form>");

        if (levelSide != null)
            levelSide.output(html);
    }

    public MemoryBuffer getBuffer() {
        return buff;
    }

    public void setBuffer(MemoryBuffer buff) {
        this.buff = buff;
    }

    public ButtonsFields getButtons() {
        if (buttons == null)
            buttons = new ButtonsFields(this);
        return buttons;
    }

    public void setLevelSide(UIComponent levelSide) {
        this.levelSide = levelSide;
    }

    public ButtonField readAll() {
        if (readAll) {
            return submit;
        }

        if (buttons == null) {
            return submit;
        }

        submit = null;
        // 取 form submit 按钮
        for (AbstractField field : buttons.getFields()) {
            if (field instanceof ButtonField) {
                ButtonField button = (ButtonField) field;
                String key = button.getField();
                String val = request.getParameter(key);
                if (val != null && val.equals(button.getData())) {
                    submit = button;
                    break;
                }
            }
        }

        // 将用户值或缓存值存入到dataSet中
        for (AbstractField field : this.fields)
            field.updateField();

        // 将可折叠字段的值存入到dataSet中
        for (IField field : this.getExpender().getFields())
            ((AbstractField) field).updateField();

        readAll = true;
        return submit;
    }

    @Override
    public void updateValue(String id, String code) {
        String val = request.getParameter(id);
        if (submit != null) {
            dataSet.setField(code, val == null ? "" : val);
            if (buff != null)
                buff.setField(code, val);
        } else {
            if (val != null)
                dataSet.setField(code, val);
            else if (buff != null && !buff.isNull() && buff.getRecord().exists(code))
                dataSet.setField(code, buff.getString(code));
        }
    }

    public ButtonField getSubmit() {
        return this.submit;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public AbstractGridLine getExpender() {
        if (expender == null)
            expender = new ExpenderGridLine(this);

        return expender;
    }

    public class ButtonsFields extends UIComponent implements DataSource {
        private DataSource dataSource;
        private List<AbstractField> fields = new ArrayList<>();

        public ButtonsFields(DataSource dataView) {
            this.dataSource = dataView;
        }

        @Override
        public void addField(IField field) {
            if (field instanceof AbstractField)
                fields.add((AbstractField) field);
            else
                throw new RuntimeException("不支持的数据类型：" + field.getClass().getName());
        }

        public List<AbstractField> getFields() {
            return this.fields;
        }

        public void remove(AbstractField field) {
            fields.remove(field);
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

    @Override
    public DataSet getDataSet() {
        return dataSet;
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
