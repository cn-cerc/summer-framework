package cn.cerc.ui.vcl;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.ext.UISpan;

/**
 * 多行文本输入框
 *
 * @author 黄荣君
 */
//FIXME 应改为 UITextarea，ZhangGong 2021/3/19
public class UITextArea extends UIComponent {
    private UISpan caption;
    private String name;
    private String text;
    private String placeholder;
    private int cols;// 列
    private int rows;// 行
    private String onInput;
    private boolean autofocus;
    private boolean readonly;

    public UITextArea() {
    }

    public UITextArea(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        if (caption != null) {
            caption.output(html);
        }

        html.print("<textarea ");
        if (this.getId() != null) {
            html.print("id='%s' ", this.getId());
        }
        if (getName() != null) {
            html.print("name='%s' ", name);
        } else if (this.getId() != null) {
            html.print("name='%s' ", this.getId());
        }
        if (getRows() != 0) {
            html.print("rows='%s' ", rows);
        }
        if (getCols() != 0) {
            html.print("cols='%s' ", cols);
        }
        if (getPlaceholder() != null) {
            html.print("placeholder='%s' ", placeholder);
        }
        if (isReadonly()) {
            html.print("readonly='readonly' ");
        }
        if (getOnInput() != null) {
            html.print("oninput='%s' ", onInput);
        }
        if (isAutofocus()) {
            html.print("autofocus ");
        }
        super.outputCss(html);
        html.print(">");

        if (getText() != null) {
            html.print(text);
        }
        html.print("</textarea>");
    }

    public UISpan getCaption() {
        if (caption == null) {
            caption = new UISpan();
        }
        return caption;
    }

    public UITextArea setCaption(UISpan caption) {
        this.caption = caption;
        return this;
    }

    public String getName() {
        return name;
    }

    public UITextArea setName(String name) {
        this.name = name;
        return this;
    }

    public String getText() {
        return text;
    }

    public UITextArea setText(String text) {
        this.text = text;
        return this;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public UITextArea setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public int getCols() {
        return cols;
    }

    public UITextArea setCols(int cols) {
        this.cols = cols;
        return this;
    }

    public int getRows() {
        return rows;
    }

    public UITextArea setRows(int rows) {
        this.rows = rows;
        return this;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public UITextArea setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    public String getOnInput() {
        return onInput;
    }

    public void setOnInput(String onInput) {
        this.onInput = onInput;
    }

    public boolean isAutofocus() {
        return autofocus;
    }

    public void setAutofocus(boolean autofocus) {
        this.autofocus = autofocus;
    }

}
