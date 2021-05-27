package cn.cerc.mis.vcl;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class TMainForm extends TCustomForm implements ActionListener {
    private final JMenuBar mainMenu;
    private final TStatusBar statusBar;
    private final JMenuItem mnuOpenFile;

    public TMainForm() {
        super();
        this.statusBar = new TStatusBar();
        this.add(statusBar, BorderLayout.SOUTH);

        mainMenu = new JMenuBar();
        this.setJMenuBar(mainMenu);

        JMenu mnuFile = new JMenu("File");
        mainMenu.add(mnuFile);

        mnuOpenFile = new JMenuItem("open file");
        mnuFile.add(mnuOpenFile);
        mnuOpenFile.addActionListener(this);

        JMenuItem mnuExit = new JMenuItem("exit");
        mnuFile.add(mnuExit);

        mnuExit.addActionListener((e) -> {
            this.dispose();
            System.exit(0);
        });
    }

    public TStatusBar getStatusBar() {
        return statusBar;
    }

    public JMenuBar getMainMenu() {
        return mainMenu;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == mnuOpenFile) {
            JFileChooser chooseFile = new JFileChooser();
            int returnVal = chooseFile.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File f = chooseFile.getSelectedFile();
                String message = "你选择了文件：" + chooseFile.getName(f);
                if (confirm(message))
                    statusBar.setText(message);
            }
        } else {
            statusBar.setText("sender: " + e.getSource().getClass().getName());
        }
    }

    public boolean confirm(String message) {
        return JOptionPane.showConfirmDialog(null, message, "确认", JOptionPane.ERROR_MESSAGE) == JOptionPane.YES_OPTION;
    }
}
