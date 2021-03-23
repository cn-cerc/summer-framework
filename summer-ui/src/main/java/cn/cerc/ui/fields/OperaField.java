package cn.cerc.ui.fields;

import cn.cerc.core.ClassResource;
import cn.cerc.core.Record;
import cn.cerc.mis.cdn.CDN;
import cn.cerc.ui.SummerUI;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IColumn;
import cn.cerc.ui.core.UrlRecord;
import cn.cerc.ui.other.BuildText;
import cn.cerc.ui.other.BuildUrl;
import cn.cerc.ui.parts.UIComponent;

public class OperaField extends AbstractField implements IFieldDialog, IFieldBuildText, IFieldBuildUrl, IColumn {
    private static final ClassResource res = new ClassResource(OperaField.class, SummerUI.ID);
    private String value = res.getString(1, "内容");
    private UIDialogField dialog;
    private BuildText buildText;
    private BuildUrl buildUrl;

    public OperaField(UIComponent owner) {
        this(owner, res.getString(2, "操作"), 3);
        this.setReadonly(true);
    }

    public OperaField(UIComponent owner, String name, int width) {
        super(owner, name, width);
        this.setAlign("center");
        this.setField("_opera_");
        this.setCssClass("right");
    }

    @Override
    public String getText() {
        if (getBuildText() != null) {
            Record record = getRecord();
            HtmlWriter html = new HtmlWriter();
            getBuildText().outputText(record, html);
            return html.toString();
        }
        return this.value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public OperaField setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public OperaField setReadonly(boolean readonly) {
        super.setReadonly(true);
        return this;
    }

    @Override
    public UIDialogField getDialog() {
        return dialog;
    }

    @Override
    public OperaField setDialog(String dialogfun) {
        this.dialog = new UIDialogField(dialogfun);
        dialog.setInputId(this.getId());
        return this;
    }

    @Override
    public OperaField createText(BuildText buildText) {
        this.buildText = buildText;
        return this;
    }

    @Override
    public BuildText getBuildText() {
        return buildText;
    }

    @Override
    public OperaField createUrl(BuildUrl buildUrl) {
        this.buildUrl = buildUrl;
        return this;
    }

    @Override
    public BuildUrl getBuildUrl() {
        return buildUrl;
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
            if (getHtmType() != null) {
                html.print(" type=\"%s\"", this.getHtmType());
            } else {
                html.print(" type=\"text\"");
            }
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
            html.println("/>");

            html.print("<span>");
            if (dialog != null && dialog.isOpen()) {
                dialog.setConfig(config).output(html);
            }
            html.println("</span>");
        }
    }

    @Override
    public void outputColumn(HtmlWriter html) {
        Record record = getRecord();

        if (this.getBuildUrl() != null) {
            UrlRecord url = new UrlRecord();
            this.getBuildUrl().buildUrl(record, url);
            if (!"".equals(url.getUrl())) {
                html.print("<a href=\"%s\"", url.getUrl());
                if (url.getTitle() != null) {
                    html.print(" title=\"%s\"", url.getTitle());
                }
                if (url.getTarget() != null) {
                    html.print(" target=\"%s\"", url.getTarget());
                }
                if (url.getHintMsg() != null) {
                    html.print(" onClick=\"return confirm('%s');\"", url.getHintMsg());
                }
                html.print(">%s</a>", this.getText());
            } else {
                html.print(this.getText());
            }
        } else {
            html.print(this.getText());
        }
    }

}
