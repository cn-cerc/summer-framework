package cn.cerc.ui.mvc;

import cn.cerc.mis.core.IForm;
import cn.cerc.ui.core.UrlRecord;

import java.util.List;

public interface IMenuBar {
    // 登记菜单栏菜单项
    int enrollMenu(IForm form, List<UrlRecord> menus);
}
