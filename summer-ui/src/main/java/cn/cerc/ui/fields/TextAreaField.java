package cn.cerc.ui.fields;

import cn.cerc.core.Record;
import cn.cerc.mis.core.IForm;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class TextAreaField extends AbstractField implements IFieldPlaceholder, IFieldTextArea {
    private String placeholder;
    private boolean required;
    // 最大字符串数
    private int maxlength;
    // 可见行数
    private int rows;
    // 可见宽度
    private int cols;
    // 是否禁用
    private boolean resize = true;

    public TextAreaField(UIComponent owner, String name, String field) {
        super(owner, name, 0);
        this.setField(field);
    }

    public TextAreaField(UIComponent owner, String name, String field, int width) {
        super(owner, name, 0);
        this.setField(field);
        this.setWidth(width);
    }

    @Override
    public void outputLine(HtmlWriter html) {
        if (this.isReadonly()) {
            outputTextArea(html);
        } else {
            if (this.getOrigin() instanceof IForm) {
                IForm form = (IForm) this.getOrigin();
                if (form.getClient().isPhone()) {
                    if (this.isReadonly()) {
                        html.print(this.getName() + "：");
                        html.print(this.getText());
                        return;
                    }
                }
            }

            html.print("<label for=\"%s\">%s</label>", this.getId(), this.getName() + "：");
            outputTextArea(html);
            html.println("<span></span>");
        }
    }

    private void outputTextArea(HtmlWriter html) {
        html.print("<textarea");
        html.print(" id=\"%s\"", this.getId());
        html.print(" name=\"%s\"", this.getId());
        String value = this.getText();

        if (this.isReadonly()) {
            html.print(" readonly=\"readonly\"");
        }
        if (this.isRequired()) {
            html.print(" required");
        }
        if (this.getPlaceholder() != null) {
            html.print(" placeholder=\"%s\"", this.getPlaceholder());
        }
        if (this.getMaxlength() > 0) {
            html.print(" maxlength=\"%s\"", this.getMaxlength());
        }
        if (this.getRows() > 0) {
            html.print(" rows=\"%s\"", this.getRows());
        }
        if (this.getCols() > 0) {
            html.print(" cols=\"%s\"", this.getCols());
        }
        if (this.isResize()) {
            html.println("style=\"resize: none;\"");
        }
        html.println(">");

        if (value != null) {
            html.print("%s", value);
        }
        if (this.getValue() != null) {
            html.print("%s", this.getValue());
        }
        html.println("</textarea>");
    }

    @Override
    public String getText() {
        Record record = getRecord();
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

    @Override
    public String getPlaceholder() {
        return placeholder;
    }

    @Override
    public TextAreaField setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public boolean isRequired() {
        return required;
    }

    public TextAreaField setRequired(boolean required) {
        this.required = required;
        return this;
    }

    @Override
    public int getMaxlength() {
        return maxlength;
    }

    @Override
    public TextAreaField setMaxlength(int maxlength) {
        this.maxlength = maxlength;
        return this;
    }

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public TextAreaField setRows(int rows) {
        this.rows = rows;
        return this;
    }

    @Override
    public int getCols() {
        return cols;
    }

    @Override
    public TextAreaField setCols(int cols) {
        this.cols = cols;
        return this;
    }

    @Override
    public boolean isResize() {
        return resize;
    }

    @Override
    public TextAreaField setResize(boolean resize) {
        this.resize = resize;
        return this;
    }

    // 隐藏输出
    @Override
    public void outputHidden(HtmlWriter html) {
        html.print("<input");
        html.print(" type=\"hidden\"");
        html.print(" id=\"%s\"", this.getId());
        html.print(" name=\"%s\"", this.getId());
        String value = this.getText();
        if (value != null) {
            html.print(" value=\"%s\"", value);
        }
        html.println("/>");
    }

}
