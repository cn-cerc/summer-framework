package cn.cerc.mis.vcl;

import java.awt.BorderLayout;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

@SuppressWarnings("serial")
public class TMainForm extends TCustomForm {
    private final JMenuBar mainMenu;
    private JMenu mnuClose;
    private final TStatusBar statusBar;

    public TMainForm() {
        super();
        mainMenu = new JMenuBar();
        this.setJMenuBar(mainMenu);
        mnuClose = new JMenu("exit");
        mainMenu.add(mnuClose);

        this.statusBar = new TStatusBar();
        this.add(statusBar, BorderLayout.SOUTH);
    }

    public TStatusBar getStatusBar() {
        return statusBar;
    }

    public JMenuBar getMainMenu() {
        return mainMenu;
    }
}
