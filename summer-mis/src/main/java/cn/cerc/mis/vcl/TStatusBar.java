package cn.cerc.mis.vcl;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class TStatusBar extends JPanel {
    private final JLabel label;

    public TStatusBar() {
        super();
        label = new JLabel();
        label.setVisible(true);
        this.add(label);
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.setBackground(new Color(200,200,200));
        label.setText("please");
    }

    public String getText() {
        return label.getText();
    }

    public void setText(String text) {
        label.setText(text);
    }

}
