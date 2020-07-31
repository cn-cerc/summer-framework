package cn.cerc.ui.parts;

import cn.cerc.core.Utils;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.UrlRecord;
import cn.cerc.ui.vcl.UIButton;
import cn.cerc.ui.vcl.UIImage;
import cn.cerc.ui.vcl.ext.UISpan;

import java.util.ArrayList;
import java.util.List;

public class UISheetMenu extends UISheet {
    private List<UrlRecord> menus = new ArrayList<>();
    private UIImage icon; // 显示icon
    private UISpan caption; // 标题
    private UIButton opera; // 操作

    public UISheetMenu(UIToolbar owner) {
        super(owner);
        this.setCssClass("menuList");
    }

    @Override
    public void output(HtmlWriter html) {
        if (menus.size() == 0) {
            return;
        }

        html.println("<section class='%s'>", this.cssClass);
        if (caption != null) {
            html.println("<div class='nowpage'>");
            // icon.output(html);
            caption.output(html);
            if (opera != null) {
                opera.output(html);
            }
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
            if (url.isWindow()) {
                String hrip = "hrip:" + url.getUrl();
                html.print(" <a href='%s' class='erp_menu'/><img src='%s' role='icon'/></a>", hrip, "images/menu/erp-blue.png");
            }
            if (Utils.isNotEmpty(url.getArrow())) {
                html.println("<img src='%s' role='arrow' />", url.getArrow());
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

    public void setIconAndCaption(String logoUrl, String caption) {
        this.icon = new UIImage();
        this.icon.setSrc(logoUrl);
        this.caption = new UISpan();
        this.caption.setText(caption);
    }

    public void setIconAndCaption(String logoUrl, String caption, UIButton opera) {
        this.icon = new UIImage();
        this.icon.setSrc(logoUrl);
        this.caption = new UISpan();
        this.caption.setText(caption);
        this.opera = opera;
    }
}
