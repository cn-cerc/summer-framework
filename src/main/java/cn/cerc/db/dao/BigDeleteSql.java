package cn.cerc.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ClassData;
import cn.cerc.core.ClassFactory;
import cn.cerc.db.mysql.BuildStatement;

public class BigDeleteSql {
    private static final Logger log = LoggerFactory.getLogger(BigDeleteSql.class);

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
