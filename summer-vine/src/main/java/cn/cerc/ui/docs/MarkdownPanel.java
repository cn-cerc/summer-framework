package cn.cerc.ui.docs;

import cn.cerc.core.ClassResource;
import cn.cerc.mis.core.IPage;
import cn.cerc.ui.SummerUI;
import cn.cerc.ui.other.OperaPanel;
import cn.cerc.ui.parts.UIComponent;

public class MarkdownPanel extends OperaPanel {
    private static final ClassResource res = new ClassResource(MarkdownPanel.class, SummerUI.ID);

    private String fileName;

    public MarkdownPanel(UIComponent owner) {
        super(owner);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        if (this.getOwner() instanceof IPage) {
            IPage page = (IPage) this.getOwner();
            MarkdownDoc doc = new MarkdownDoc(page.getForm());
            doc.setOutHtml(true);
            this.setReadme(doc.getContext("/docs/" + fileName, res.getString(1, "(暂未编写相应的说明)")));
        }
    }

}
