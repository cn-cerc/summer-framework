package cn.cerc.mis.mail;

import java.util.ArrayList;

public class HtmlControl extends HtmlComponent {
    private HtmlControl parent;
    private ArrayList<HtmlControl> controls = new ArrayList<HtmlControl>();

    public HtmlControl(HtmlControl owner) {
        this.init(owner);
        this.parent = owner;
        if (owner != null) {
            owner.addControl(this);
        }
    }

    private void addControl(HtmlControl child) {
        this.controls.add(child);
    }

    public int getControlCount() {
        return getControls().size();
    }

    public HtmlControl getParent() {
        return parent;
    }

    public void setParent(HtmlControl parent) {
        this.parent = parent;
    }

    public ArrayList<HtmlControl> getControls() {
        return controls;
    }

    public void getHtml(StringBuffer html) {
        for (HtmlControl obj : this.getControls()) {
            obj.getHtml(html);
        }
    }

}
