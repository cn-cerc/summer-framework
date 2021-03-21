package cn.cerc.ui.core;

import cn.cerc.ui.fields.AbstractField;

public interface IReadonlyOwner {
    
    boolean isReadonly();

    AbstractField setReadonly(boolean value);
}
