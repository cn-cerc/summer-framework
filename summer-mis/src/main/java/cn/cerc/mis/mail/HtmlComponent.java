package cn.cerc.mis.mail;

import java.util.ArrayList;

/**
 * 用于组件组合，原命名为Component
 * 
 * @author 张弓
 *
 */
public class HtmlComponent {
    private ArrayList<HtmlComponent> components = new ArrayList<HtmlComponent>();
    private int tag;

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public final void init(HtmlComponent owner) {
        // 此函数专供后续对象覆盖使用
        if (owner != null) {
            owner.addComponent(this);
        }
    }

    private void addComponent(HtmlComponent child) {
        this.components.add(child);
    }

}
