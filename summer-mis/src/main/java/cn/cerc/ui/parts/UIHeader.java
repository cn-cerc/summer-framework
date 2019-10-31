package cn.cerc.ui.parts;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.mis.core.AbstractJspPage;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.IForm;
import cn.cerc.ui.core.Component;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.UrlRecord;

public class UIHeader extends UIComponent {
    private static final int MAX_MENUS = 4;
    private UIAdvertisement advertisement; // 可选
    // 页面标题
    private String pageTitle = null;
    // 首页
    private UrlRecord homePage;
    // 左边菜单
    private List<UrlRecord> leftMenus = new ArrayList<>();
    // 左菜单按钮
    private List<UIBottom> leftBottom = new ArrayList<>();
    // 右边菜单
    private List<UrlRecord> rightMenus = new ArrayList<>();
    // 退出
    private UrlRecord exitPage = null;

    public UIHeader(AbstractJspPage owner) {
        super(owner);
        homePage = new UrlRecord(Application.getAppConfig().getFormDefault(), "<img src=\"images/Home.png\"/>");
        leftMenus.add(homePage);
    }

    @Override
    @Deprecated
    public void setOwner(Component owner) {
        super.setOwner(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        if (this.leftBottom.size() > MAX_MENUS)
            throw new RuntimeException(String.format("底部菜单区最多只支持 %d 个菜单项", MAX_MENUS));

        html.print("<header role='header'");
        super.outputCss(html);
        html.println(">");
        if (advertisement != null) {
            html.println("<section role='advertisement'>");
            html.println(advertisement.toString());
            html.println("</section>");
        }
        html.println("<nav role='mainMenu'>");

        // 输出：左边
        html.println("<section role='leftMenu'>");
        if (leftMenus.size() > 0) {
            html.print("<ul>");
            int i = 0;
            for (UrlRecord menu : leftMenus) {
                html.print("<li>");
                if (i > 1)
                    html.println("<span>-></span>");
                html.print("<a href=\"%s\">%s</a>", menu.getUrl(), menu.getName());
                i++;
                html.print("</li>");
            }
            html.print("</ul>");
            if (leftBottom.size() > 0) {
                html.println("<div role='headerButtons'>");
                for (UIBottom bottom : leftBottom) {
                    bottom.output(html);
                }
                html.println("</div>");
            }
        }
        html.println("</section>");

        // 降序输出：右边
        html.println("<section role='rightMenu'>");
        if (rightMenus.size() > 0) {
            html.print("<ul>");
            int i = rightMenus.size() - 1;
            while (i > -1) {
                UrlRecord menu = rightMenus.get(i);
                html.print("<li>");
                html.print("<a href=\"%s\">%s</a>", menu.getUrl(), menu.getName());
                html.print("</li>");
                i--;
            }
            html.print("</ul>");
        }
        html.println("</section>");

        html.println("</nav>");
        html.println("</header>");
    }

    public UIAdvertisement getAdvertisement() {
        if (advertisement == null)
            advertisement = new UIAdvertisement(this);
        return advertisement;
    }

    public void initHeader() {
        IForm form = ((AbstractJspPage) this.getOwner()).getForm();
        // 刷新
        if (this.pageTitle != null) {
            leftMenus.add(new UrlRecord("javascript:location.reload()", this.pageTitle));
        }
        if (leftMenus.size() > 2) {
            if (form.getClient().isPhone()) {
                UrlRecord first = leftMenus.get(0);
                UrlRecord last = leftMenus.get(leftMenus.size() - 1);
                leftMenus.clear();
                leftMenus.add(first);
                leftMenus.add(last);
            }
        }
        if (leftMenus.size() == 0) {
            leftMenus.add(new UrlRecord("/", "首页"));
            leftMenus.add(new UrlRecord("javascript:history.go(-1);", "刷新"));
        }
        // 兼容老的jsp文件使用
        form.getRequest().setAttribute("barMenus", leftMenus);
        form.getRequest().setAttribute("subMenus", rightMenus);
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public void addLeftMenu(UrlRecord urlRecord) {
        leftMenus.add(urlRecord);
    }

    public void addRightMenu(UrlRecord urlRecord) {
        rightMenus.add(urlRecord);
    }

    public UrlRecord getHomePage() {
        return homePage;
    }

    public void setHomePage(UrlRecord homePage) {
        this.homePage = homePage;
    }

    public List<UrlRecord> getRightMenus() {
        return this.rightMenus;
    }

    public UrlRecord getExitPage() {
        return exitPage;
    }

    public void setExitPage(UrlRecord exitPage) {
        this.exitPage = exitPage;
    }

    public void setExitPage(String url) {
        if (exitPage == null)
            exitPage = new UrlRecord();
        exitPage.setName("<img src=\"images/return.png\"/>");
        exitPage.setSite(url);
    }

    public void addLeftMenu(String url, String name) {
        addLeftMenu(new UrlRecord(url, name));
    }

    public void addRightMenu(String url, String name) {
        addRightMenu(new UrlRecord(url, name));
    }

    public void setExitUrl(String url) {
        setExitPage(url);
    }

    public UIBottom addButton() {
        UIBottom button = new UIBottom(this);
        leftBottom.add(button);
        return button;
    }

    public void addButton(String caption, String url) {
        this.addButton(caption, url, null);
    }

    public void addButton(String caption, String url, String iconUrl) {
        int count = 1;
        for (Component obj : this.getComponents()) {
            if (obj instanceof UIBottom) {
                count++;
            }
        }
        UIBottom item = addButton();
        item.setCaption(caption);
        item.setUrl(url);
        item.setCssClass("bottomBotton");
        item.setId("button" + count);
        if (!getForm().getClient().isPhone()) {
            if (iconUrl == null || "".equals(iconUrl)) {
                iconUrl = String.format("images/icon%s.png", count);
            }
            item.setCaption(String.format("<img src='%s'/>%s", iconUrl, item.getName()));
        }
    }

    public IForm getForm() {
        return ((AbstractJspPage) this.getOwner()).getForm();
    }
}
