package cn.cerc.db.queue;

import cn.cerc.core.IDataOperator;
import cn.cerc.core.Record;

public class OssOperator implements IDataOperator {
    // 根据 sql 获取数据库表名
    public String findTableName(String sql) {
        String result = null;
        String[] items = sql.split("[ \r\n]");
        for (int i = 0; i < items.length; i++) {
            if (items[i].toLowerCase().contains("from")) {
                // 如果取到form后 下一个记录为数据库表名
                while (items[i + 1] == null || "".equals(items[i + 1].trim())) {
                    // 防止取到空值
                    i++;
                }
                result = items[++i]; // 获取数据库表名
                break;
            }
        }

        if (result == null) {
            throw new RuntimeException("SQL语句异常");
        }

        return result;
    }

    @Override
    public boolean insert(Record record) {
        throw new RuntimeException("阿里云OSS服务，不支持插入操作");
    }

    @Override
    public boolean update(Record record) {
        throw new RuntimeException("阿里云OSS服务，不支持修改操作");
    }

    @Override
    public boolean delete(Record record) {
        throw new RuntimeException("阿里云OSS服务，不支持删除操作");
    }
}
