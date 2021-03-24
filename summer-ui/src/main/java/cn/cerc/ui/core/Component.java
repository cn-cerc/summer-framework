package cn.cerc.ui.core;

import java.util.ArrayList;
import java.util.List;

public abstract class Component {
    private List<Component> components = new ArrayList<>();
    private Component owner;

    private String id;

    public Component() {

    }

    public Component(Component owner) {
        setOwner(owner);
    }

    @Deprecated
    public Component(Component owner, String id) {
        this.id = id;
        setOwner(owner);
    }

    public void addComponent(Component component) {
        if (!components.contains(component)) {
            components.add(component);
        }
    }

    public final Component getOwner() {
        return owner;
    }

    public void setOwner(Component owner) {
        this.owner = owner;
        if (owner != null) {
            owner.addComponent(this);
        }
    }

    public final List<Component> getComponents() {
        return components;
    }

    public final String getId() {
        return id;
    }

    public final Component setId(String id) {
        this.id = id;
        if (owner != null && id != null) {
            owner.addComponent(this);
        }
        return this;
    }

}
