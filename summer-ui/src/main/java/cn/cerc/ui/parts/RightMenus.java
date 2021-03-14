package cn.cerc.ui.parts;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.cerc.core.ISession;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.Handle;
import cn.cerc.ui.core.IRightMenuLoad;
import cn.cerc.ui.mvc.IMenuBar;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RightMenus extends Handle {
    private List<IMenuBar> items = new ArrayList<>();

    public List<IMenuBar> getItems() {
        return items;
    }

    public void setItems(List<IMenuBar> items) {
        this.items = items;
    }

    @Override
    public void setSession(ISession session) {
        super.setSession(session);

        IRightMenuLoad child = Application.getBeanDefault(IRightMenuLoad.class, session);
        if (child != null)
            child.loadMenu(items);

    }
}
