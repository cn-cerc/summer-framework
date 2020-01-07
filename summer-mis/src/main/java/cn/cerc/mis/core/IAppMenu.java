package cn.cerc.mis.core;

import java.util.List;

import cn.cerc.core.IHandle;

public interface IAppMenu {

    // 返回系统菜单定义
    public MenuItem getItem(String menuId);

    // 返回系统所有的Module
    public List<MenuItem> getModule(IHandle handle);

    // 返回指定父菜单下的所有子菜单
    public List<MenuItem> getList(IHandle handle, String parentId, boolean security);
}
