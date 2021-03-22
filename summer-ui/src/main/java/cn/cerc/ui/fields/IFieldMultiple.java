package cn.cerc.ui.fields;

public interface IFieldMultiple {
    // 用于文件上传是否可以选则多个文件
//    private boolean multiple = false;

    boolean isMultiple();

    Object setMultiple(boolean multiple);

}
