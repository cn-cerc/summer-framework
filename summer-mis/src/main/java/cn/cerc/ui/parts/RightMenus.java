package cn.cerc.ui.parts;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.cerc.mis.core.AbstractHandle;
import cn.cerc.mis.page.IMenuBar;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RightMenus extends AbstractHandle {
    private List<IMenuBar> items = new ArrayList<>();

    public List<IMenuBar> getItems() {
        return items;
    }

    public void setItems(List<IMenuBar> items) {
        this.items = items;
    }

}
