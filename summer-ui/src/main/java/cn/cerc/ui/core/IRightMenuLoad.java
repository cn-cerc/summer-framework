package cn.cerc.ui.core;

import java.util.List;

import cn.cerc.db.core.IHandle;
import cn.cerc.ui.mvc.IMenuBar;

public interface IRightMenuLoad extends IHandle{

    void loadMenu(List<IMenuBar> items);

}
