package cn.cerc.ui.columns;

import cn.cerc.mis.core.IForm;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IReadonlyOwner;
import cn.cerc.ui.core.UIOriginComponent;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UIImage;
import cn.cerc.ui.vcl.UIInput;
import cn.cerc.ui.vcl.UILabel;

public class ImageColumn extends AbstractColumn implements IDataColumn {
    private UIInput input = new UIInput(this);
    private UIComponent helper;
    private boolean readonly;
    private String width;
    private String height;

    public ImageColumn(UIComponent owner) {
        super(owner);
        if (owner instanceof IReadonlyOwner) {
            this.setReadonly(((IReadonlyOwner) owner).isReadonly());
        }
    }

    public ImageColumn(UIComponent owner, String name, String code) {
        super(owner);
        this.setCode(code).setName(name);
        if (owner instanceof IReadonlyOwner) {
            this.setReadonly(((IReadonlyOwner) owner).isReadonly());
        }
        input.setName(code);
    }

    public ImageColumn(UIComponent owner, String name, String code, int width) {
        super(owner);
        this.setCode(code).setName(name).setSpaceWidth(width);
        if (owner instanceof IReadonlyOwner) {
            this.setReadonly(((IReadonlyOwner) owner).isReadonly());
        }
        input.setName(code);
    }

    @Override
    public void outputCell(HtmlWriter html) {
        if (this.getOrigin() instanceof IForm) {
            IForm form = (IForm) this.getOrigin();
            if (form.getClient().isPhone()) {
                outputCellPhone(html);
                return;
            }
        }
        outputCellWeb(html);
    }

    private void outputCellWeb(HtmlWriter html) {
        String url = getRecord().getString(this.getCode());
        if (this.readonly) {
            UIImage img = new UIImage();
            img.setStaticPath("");
            img.setSrc(url);
            img.setWidth(this.getWidth());
            img.setHeight(this.getHeight());
            img.output(html);
            
        } else {
            input.setValue(url);
            input.output(html);
        }
    }

    private void outputCellPhone(HtmlWriter html) {
        String text = getRecord().getString(this.getCode());
        if (this.readonly) {
            html.print(getName() + "：");
            UIImage img = new UIImage();
            img.setStaticPath("");
            img.setSrc(text);
            img.setWidth(this.getWidth());
            img.setHeight(this.getHeight());
            img.output(html);
        } else {
            UILabel label = new UILabel();
            label.setFocusTarget(this.getCode());
            label.setText(getName() + "：");
            label.output(html);
            input.setId(getCode());
            input.setReadonly(readonly);
            input.setValue(text);
            input.output(html);
        }
    }

    @Override
    public void outputLine(HtmlWriter html) {
        String text = getRecord().getString(this.getCode());

        if (!this.isHidden()) {
            UILabel label = new UILabel();
            label.setFocusTarget(this.getCode());
            label.setText(getName() + "：");
            label.output(html);
        }

        input.setId(getId());
        input.setReadonly(readonly);
        UIImage img = new UIImage();
        img.setStaticPath("");
        img.setSrc(text);
        img.setWidth(this.getWidth());
        img.setHeight(this.getHeight());
        img.output(html);

        if (!this.isHidden()) {
            html.print("<span>");
            if (this.helper != null)
                helper.output(html);
            html.println("</span>");
        }
    }

    public UIComponent getHelper() {
        if (helper != null)
            helper = new UIOriginComponent(this);
        return helper;
    }

    @Override
    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public ImageColumn setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    public ImageColumn setPlaceholder(String placeholder) {
        input.setPlaceholder(placeholder);
        return this;
    }

    public String getPlaceholder() {
        return input.getPlaceholder();
    }

    @Override
    public boolean isHidden() {
        return input.isHidden();
    }

    @Override
    public ImageColumn setHidden(boolean hidden) {
        input.setHidden(hidden);
        return this;
    }

    public ImageColumn setInputType(String inputType) {
        input.setInputType(inputType);
        return this;
    }

    public String getHeight() {
        return height;
    }

    public ImageColumn setHeight(String height) {
        this.height = height;
        return this;
    }

    public String getWidth() {
        return width;
    }

    public ImageColumn setWidth(String width) {
        this.width = width;
        return this;
    }

}
