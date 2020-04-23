package cn.cerc.mis.core;

import lombok.Getter;
import lombok.Setter;

/**
 * 用于构建右边菜单的显示
 */
public class MenuItem {

    // 菜单代码
    @Setter
    @Getter
    private String code;

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

    // 是否为原生窗口
    @Setter
    @Getter
    private boolean window;

}
