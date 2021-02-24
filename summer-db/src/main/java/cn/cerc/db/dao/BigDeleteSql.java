package cn.cerc.db.dao;

import cn.cerc.core.ClassData;
import cn.cerc.core.ClassFactory;
import cn.cerc.db.mysql.BuildStatement;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class BigDeleteSql {

    public static boolean exec(Connection conn, Object curRecord, boolean preview) {
        String lastCommand = null;
        ClassData classData = ClassFactory.get(curRecord.getClass());
        String updateKey = classData.getUpdateKey();
        try (BuildStatement bs = new BuildStatement(conn)) {
            bs.append("delete from ").append(classData.getTableId());
            Map<String, Object> items = new LinkedHashMap<>();
            BigOperator.copy(curRecord, (key, value) -> {
                items.put(key, value);
            });

            Object value = items.get(updateKey);
            if (value == null) {
                throw new RuntimeException("primary key is null");
            }

            bs.append(" where ");
            bs.append(updateKey).append("=? ", value);

            PreparedStatement ps = bs.build();
            lastCommand = bs.getCommand();
            if (preview) {
                log.info(lastCommand);
                return false;
            } else {
                log.debug(lastCommand);
            }

            return ps.execute();
        } catch (SQLException e) {
            log.error(lastCommand);
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
