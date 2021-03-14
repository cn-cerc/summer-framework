package cn.cerc.mis.core;

public class CustomForm extends AbstractForm {

    public void init(CustomForm owner) {
        super.init(owner);
    }

    @Override
    public IPage execute() throws Exception {
        JsonPage page = new JsonPage(this);
        page.put("class", this.getClass().getName());
        page.setResultMessage(false, "page is not defined.");
        return page;
    }

}
