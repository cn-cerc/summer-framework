package cn.cerc.ui.parts;

import cn.cerc.mis.core.MenuItem;
import cn.cerc.ui.core.HtmlWriter;

public class UIMenuItem extends UIComponent {
    private String img = "";
    private String name;
    private String code;
    private int hrip;
    private boolean delphi;
    private boolean menuLock;

    private String target = "_blank";

    public UIMenuItem(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        // 输出菜单图像
        html.println("<div role='menuIcon'>");
        html.print("<a href='%s' onclick=\"updateUserHit('%s')\"", getCode(), getCode());
        if (target != null && !"".equals(target)) {
            html.print(" target='%s'", this.target);
        }
        html.println(">");
        html.println("<img src='%s'", getImg());
        if (menuLock) {
            html.println("role='menuLock'");
        }
        html.println(">");
        html.println("</a>");
        html.println("</div>");

        // 输出菜单名称
        html.println("<div role='menuName'>");
        if (getHrip() == 2 && isDelphi()) {
            html.println("<a href=\"hrip:%s\" onclick=\"updateUserHit('%s')\">", getCode(), getCode());
            // 闪电 ⚡ 标记
            html.println("<img src=\"%s\"/>", "images/lightning.png");
            html.println("</a>");
        }

        html.println("<a href='%s' onclick=\"updateUserHit('%s')\"", getCode(), getCode());
        if (target != null && !"".equals(target)) {
            html.print(" target='%s'", this.target);
        }
        html.println(">%s</a>", getName());
        html.println("</div>");
    }

    public UIMenuItem init(String name, String code, String img) {
        this.name = name;
        this.code = code;
        this.img = img;
        return this;
    }

    public UIMenuItem init(MenuItem item) {
        setHrip(item.getHrip());
        setCode(item.getId());

        String str = item.getTitle();
        str = str.substring(str.indexOf("]") + 1);
        str = str.substring(str.indexOf("\\") + 1);

        setName(str);
        setImg("menu/" + item.getId() + ".png");
        return this;
    }

    public String getImg() {
        return img;
    }

    public UIMenuItem setImg(String img) {
        this.img = img;
        return this;
    }

    public String getName() {
        return name;
    }

    public UIMenuItem setName(String name) {
        this.name = name;
        return this;
    }

    public String getCode() {
        return code;
    }

    public UIMenuItem setCode(String url) {
        this.code = url;
        return this;
    }

    public int getHrip() {
        return hrip;
    }

    public void setHrip(int hrip) {
        this.hrip = hrip;
    }

    public boolean isDelphi() {
        return delphi;
    }

    public UIMenuItem setDelphi(boolean delphi) {
        this.delphi = delphi;
        return this;
    }

    public String getTarget() {
        return target;
    }

    public UIMenuItem setTarget(String target) {
        this.target = target;
        return this;
    }

    public boolean isMenuLock() {
        return menuLock;
    }

    public UIMenuItem setMenuLock(boolean menuLock) {
        this.menuLock = menuLock;
        return this;
    }
}