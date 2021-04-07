package cn.cerc.sms;

public abstract class CustomSMS {

    private String message;

    private String templateText;

    public abstract boolean send(String mobile, String templateId, String... args);

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTemplateText() {
        return templateText;
    }

    public void setTemplateText(String templateText) {
        this.templateText = templateText;
    }

}
