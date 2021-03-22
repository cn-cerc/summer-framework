package cn.cerc.ui.custom;

import org.springframework.stereotype.Component;

import cn.cerc.core.Utils;
import cn.cerc.mis.core.IForm;
import cn.cerc.mis.language.R;
import cn.cerc.ui.code.IFormInfo;
import cn.cerc.ui.menu.MenuList;

@Component
public class FormInfoDefault implements IFormInfo {

    @Override
    public String getFormCaption(IForm form, String formId, String defaultValue) {
        //FIXME 不得对函数使用R.asString，此处需要改进! ZhangGong 2021/3/16
        if (Utils.isNotEmpty(form.getName())) {
            return R.asString(form.getHandle(), form.getName());
        } else {
            return R.asString(form.getHandle(), MenuList.create(form.getHandle()).getName(formId));
        }
        
    }

}
