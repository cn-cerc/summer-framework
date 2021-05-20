package cn.cerc.mis.vcl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 窗口基类
 *
 * @author 张弓
 */
public class TCustomForm extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel content = new JPanel();
    private Position position = Position.Default;
    
    public enum Position {
        Default, ScreenCenter,
    }

    public TCustomForm() {
        this.setTitle("(未命名)");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        this.setSize(screenSize.width / 2, screenSize.height / 2);

//        this.setContentPane(content);
        
        this.add(content, BorderLayout.CENTER);
  
        this.setPosition(Position.ScreenCenter);
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        if (this.position != position) {
            if (position == Position.ScreenCenter)
                this.setLocationRelativeTo(null);
            this.position = position;
        }
    }

    public JPanel getContent() {
        return content;
    }

}
