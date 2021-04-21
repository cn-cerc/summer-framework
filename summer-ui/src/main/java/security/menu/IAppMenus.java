package security.menu;

import java.util.List;

import cn.cerc.db.core.ISessionOwner;

public interface IAppMenus extends ISessionOwner{

    List<IMenuItem> getItems();

    IMenuItem getItem(String menuCode);

}
