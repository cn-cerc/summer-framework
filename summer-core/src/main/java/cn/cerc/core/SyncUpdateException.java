package cn.cerc.core;

public class SyncUpdateException extends Exception {
    private static final long serialVersionUID = -7421586617677073495L;

    public SyncUpdateException(Exception e) {
        super(e.getMessage());
        this.addSuppressed(e);
    }

    public SyncUpdateException(String message) {
        super(message);
    }
}
