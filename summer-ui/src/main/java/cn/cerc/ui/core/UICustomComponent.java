package cn.cerc.ui.core;

import java.lang.reflect.InvocationTargetException;

import cn.cerc.ui.parts.UIComponent;

public class UICustomComponent extends UIComponent {
    protected String cssClass;
    protected String cssStyle;
    
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

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getCssStyle() {
        return cssStyle;
    }

    @Deprecated
    public void setCssStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }

    @Deprecated
    protected void outputCss(HtmlWriter html) {
        if (this.cssClass != null) {
            html.print(" class='%s'", cssClass);
        }
        if (this.cssStyle != null) {
            html.print(" style='%s'", cssStyle);
        }
    }
}
