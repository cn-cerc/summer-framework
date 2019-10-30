package cn.cerc.mis.core;

public class ServiceStatus implements IStatus {
    private int status;
    private boolean result;
    private String message;

    public ServiceStatus(boolean result) {
        this.result = result;
        this.message = "";
        this.status = result ? 200 : 100;
    }

    public ServiceStatus(boolean result, String message) {
        this.result = result;
        this.message = message;
        this.status = result ? 200 : 100;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public boolean getResult() {
        return result;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public ServiceStatus setStatus(int status) {
        this.status = status;
        return this;
    }

    public ServiceStatus setResult(boolean result) {
        this.result = result;
        return this;
    }

    public ServiceStatus setMessage(String message) {
        this.message = message;
        return this;
    }

}
