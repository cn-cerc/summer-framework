package cn.cerc.ui.fields;

import cn.cerc.core.Record;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class UploadField extends AbstractField {
    // 用于文件上传是否可以选则多个文件
    private boolean multiple = false;
    private String htmType;

    public UploadField(UIComponent owner, String name, String field) {
        super(owner, name, 5);
        this.setField(field);
        this.setHtmType("file");
    }

    @Override
    public String getText() {
        Record record = getRecord();
        return record.getString(field);
    }

    public boolean isMultiple() {
        return multiple;
    }

    public UploadField setMultiple(boolean multiple) {
        this.multiple = multiple;
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

    @Override
    public void outputLine(HtmlWriter html) {
        if (this.isReadonly()) {
            html.print(this.getName() + "：");
            html.print(this.getText());
        } else {
            html.print("<label for=\"%s\">%s</label>", this.getId(), this.getName() + "：");
            html.print("<input");
            html.print(" type=\"text\"");
            html.print(" id=\"%s\"", this.getId());
            html.print(" name=\"%s\"", this.getId());
            String value = this.getText();
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
            if (this.isMultiple()) {
                html.print(" multiple");
            }
            html.println("/>");

            html.print("<span>");
            html.println("</span>");
        }
    }

}
