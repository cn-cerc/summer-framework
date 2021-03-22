package cn.cerc.mis.task;

import cn.cerc.core.TDateTime;
import cn.cerc.db.mysql.MysqlConnection;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 清理在线用户记录表
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskTrackCurrentUser extends AbstractTask {

    @Override
    public void execute() {
        // 清理在线用户记录表
        MysqlConnection conn = (MysqlConnection) handle.getSession().getProperty(MysqlConnection.sessionId);

        // 删除超过100天的登录记录
        conn.execute(String.format("delete from %s where datediff(now(),LoginTime_)>100", systemTable.getCurrentUser()));

        // 清除所有未正常登录的用户记录
        StringBuilder sql2 = new StringBuilder();
        sql2.append(String.format("update %s set Viability_=-1,LogoutTime_='%s' ", systemTable.getCurrentUser(),
                TDateTime.now()));

        // 在线达24小时以上的用户
        sql2.append("where (Viability_>0) and (");
        sql2.append("(hour(timediff(now(),LoginTime_)) > 24 and LogoutTime_ is null)");

        // 在早上5点以后，清除昨天的用户
        if (TDateTime.now().getHours() > 5) {
            sql2.append(" or (datediff(now(),LoginTime_)=1)");
        }

        // 已登出超过4小时的用户
        sql2.append(" or (hour(timediff(now(),LogoutTime_)) > 4)");
        sql2.append(")");
        conn.execute(sql2.toString());
    }

}
