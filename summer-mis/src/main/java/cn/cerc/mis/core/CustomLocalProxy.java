package cn.cerc.mis.core;

public class CustomLocalProxy {
    private String service;
    private String message;

    public String getService() {
        return service;
    }

    public CustomLocalProxy setService(String service) {
        this.service = service;
        return this;
    }

    public String getMessage() {
        if (message != null) {
            return message.replaceAll("'", "\"");
        } else {
            return null;
        }
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
