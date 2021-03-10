package cn.cerc.ui.parts;

import cn.cerc.core.ClassConfig;
import cn.cerc.mis.cdn.CDN;
import cn.cerc.ui.SummerUI;
import cn.cerc.ui.core.HtmlWriter;

public class UIAdvertisement extends UIComponent {
    private static final ClassConfig config = new ClassConfig(UIAdvertisement.class, SummerUI.ID);

    public UIAdvertisement(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<div class=\"ad\">");
        html.println("<div class=\"ban_javascript clear\">");
        html.println("<ul>");
        html.println("<li><img src=\"%s\"></li>", CDN.get(config.getClassProperty("icon", "")));
        html.println("</ul>");
        html.println("</div>");
        html.println("</div>");
    }
}
