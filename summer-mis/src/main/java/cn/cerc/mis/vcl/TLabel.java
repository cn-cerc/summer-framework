package cn.cerc.mis.vcl;

import java.awt.Container;

import javax.swing.JLabel;

@SuppressWarnings("serial")
public class TLabel extends JLabel {

    public TLabel(Container owner) {
        super();
        if (owner instanceof TCustomForm)
            ((TCustomForm) owner).getContent().add(this);
        else
            owner.add(this);
    }

    public static TLabel Create(Container owner) {
        return new TLabel(owner);
    }

}
