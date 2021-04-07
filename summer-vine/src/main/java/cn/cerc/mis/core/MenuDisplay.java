package cn.cerc.mis.core;

/**
 * 用于构建右边菜单的显示
 */
public class MenuDisplay {

    // 菜单代码
    private String code;

    // 菜单标题
    private String title;

    // 菜单编号，一般为数字
    private String pageNo;

    // 上级菜单，若无，则为
    private String parent;

    /**
     * 菜单图标，为URL值
     * <p>
     * 菜单图标不需要从此项进行设置，而是根据菜单代码从静态资源文件或者oss直接读取
     */
    private String icon;

    // 菜单分组
    private String group;

    // 是否为原生窗口
    private boolean window;

    // 是否支持手机
    private boolean phone;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public boolean isWindow() {
        return window;
    }

    public void setWindow(boolean window) {
        this.window = window;
    }

    public boolean isPhone() {
        return phone;
    }

    public void setPhone(boolean phone) {
        this.phone = phone;
    }
}
