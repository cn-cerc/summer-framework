package cn.cerc.ui.parts;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.ClassResource;
import cn.cerc.core.IUserLanguage;
import cn.cerc.core.Utils;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.ITokenManage;
import cn.cerc.mis.cdn.CDN;
import cn.cerc.mis.config.ApplicationConfig;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.IClient;
import cn.cerc.mis.core.IForm;
import cn.cerc.mis.language.R;
import cn.cerc.ui.SummerUI;
import cn.cerc.ui.core.Component;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.ICorpInfo;
import cn.cerc.ui.core.UrlRecord;
import cn.cerc.ui.mvc.AbstractPage;
import cn.cerc.ui.phone.Block104;

public class UIHeader extends UICssComponent implements IUserLanguage {
    private static final ClassConfig config = new ClassConfig(UIHeader.class, SummerUI.ID);
    private final ClassResource res = new ClassResource(this, SummerUI.ID);

    private static final int MAX_MENUS = 4;
    private UIAdvertisement advertisement; // 可选
    // 页面标题
    private String pageTitle = null;
    // 首页
    private UrlRecord homePage;
    // 左边菜单
    private List<UrlRecord> leftMenus = new ArrayList<>();
    // 菜单搜索区域
    private Block104 menuSearchArea = null;
    // 左菜单按钮
    private List<UIBottom> leftBottom = new ArrayList<>();
    // 右边菜单
    private List<UrlRecord> rightMenus = new ArrayList<>();
    // 当前用户
    private String userName;
    // 欢迎语
    private String welcome;
    // logo图标src
    private String logoSrc;
    // 当前用户
    private String currentUser;
    // 当前帐套名称
    private String corpNoName;
    // 退出
    private UrlRecord exitPage = null;
    // 退出系统
    private UrlRecord exitSystem = null;
    // 菜单模组
    private String moduleCode = null;

    private IHandle handle;

    public void setHeadInfo(String logoSrc, String welcome) {
        this.logoSrc = logoSrc;
        this.welcome = welcome;
    }

    private String getHomeImage(AbstractPage owner) {
        String homeImg = config.getClassProperty("icon.home", "");
        if (owner.getForm().getClient().isPhone()) {
            String phoneIndex = config.getString("app.phone.home.image", null);
            if (Utils.isNotEmpty(homeImg)) {
                homeImg = phoneIndex;
            }
        }
        return String.format("<img src=\"%s%s\"/>", Application.getStaticPath(), homeImg);
    }

    private String getLogo() {
        String logo = config.getString("app.logo.src", null);
        if (Utils.isNotEmpty(logo)) {
            return CDN.get(logo);
        }
        return CDN.get(config.getClassProperty("icon.logo", ""));
    }

    public UIHeader(AbstractPage page) {
        super(page);
        this.handle = page.getForm().getHandle();

        String defaultPage = config.getString(Application.FORM_DEFAULT, "default");
        homePage = new UrlRecord(defaultPage, getHomeImage(page));
        leftMenus.add(homePage);

        homePage = new UrlRecord(defaultPage, res.getString(1, "开始"));

        IClient client = page.getForm().getClient();
        boolean isShowBar = config.getBoolean("app.ui.head.show", true);
        if (!client.isPhone() && isShowBar) {
            String token = (String) handle.getSession().getProperty(Application.TOKEN);
            ITokenManage manage = Application.getBeanDefault(ITokenManage.class, handle.getSession());
            manage.resumeToken(token);
            currentUser = res.getString(2, "用户");
            leftMenus.add(homePage);
            this.userName = handle.getSession().getUserName();
            if (Utils.isNotEmpty(handle.getCorpNo())) {
                ICorpInfo info = Application.getBeanDefault(ICorpInfo.class, handle.getSession());
                this.corpNoName = info.getShortName();
            }
            logoSrc = getLogo();
            welcome = config.getString("app.welcome.language", res.getString(3, "欢迎使用系统"));

            String exitName = config.getString("app.exit.name", "#");
            String exitUrl = config.getString("app.exit.url", null);
            exitSystem = new UrlRecord();
            exitSystem.setName(exitName).setSite(exitUrl);
        }
    }

    @Override
    @Deprecated
    public void setOwner(Component owner) {
        super.setOwner(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        if (this.leftBottom.size() > MAX_MENUS) {
            throw new RuntimeException(String.format(res.getString(4, "底部菜单区最多只支持 %d 个菜单项"), MAX_MENUS));
        }

        html.print("<header role='header'");
        super.outputCss(html);
        html.println(">");
        if (!"".equals(this.userName) && this.userName != null) {
            html.print("<div class='titel_top'>");
            html.print("<div class='logo_box'>");
            html.print("<img src='%s'/>", logoSrc);
            html.print("</div>");
            html.print("<span>%s</span>", welcome);
            html.print("<div class='user_right'>");
            html.print(
                    "<span>%s：<i><a href='%sTFrmChooseAccount' style='margin-left:0.5em;'>%s</a></i><i>/</i><i>%s</i></span>",
                    currentUser, ApplicationConfig.App_Path, corpNoName, userName);
            html.print("<a href='%s'>%s</a>", exitSystem.getUrl(), exitSystem.getName());
            html.print("</div>");
            html.print("</div>");
        }
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
                if (i > 1) {
                    html.println("<span>></span>");
                }
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
        if (menuSearchArea != null) {
            menuSearchArea.output(html);
        }
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
        if (advertisement == null) {
            advertisement = new UIAdvertisement(this);
        }
        return advertisement;
    }

    public void initHeader() {
        IForm form = ((AbstractPage) this.getOwner()).getForm();
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
            leftMenus.add(new UrlRecord("/", res.getString(5, "首页")));
            leftMenus.add(new UrlRecord("javascript:history.go(-1);", res.getString(6, "刷新")));
        }
        // 兼容老的jsp文件使用
        form.getRequest().setAttribute("barMenus", leftMenus);
        form.getRequest().setAttribute("subMenus", rightMenus);
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModule(String moduleCode, String moduleName) {
        this.moduleCode = moduleCode;
        if (!"".equals(moduleCode)) {
            this.addLeftMenu(moduleCode, moduleName);
        }
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
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
        if (exitPage == null) {
            exitPage = new UrlRecord();
        }
        exitPage.setName("<img src=\"images/return.png\"/>");
        exitPage.setSite(url);
    }

    public void addRightMenu(String url, String name) {
        addRightMenu(new UrlRecord(url, name));
    }

    public void addRightMenu(UrlRecord urlRecord) {
        rightMenus.add(urlRecord);
    }

    public void addLeftMenu(String url, String name) {
        leftMenus.add(new UrlRecord(url, name));
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
            item.setCaption(String.format("<img src='%s' />%s", iconUrl, item.getName()));
        }
    }

    public IForm getForm() {
        return ((AbstractPage) this.getOwner()).getForm();
    }

    public String getUserName() {
        return userName;
    }

    public String getWelcome() {
        return welcome;
    }

    public String getLogoSrc() {
        return logoSrc;
    }

    public UrlRecord getExitSystem() {
        return exitSystem;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public Block104 getMenuSearchArea() {
        if (menuSearchArea == null) {
            menuSearchArea = new Block104(this);
        }
        return menuSearchArea;
    }

    @Override
    public String getLanguageId() {
        return R.getLanguageId(handle);
    }

}
