package cn.cerc.mis.sync;

public enum SyncOpera {

    Append, Delete, Update, Reset;

    public static String getName(SyncOpera opera) {
        switch (opera) {
        case Append:
            return "append";
        case Delete:
            return "delete";
        case Update:
            return "update";
        case Reset:
            return "reset";
        default:
            return "other";
        }
    }
}
