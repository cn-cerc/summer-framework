package cn.cerc.ui.parts;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import cn.cerc.mis.core.AbstractJspPage;
import cn.cerc.mis.core.IForm;
import cn.cerc.ui.core.Component;
import cn.cerc.ui.core.HtmlWriter;

public class UIFooter extends UIComponent {
    private static final int MAX_MENUS = 6;
    // protected UrlRecord checkAll;
    private boolean flag = false;
    private UIFooterOperation operation;
    private List<UIBottom> buttons = new ArrayList<>();
    private boolean showHotKeyName = false; // 是否显示快捷键名称

    public UIFooter(UIComponent owner) {
        super(owner);
        this.setId("bottom");
    }

    @Override
    @Deprecated
    public void setOwner(Component owner) {
        super.setOwner(owner);
    }

    public void setCheckAllTargetId(String targetId) {
        this.getOperation().getCheckAll().setTargetId(targetId);
    }

    @Override
    public void output(HtmlWriter html) {
        if (this.getComponents().size() > MAX_MENUS)
            throw new RuntimeException(String.format("底部菜单区最多只支持 %d 个菜单项", MAX_MENUS));

        if (this.buttons.size() > MAX_MENUS)
            throw new RuntimeException(String.format("底部菜单区最多只支持 %d 个菜单项", MAX_MENUS));

        html.print("<footer role='footer'");
        if (isEmpty()) {
            html.print(" style='display:none'");
        }
        html.println(">");
        if (this.operation != null) {
            html.println("<section role='footerOperation'>");
            this.operation.output(html);
            html.println("</section>");
            html.println("<section role='footerTools'>");
        } else {
            html.println("<section role='footerButtons'>");
        }
        for (Component component : this.getComponents()) {
            if (component != this.operation && component instanceof UIComponent)
                ((UIComponent) component).output(html);
        }
        html.println("</section>");
        HttpServletRequest request = getForm().getRequest();
        if (request != null) {
            if (!getForm().getClient().isPhone()) {
                html.print("<div class=\"bottom-message\"");
                html.print(" id=\"msg\">");
                String msg = request.getParameter("msg");
                if (msg != null)
                    html.print(msg.replaceAll("\r\n", "<br/>"));
                html.println("</div>");
            }
        }
        html.print("</footer>");
    }

    public IForm getForm() {
        return ((AbstractJspPage) this.getOwner()).getForm();
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public List<UIBottom> getButtons() {
        return buttons;
    }

    public UIBottom addButton() {
        UIBottom button = new UIBottom(this);
        buttons.add(button);
        return button;
    }

    public void addButton(String caption, String url) {
        int count = 1;
        for (Component obj : this.getComponents()) {
            if (obj instanceof UIBottom) {
                count++;
            }
        }
        UIBottom item = addButton();
        item.setCaption(caption);
        item.setUrl(url);

        item.setCssClass("bottomBotton");
        item.setId("button" + count);
        if (!getForm().getClient().isPhone()) {
            if (showHotKeyName) {
                item.setCaption(String.format("F%s:%s", count, item.getName()));
            } else {
                item.setCaption(item.getName());
            }
        }
    }

    public UIFooterOperation getOperation() {
        if (operation == null)
            operation = new UIFooterOperation(this);
        return operation;
    }

    private boolean isEmpty() {
        return this.operation == null && buttons.size() == 0;
    }
}
