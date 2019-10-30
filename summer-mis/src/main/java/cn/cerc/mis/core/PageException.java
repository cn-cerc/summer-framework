package cn.cerc.mis.core;

public class PageException extends RuntimeException {
    private static final long serialVersionUID = -7877763096373935534L;
    private String viewFile;

    public PageException(String message) {
        super(message);
    }

    public String getViewFile() {
        return viewFile;
    }

    public void setViewFile(String viewFile) {
        this.viewFile = viewFile;
    }

}
