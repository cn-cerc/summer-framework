package cn.cerc.ui.phone;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UIImage;
import cn.cerc.ui.vcl.UITextBox;
import cn.cerc.ui.vcl.ext.UISpan;

/**
 * 
 * @author 张弓
 *
 */
public class Block402 extends UIComponent {
    private String title = "(title)";
    private UIImage product = new UIImage();
    private UIImage add = new UIImage();
    private UIImage diff = new UIImage();
    private UISpan describe = new UISpan();
    private UISpan remark = new UISpan();
    private UITextBox input = new UITextBox();
    private String role = new String();
    private String dataName = new String();
    private String dataJson = new String();

    /**
     * 进出库单据明细之显示与数量修改
     * 
     * @param owner
     *            内容显示区
     */
    public Block402(UIComponent owner) {
        super(owner);
        product.setRole("product");
        product.setSrc("jui/phone/block402-product.png");

        describe.setRole("describe");
        describe.setText("(describe)");

        remark.setRole("remark");
        remark.setText("(remark)");
        // 减号
        diff.setRole("diff");
        diff.setSrc("jui/phone/block402_diff.png");
        diff.setOnclick("diffClick()");
        // 加号
        add.setRole("add");
        add.setSrc("jui/phone/block402_add.png");
        add.setOnclick("addClick()");
        // 输入框
        input.setType("number");
        input.setValue("0");
        input.setMaxlength("10");
        input.setOnclick("inputEvent(value,this)");
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.print("<section class='block402'");

        if (!"".equals(this.role))
            html.print(" role='%s'", this.role);
        if (!"".equals(this.dataName))
            html.print(" data-%s='%s'", this.dataName, this.dataJson);

        html.print(">");
        html.print("<div class='up_con'>");
        product.output(html);
        html.print("<div class='name'>%s</div>", this.title);
        html.print("<div class='c_buy'>");
        describe.output(html);
        html.print("<span class='gobuy'>");
        diff.output(html);
        input.output(html);
        add.output(html);
        html.print("</span>	");
        html.print("</div>");
        html.print("</div>");
        html.print("<div class='info'>%s</div>", remark.toString());
        html.println("</section>");
    }

    public UISpan getDescribe() {
        return describe;
    }

    public void setDescribe(UISpan describe) {
        this.describe = describe;
    }

    public UITextBox getInput() {
        return input;
    }

    public UIImage getProduct() {
        return product;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UISpan getRemark() {
        return remark;
    }

    public UIImage getAdd() {
        return add;
    }

    public UIImage getDiff() {
        return diff;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }
}
