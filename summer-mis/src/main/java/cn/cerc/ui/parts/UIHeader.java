package cn.cerc.ui.parts;

import cn.cerc.core.IHandle;
import cn.cerc.core.Utils;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.mis.cdn.CDN;
import cn.cerc.mis.config.ApplicationConfig;
import cn.cerc.mis.core.AbstractJspPage;
import cn.cerc.mis.core.AppClient;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.IClient;
import cn.cerc.mis.core.IForm;
import cn.cerc.mis.language.R;
import cn.cerc.mis.services.BookInfoRecord;
import cn.cerc.mis.services.MemoryBookInfo;
import cn.cerc.ui.UIConfig;
import cn.cerc.ui.core.Component;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.UrlRecord;
import cn.cerc.ui.phone.Block104;

import java.util.ArrayList;
import java.util.List;

public class UIHeader extends UIComponent {
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
    // 当前帐套名称
    private String corpNoName;
    // 退出
    private UrlRecord exitPage = null;
    // 退出系统
    private UrlRecord exitSystem = null;
    // 菜单模组
    private String moduleCode = null;

    private final ServerConfig config = ServerConfig.getInstance();

    public void setHeadInfo(String logoSrc, String welcome) {
        this.logoSrc = logoSrc;
        this.welcome = welcome;
    }

    private String getHomeImage(AbstractJspPage owner) {
        String homeImg = UIConfig.home_index;
        if (owner.getForm().getClient().isPhone()) {
            String phoneIndex = config.getProperty("app.phone.home.image");
            if (Utils.isNotEmpty(homeImg)) {
                homeImg = CDN.get(phoneIndex);
            }
        }
        return String.format("<img src=\"%s\"/>", homeImg);
    }

    private String getLogo() {
        String logo = config.getProperty("app.logo.src");
        if (Utils.isNotEmpty(logo)) {
            return CDN.get(logo);
        }
        return UIConfig.app_logo;
    }

    public UIHeader(AbstractJspPage page) {
        super(page);
        homePage = new UrlRecord(Application.getAppConfig().getFormDefault(), getHomeImage(page));
        leftMenus.add(homePage);

        IHandle handle = page.getForm().getHandle();
        homePage = new UrlRecord(Application.getAppConfig().getFormDefault(), R.asString(handle, "开始"));

        IClient client = page.getForm().getClient();
        boolean isShowBar = "true".equals(config.getProperty("app.ui.head.show", "true"));
        if (!client.isPhone() && isShowBar) {
            String token = (String) handle.getProperty(Application.token);
            handle.init(token);
            leftMenus.add(homePage);
            this.userName = handle.getUserName();
            if (Utils.isNotEmpty(handle.getCorpNo())) {
                BookInfoRecord item = MemoryBookInfo.get(handle, handle.getCorpNo());
                this.corpNoName = item.getShortName();
            }
            logoSrc = getLogo();
            welcome = config.getProperty("app.welcome.language", "欢迎使用系统");

            String exitName = config.getProperty("app.exit.name", "#");
            String exitUrl = config.getProperty("app.exit.url");
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
            throw new RuntimeException(
                    String.format(R.asString(this.getForm().getHandle(), "左侧菜单区最多只支持 %d 个菜单项"), MAX_MENUS));
        }

        html.print("<header role='header'");
        super.outputCss(html);
        html.println(">");
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

        if (menuSearchArea != null) {
            html.println("<section role='center'>");
            menuSearchArea.output(html);
            html.println("</section>");
        }

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
        if (userName != null) {
            html.print(
                    "<span class='userName'><a href='%sTFrmChooseAccount' style='margin-left:0.5em;'>%s</a></i>[<i>%s</i>]</span>",
                    ApplicationConfig.App_Path, corpNoName, userName);
            IClient client = ((AbstractJspPage) this.getOwner()).getForm().getClient();
            if (!AppClient.ee.equals(client.getDevice())) {
                html.print("<a href='%s'>%s</a>", exitSystem.getUrl(), exitSystem.getName());
            }
        }
        html.println("</section>");
        html.println("</header>");
    }

    public UIAdvertisement getAdvertisement() {
        if (advertisement == null) {
            advertisement = new UIAdvertisement(this);
        }
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
            leftMenus.add(new UrlRecord("/", R.asString(this.getForm().getHandle(), "首页")));
            leftMenus.add(new UrlRecord("javascript:history.go(-1);", R.asString(this.getForm().getHandle(), "刷新")));
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
            this.addLeftMenu("FrmModule?module=" + moduleCode, moduleName);
        }
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public void addLeftMenu(UrlRecord urlRecord) {
        if (this.moduleCode == null) {
            this.moduleCode = urlRecord.getSite();
            urlRecord.setSite("FrmModule?module=" + urlRecord.getSite());
        }
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
        if (exitPage == null) {
            exitPage = new UrlRecord();
        }
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
            item.setCaption(String.format("<img src='%s' />%s", iconUrl, item.getName()));
        }
    }

    public IForm getForm() {
        return ((AbstractJspPage) this.getOwner()).getForm();
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

    public Block104 getMenuSearchArea() {
        if (menuSearchArea == null) {
            menuSearchArea = new Block104(this);
        }
        return menuSearchArea;
    }
}
