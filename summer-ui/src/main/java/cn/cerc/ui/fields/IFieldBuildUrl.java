package cn.cerc.ui.fields;

import cn.cerc.ui.other.BuildUrl;

// 自定义取值
public interface IFieldBuildUrl {
    
    Object createUrl(BuildUrl buildUrl);

    BuildUrl getBuildUrl();

}
