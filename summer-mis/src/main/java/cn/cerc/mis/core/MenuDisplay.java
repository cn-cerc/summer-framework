package cn.cerc.mis.core;

import lombok.Getter;
import lombok.Setter;

/**
 * 用于构建右边菜单的显示
 */
@Deprecated
//TODO MenuDisplay 此对象不应该存在框架中
@Setter
@Getter
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
}
