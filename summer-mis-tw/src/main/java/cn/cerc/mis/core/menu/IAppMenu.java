package cn.cerc.mis.core.menu;

import cn.cerc.db.core.IHandle;

import java.util.List;

public interface IAppMenu {

    // 返回系統菜單定義
    MenuItem getItem(String menuId);

    // 返回指定父菜單下的所有子菜單
    List<MenuItem> getList(IHandle handle, String parentId, boolean security);
}
