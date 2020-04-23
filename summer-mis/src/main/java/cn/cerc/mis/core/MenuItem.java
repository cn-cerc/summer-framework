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
    @Setter
    @Getter
    private String parent;

    /**
     * 菜单图标，为URL值
     * <p>
     * 菜单图标不需要从此项进行设置，而是外部根据菜单代码直接读取
     */
    @Setter
    @Getter
    @Deprecated
    private String imageSrc;

    // 菜单分组
    @Setter
    @Getter
    private String group;

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

}
