package cn.cerc.mis.vcl;

import java.awt.Container;

import javax.swing.JTextField;

@SuppressWarnings("serial")
public class TEdit extends JTextField {

    public TEdit(Container owner) {
        super();
        if (owner instanceof TCustomForm)
            ((TCustomForm) owner).getContent().add(this);
        else
            owner.add(this);
        this.setColumns(30);
    }
}
