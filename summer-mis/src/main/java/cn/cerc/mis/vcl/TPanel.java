package cn.cerc.mis.vcl;

import java.awt.Container;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class TPanel extends JPanel {

    public TPanel(Container owner) {
        super();
        if (owner instanceof TCustomForm)
            ((TCustomForm) owner).getContent().add(this);
        else
            owner.add(this);
    }
}
