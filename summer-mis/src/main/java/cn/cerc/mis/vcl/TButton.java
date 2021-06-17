package cn.cerc.mis.vcl;

import java.awt.Container;
import java.awt.event.ActionListener;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class TButton extends JButton {
    
    public TButton(Container owner) {
        super();
        if (owner instanceof TCustomForm)
            ((TCustomForm) owner).getContent().add(this);
        else
            owner.add(this);
        this.setSize(125, 25);
    }

    public void setOnClick(ActionListener owner) {
        this.addActionListener(owner);
    }
}
