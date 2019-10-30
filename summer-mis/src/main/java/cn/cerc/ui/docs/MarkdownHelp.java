package cn.cerc.ui.docs;

import cn.cerc.mis.core.IForm;
import cn.cerc.ui.parts.UISheetHelp;
import cn.cerc.ui.parts.UIToolBar;

public class MarkdownHelp extends UISheetHelp {

    public MarkdownHelp(UIToolBar owner) {
        super(owner);
    }

    public void loadResourceFile(IForm form, String mdFileName) {
        MarkdownDoc doc = new MarkdownDoc(form);
        doc.setOutHtml(true);
        this.setContent(doc.getContext("/docs/" + mdFileName, "(暂未编写相应的说明)"));
    }
}
