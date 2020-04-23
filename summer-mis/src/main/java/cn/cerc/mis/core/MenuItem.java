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


    // 上级菜单，若无，则为
    @Setter
    @Getter
    private String parent;

    /**
     * 菜单图标，为URL值
     * <p>
     * 菜单图标不需要从此项进行设置，而是根据菜单代码从静态资源文件或者oss直接读取
     */
    @Setter
    @Getter
    private String icon;

    // 菜单分组
    @Setter
    @Getter
    private String group;

    // 是否为原生窗口
    @Setter
    @Getter
    private boolean window;

}
