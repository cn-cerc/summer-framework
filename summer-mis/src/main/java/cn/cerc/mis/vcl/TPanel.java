package cn.cerc.mis.vcl;

import java.awt.Container;
import java.awt.FlowLayout;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class TPanel extends JPanel {
    private int align = FlowLayout.CENTER;

    public TPanel(Container owner) {
        super();
        if (owner instanceof TCustomForm)
            ((TCustomForm) owner).getContent().add(this);
        else
            owner.add(this);
    }

    public int getAlign() {
        return align;
    }

    public TPanel setAlign(int align) {
        if (this.align != align) {
            setLayout(new FlowLayout(align));
            this.align = align;
        }
        return this;
    }
}
