package cn.cerc.ui.parts;

import cn.cerc.db.core.IHandle;
import cn.cerc.ui.mvc.IMenuBar;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RightMenus extends IHandle {
    private List<IMenuBar> items = new ArrayList<>();

    public List<IMenuBar> getItems() {
        return items;
    }

    public void setItems(List<IMenuBar> items) {
        this.items = items;
    }

}
