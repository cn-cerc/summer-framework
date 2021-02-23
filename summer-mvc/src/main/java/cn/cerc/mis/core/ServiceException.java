package cn.cerc.mis.core;

public class ServiceException extends Exception {
    private static final long serialVersionUID = -4379499449839426137L;

    public ServiceException() {
    }

    public ServiceException(Exception e) {
        super(e.getMessage());
        this.addSuppressed(e);
    }

    public ServiceException(String message) {
        super(message);
    }

}
