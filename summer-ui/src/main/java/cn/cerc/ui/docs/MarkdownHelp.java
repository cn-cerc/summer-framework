package cn.cerc.ui.docs;

import cn.cerc.core.ClassResource;
import cn.cerc.mis.core.IForm;
import cn.cerc.ui.SummerUI;
import cn.cerc.ui.parts.UISheetHelp;
import cn.cerc.ui.parts.UIToolbar;

public class MarkdownHelp extends UISheetHelp {
    private static final ClassResource res = new ClassResource(MarkdownHelp.class, SummerUI.ID);

    public MarkdownHelp(UIToolbar owner) {
        super(owner);
    }

    public void loadResourceFile(IForm form, String mdFileName) {
        MarkdownDoc doc = new MarkdownDoc(form);
        doc.setOutHtml(true);
        this.setContent(doc.getContext("/docs/" + mdFileName, res.getString(1, "(暂未编写相应的说明)")));
    }
}
