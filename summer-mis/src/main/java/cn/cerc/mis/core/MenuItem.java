package cn.cerc.mis.core;

import java.util.HashMap;
import java.util.Map;

// FIXME 2019-12-25 此对象的静态变量应改为属性 
// 此对象应该换为 MenuItem
public class MenuItem {
    // 菜单标题
    public static final String TITLE = "title";
    // 菜单编号，一般为数字
    public static final String PAGENO = "formNo";
    // 软件类别，如 1,2,，其中1及2各代表一种软件
    public static final String SOFTWARE = "versions";
    // 菜单授权码
    public static final String PERMISSION = "permission";
    // true: 需要登录方可使用
    public static final String SECURITY = "security";
    // 上级菜单，若无，则为""
    public static final String PARENT = "parentId";
    // 菜单图标，为URL值
    public static final String IMAGE = "image";
    // 菜单分组
    public static final String GROUP = "group";
    // 菜单代码
    private String code;
    private boolean window;

    // 所有参数值
    private Map<String, String> params = new HashMap<>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        String result = this.getParam(TITLE);
        return result != null ? result : "";
    }

    public String getPageNo() {
        String result = this.getParam(PAGENO);
        return result != null ? result : "";
    }

    public boolean isSecurityEnabled() {
        String result = this.getParam(SECURITY);
        return result == null || "true".equals(result);
    }

    public String getPermissionCode() {
        String result = this.getParam(PERMISSION);
        return result != null ? result : "";
    }

    public String getSoftwareList() {
        String result = this.getParam(SOFTWARE);
        return result != null ? result : "";
    }

    public String getParentId() {
        String result = this.getParam(PARENT);
        return result != null ? result : "";
    }

    public void setImage(String image) {
        this.setParam(IMAGE, image);
    }

    public String getImage() {
        String result = this.getParam(IMAGE);
        return result != null ? result : "";
    }

    public void setParam(String key, String value) {
        params.put(key, value);
    }

    private String getParam(String key) {
        return params.get(key);
    }

    @Deprecated
    public Map<String, String> getParams() {
        return params;
    }

    @Deprecated
    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public boolean isWindow() {
        return window;
    }

    public void setWindow(boolean window) {
        this.window = window;
    }
    
    public String getGroup() {
        String result = this.getParam(GROUP);
        return result != null ? result : "";
    }

}
