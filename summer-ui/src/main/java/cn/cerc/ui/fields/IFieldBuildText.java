package cn.cerc.ui.fields;

import cn.cerc.ui.other.BuildText;

// 自定义取值
public interface IFieldBuildText {
    
    Object createText(BuildText buildText);

    BuildText getBuildText();

}
