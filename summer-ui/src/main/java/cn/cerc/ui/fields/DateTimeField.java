package cn.cerc.ui.fields;

import cn.cerc.core.Record;
import cn.cerc.mis.cdn.CDN;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IColumn;
import cn.cerc.ui.core.ISimpleLine;
import cn.cerc.ui.core.UrlRecord;
import cn.cerc.ui.other.BuildText;
import cn.cerc.ui.parts.UIComponent;

public class DateTimeField extends AbstractField implements IColumn, IFieldBuildText, ISimpleLine {

    private BuildText buildText;

    public DateTimeField(UIComponent owner, String name, String field) {
        super(owner, name, 10);
        this.setField(field);
        this.setAlign("center");
    }

    public DateTimeField(UIComponent owner, String name, String field, int width) {
        super(owner, name, width);
        this.setField(field);
    }

    @Override
    public String getText() {
        Record record = getRecord();
        if (record == null) {
            return null;
        }
        if (getBuildText() != null) {
            HtmlWriter html = new HtmlWriter();
            getBuildText().outputText(record, html);
            return html.toString();
        }
        return record.getString(getField());
    }

    @Override
    public DateTimeField createText(BuildText buildText) {
        this.buildText = buildText;
        return this;
    }

    @Override
    public BuildText getBuildText() {
        return buildText;
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

            if (this instanceof IFieldShowStar) {
                IFieldShowStar obj = (IFieldShowStar) this;
                if (obj.isShowStar()) {
                    html.println("<font>*</font>");
                }
            }

            html.print("<span>");
            if (this instanceof IFieldDialog) {
                IFieldDialog obj = (IFieldDialog) this;
                DialogField dialog = obj.getDialog();
                if (dialog != null && dialog.isOpen()) {
                    html.print("<a href=\"%s\">", dialog.getUrl());
                    if (obj.getIcon() != null) {
                        html.print("<img src=\"%s\">", obj.getIcon());
                    } else {
                        html.print("<img src=\"%s\">", CDN.get(config.getClassProperty("icon", "")));
                    }
                    html.print("</a>");
                    return;
                }
            }
            html.println("</span>");
        }
    }

    @Override
    public void outputColumn(HtmlWriter html) {
        Record record = getRecord();

        IFieldBuildUrl obj = null;
        if (this instanceof IFieldBuildUrl) {
            obj = (IFieldBuildUrl) this;
        }

        if (obj != null && obj.getBuildUrl() != null) {
            UrlRecord url = new UrlRecord();
            obj.getBuildUrl().buildUrl(record, url);
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
