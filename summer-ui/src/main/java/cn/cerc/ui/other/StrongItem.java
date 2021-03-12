package cn.cerc.ui.other;

import cn.cerc.mis.config.ApplicationConfig;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

import java.text.DecimalFormat;

public class StrongItem extends UIComponent {
    private String name;
    private Double value;
    private String percentSign;

    public StrongItem(UIComponent owner) {
        super(owner);
    }

    public String getName() {
        return name;
    }

    public StrongItem setName(String name) {
        this.name = name;
        return this;
    }

    public Double getValue() {
        return value;
    }

    public StrongItem setValue(Double value) {
        this.value = value;
        return this;
    }

    public String getPercentSign() {
        return percentSign;
    }

    public StrongItem setPercentSign(String percentSign) {
        this.percentSign = percentSign;
        return this;
    }

    @Override
    public void output(HtmlWriter html) {
        DecimalFormat df = new DecimalFormat(ApplicationConfig.getPattern());
        html.print("%s：", this.getName());
        html.print("<strong");
        if (this.getId() != null) {
            html.print(" id=\"%s\"", this.getId());
        }
        html.print(">");
        html.print(df.format(this.value));
        if (this.getPercentSign() != null) {
            html.print(this.percentSign);
        }
        html.print("</strong>");
    }
}
