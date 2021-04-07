package cn.cerc.mis.custom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.cerc.core.ISession;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.core.IAppLanguage;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AppLanguageDefault implements IAppLanguage {
    private static final Logger log = LoggerFactory.getLogger(AppLanguageDefault.class);
    private ISession session;

    // FIXME: 2019/11/21 用户配置表需要改为动态获取
    @Override
    public String getLanguageId(String defaultValue) {
        String result = defaultValue;
        try {
            SqlQuery ds = new SqlQuery(session);
            ds.add("select Value_ from %s where Code_='%s' and UserCode_='%s'", "UserOptions", "Lang_",
                    session.getUserCode());
            ds.open();
            if (!ds.eof()) {
                result = ds.getString("Value_");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return result;
    }

    @Override
    public ISession getSession() {
        return session;
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;
    }

}
