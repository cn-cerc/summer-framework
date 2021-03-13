package cn.cerc.ui.page;

import cn.cerc.mis.core.IForm;
import cn.cerc.mis.core.RequestData;
import cn.cerc.ui.parts.UIDocument;
import cn.cerc.ui.parts.UIMenuList;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * 系统登录页
 */
public class JspPageLike extends JspPageDialog {
    private UIMenuList menus;
    // 主体: 控制区(可选)+内容+消息区
    private UIDocument document;
    
    public JspPageLike(IForm form) {
        super(form);
    }

    @Override
    public String execute() throws ServletException, IOException {
        if (menus != null) {
            add("menus", menus);
        }
        return super.execute();
    }

    public UIMenuList getMenus() {
        if (menus == null) {
            menus = new UIMenuList(this.getDocument().getContent());
        }
        return menus;
    }

    public UIDocument getDocument() {
        if (document == null) {
            document = new UIDocument(this);
        }
        return document;
    }

    public void setMenus(Object value) {
        super.put("menus", value);
    }

    public void setDisableAccountSave(boolean value) {
        super.put("disableAccountSave", value);
    }

    public void setDisablePasswordSave(boolean value) {
        super.put("DisablePasswordSave", value);
    }

    public void setStartVine(boolean value) {
        super.put("startVine", value);
    }

    public void setStartHost(String value) {
        super.add("startHost", value);
    }

    public void setSessionKey(Object value) {
        super.put(RequestData.TOKEN, value);
    }

    public void setOnlineUsers(int value) {
        super.put("onlineUsers", value);
    }

    public void setCurrentUserCode(String value) {
        super.add("currentUserCode", value);
    }

    public void setCurrentUserName(String value) {
        super.add("currentUserName", value);
    }

    public void setCurrentCorpName(String value) {
        super.add("currentCorpName", value);
    }

    public void setIsViewOldMenu(boolean value) {
        super.put("isViewOldMenu", value);
    }

    public void setUnReadMessage(int value) {
        super.put("unReadMessage", value);
    }
}
