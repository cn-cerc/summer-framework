package cn.cerc.mis.vcl;

import java.awt.BorderLayout;

@SuppressWarnings("serial")
public class TMainForm extends TCustomForm {
    private final TStatusBar statusBar;

    public TMainForm() {
        super();
        this.statusBar = new TStatusBar();
        this.add(statusBar, BorderLayout.SOUTH);
    }

    public TStatusBar getStatusBar() {
        return statusBar;
    }
}
