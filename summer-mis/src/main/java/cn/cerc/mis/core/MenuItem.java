package cn.cerc.mis.core;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于构建右边菜单的显示
 */
public class MenuItem {

    // 菜单标题
    @Setter
    @Getter
    private String title;

    // 菜单编号，一般为数字
    @Setter
    @Getter
    private String pageNo;

    // 软件类别，如 1,2,，其中1及2各代表一种软件
    @Setter
    @Getter
    private String versions;

    // 菜单授权码
    @Setter
    @Getter
    private String proccode;

    // true: 需要登录方可使用
    @Setter
    @Getter
    private String security;

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
