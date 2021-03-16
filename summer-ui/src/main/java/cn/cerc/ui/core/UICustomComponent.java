package cn.cerc.ui.core;

import java.lang.reflect.InvocationTargetException;

import cn.cerc.ui.parts.UIComponent;

public class UICustomComponent extends UIComponent {
    
    public UICustomComponent() {
        super();
    }

    public UICustomComponent(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        for (Component component : this.getComponents()) {
            if (component instanceof UIComponent) {
                ((UIComponent) component).output(html);
            }
        }
    }

    @Deprecated
    public <T> T create(Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        T obj = clazz.getDeclaredConstructor().newInstance();
        if (!(obj instanceof Component)) {
            throw new RuntimeException("仅支持Component及其子数，不支持创建类型: " + clazz.getName());
        }
        Component item = (Component) obj;
        item.setOwner(this);
        return obj;
    }

}
