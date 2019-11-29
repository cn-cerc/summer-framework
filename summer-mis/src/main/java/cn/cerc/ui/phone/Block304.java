package cn.cerc.ui.phone;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

/**
 * 用于显示类似日志，消息等
 * <p>
 * 标题
 * <p>
 * 描述信息
 * 
 * @author HuangRongjun
 *
 */
public class Block304 extends UIComponent {
    private String title = "(title)";
    private String describe = "(describe)";

    public Block304(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.print("<div class='block304'>");

        html.print("<div role='title'>");
        html.print("<span role='title'>%s</span>", this.title);
        html.print("</div>");

        html.print("<div role='describe'>%s</div>", this.describe);
        html.print("</div>");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

}
