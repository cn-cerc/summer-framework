package cn.cerc.mis.page;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Deprecated // 请改使用 AppLoginDefault
public class AppLoginManageDefault extends AppLoginDefault {

}
