package cn.cerc.ui.vcl;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class UIRadioButton extends UIComponent {
    private String name = "";
    private String value = "";
    private boolean isSelected = false;
    private UILabel label;

    @Override
    public void output(HtmlWriter html) {
        if (label != null)
            label.output(html);
        html.print("<input type='radio'");
        if (isSelected)
            html.print(" checked=checked");
        html.print(" name='%s' value='%s'", name, value);
        super.outputCss(html);
        html.println("/>");
    }

    public UIRadioButton(UIComponent owner) {
        super(owner);
    }

    public UIRadioButton() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public UILabel getLabel() {
        if (label == null)
            label = new UILabel();
        return label;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

}
