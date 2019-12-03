package cn.cerc.ui.parts;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.core.Utils;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.UrlRecord;
import cn.cerc.ui.vcl.UIImage;
import cn.cerc.ui.vcl.ext.UISpan;

public class UISheetMenu extends UISheet {
    private List<UrlRecord> menus = new ArrayList<>();
    private UIImage logo; // 显示logo
    private UISpan welcome; // 欢迎语

    public UISheetMenu(UIToolBar owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        if (menus.size() == 0)
            return;

        html.println("<section class='menuList'>");
        if (welcome != null) {
            html.println("<div>");
            logo.output(html);
            welcome.output(html);
            html.println("</div>");
        }
        html.println("<div>");
        html.println("<ul>");
        for (UrlRecord url : menus) {
            html.println("<li>");
            html.print("<img src='%s' role='icon'/>", url.getImgage());
            html.print("<a href='%s' onclick=\"updateUserHit('%s')\"", url.getUrl(), url.getUrl());
            if (url.getId() != null) {
                html.print(" id='%s'", url.getId());
            }
            if (url.getTitle() != null) {
                html.print(" title='%s'", url.getTitle());
            }
            if (url.getHintMsg() != null) {
                html.print(" onClick='return confirm('%s');'", url.getHintMsg());
            }
            if (url.getTarget() != null) {
                html.print(" target='%s'", url.getTarget());
            }
            html.print(">%s</a>", url.getName());
            if (Utils.isNotEmpty(url.getArrow())) {
                html.println("<img src='%s' role='arrow'/>", url.getArrow());
            }
            html.println("</li>");
        }
        html.println("</ul>");
        html.println("</div>");
        html.println("</section>");
    }

    public UrlRecord addMenu() {
        UrlRecord menu = new UrlRecord();
        menus.add(menu);
        return menu;
    }

    public UrlRecord addMenus(UrlRecord menu) {
        menus.add(menu);
        return menu;
    }

    public void setLogoAndWelcome(String logoUrl, String welcome) {
        this.logo = new UIImage();
        this.logo.setSrc(logoUrl);
        this.welcome = new UISpan();
        this.welcome.setText(welcome);
    }
}
