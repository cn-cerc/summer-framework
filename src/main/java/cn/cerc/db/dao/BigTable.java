package cn.cerc.db.dao;

import java.io.IOException;
import java.lang.Thread.State;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ClassData;
import cn.cerc.core.ClassFactory;
import cn.cerc.core.ISession;
import cn.cerc.core.SqlText;
import cn.cerc.core.Utils;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.mysql.MysqlClient;
import cn.cerc.db.mysql.UpdateMode;
import cn.cerc.db.redis.JedisFactory;
import redis.clients.jedis.Jedis;

public abstract class BigTable<T extends BigRecord> implements IHandle {
    private static final Logger log = LoggerFactory.getLogger(BigTable.class);

    // 有变动待保存数据，保存完后会自动清除
    protected Map<T, T> updateList = new ConcurrentHashMap<>();
    protected Map<String, T> updateOldList = new ConcurrentHashMap<>();
    protected Map<String, T> deleteList = new ConcurrentHashMap<>();
    // 所有内存数据
    private Map<String, T> items = new ConcurrentHashMap<>();
    // 数据集名称，可为空
    private String tableId;
    // 要管理的对象
    private Class<T> clazz;
    private boolean redisEnabled = false;
    private BigStorage storage;
    private Thread storageThread;
    private BigControl control;
    private UpdateMode updateMode = UpdateMode.loose;

    private ISession session;

    public BigTable(BigControl control) {
        super();
        initClazz();
        storage = new BigStorage(this);
        this.setControl(control);
    }

    public static Object cloneObject(Object obj1) {
        Object obj2 = null;
        try {
            obj2 = obj1.getClass().getDeclaredConstructor().newInstance();
            Map<String, Object> items = new LinkedHashMap<>();
            BigOperator.copy(obj1, (key, value) -> {
                items.put(key, value);
            });
            BigOperator.copy(items, obj2);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
        return obj2;
    }

    @SuppressWarnings("unchecked")
    private void initClazz() {
        this.clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.tableId = DaoUtil.getTableName(clazz);
    }

    @Deprecated // 请改使用 open
    public int load() {
        return open();
    }

    /**
     * 执行指定的sql，并返回结果集
     *
     * @param sqlText 要执行的 SqlText
     * @return 返回载入的笔数
     */
    public int open(SqlText sqlText) {
        int total = 0;
        int offset = 0;
        while (true) {
            sqlText.setOffset(offset);
            int num = loadRecords(sqlText.getTextByLimit());
            total += num;
            if (num < sqlText.getMaximum()) {
                break;
            }
            // 开始取下一个批次的数据
            offset += num;
        }
        return total;
    }

    /**
     * 载入所有的记录
     *
     * @return 返回载入的笔数
     */
    public int open() {
        if (redisEnabled) {
            if (this.getTableId() == null) {
                throw new RuntimeException("tableId is null.");
            }
            try (Jedis jedis = JedisFactory.getJedis()) {
                // long total = jedis.hlen(this.getName());
                for (String key : jedis.hkeys(this.getTableId())) {
                    try {
                        @SuppressWarnings("unchecked")
                        T value = (T) Utils.deserializeToObject(jedis.hget(this.getTableId(), key));
                        items.put(key, value);
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (items.size() > 0) {
                return items.size();
            }
            try (Jedis jedis = JedisFactory.getJedis()) {
                jedis.del(getTableId());
            }
        }

        SqlText sql = new SqlText(clazz);
        int total = 0;
        int offset = 0;
        while (true) {
            sql.setOffset(offset);
            int num = loadRecords(sql.getTextByLimit());
            total += num;
            if (num < sql.getMaximum()) {
                break;
            }
            // 开始取下一个批次的数据
            offset += num;
        }
        return total;
    }

    @Deprecated // 请改使用open(SqlText)
    public int load(Object... keyValues) {
        SqlText sql = new SqlText(clazz);
        return loadRecords(sql.getWhereKeys(keyValues));
    }

    @Deprecated // 请改使用open(SqlText)
    public int loadWhere(String whereText) {
        SqlText sql = new SqlText(clazz);
        return loadRecords(sql.getWhere(whereText));
    }

    @Deprecated // 请改使用open(SqlText)
    public int loadWhere(String format, Object... args) {
        SqlText sql = new SqlText(clazz);
        return loadRecords(sql.getWhere(String.format(format, args)));
    }

    private int loadRecords(String sqlText) {
        int total = 0;
        try (MysqlClient client = this.getMysql().getClient()) {
            try (Statement stat = client.getConnection().createStatement()) {
                log.debug(sqlText.replaceAll("\r\n", " "));
                stat.execute(sqlText.replace("\\", "\\\\"));
                try (ResultSet rs = stat.getResultSet()) {
                    // 取得字段清单
                    ResultSetMetaData meta = rs.getMetaData();
                    // 取得所有数据
                    if (!rs.first()) {
                        return 0;
                    }
                    do {
                        total++;
                        Map<String, Object> items = new HashMap<>();
                        for (int i = 1; i <= meta.getColumnCount(); i++) {
                            String key = meta.getColumnName(i);
                            // System.out.println(rs.getObject(key) + ":" + rs.getObject(i));
                            items.put(key, rs.getObject(key));
                        }
                        try {
                            T obj = clazz.getDeclaredConstructor().newInstance();
                            BigOperator.copy(items, obj);
                            this.put(obj);
                        } catch (InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } while (rs.next());
                }
            }
        } catch (Exception e1) {
            log.error(sqlText);
            e1.printStackTrace();
        }
        return total;
    }

    public void saveInsert(T record, boolean saveToDatabase) {
        if (saveToDatabase) {
            try (MysqlClient client = this.getMysql().getClient()) {
                BigInsertSql.exec(client.getConnection(), record, false);
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
        }
        put(record);
    }

    public synchronized void saveUpdate(T newRecord, boolean saveToDatabase) {
        String key = getPrimaryKey(newRecord);
        if (saveToDatabase) {
            T srcRecord = updateOldList.get(key);

            T lastRecord = updateList.get(srcRecord); // n52
            if (lastRecord != null) {
                newRecord.mergeValue(srcRecord, lastRecord);
            }

            updateList.put(srcRecord, newRecord); // n50, n56
        }
        put(newRecord);
    }

    public void saveDelete(T record, boolean saveToDatabase) {
        String key = getPrimaryKey(record);
        if (saveToDatabase) {
            deleteList.put(key, record);
        } else {
            if (deleteList.containsKey(key)) {
                deleteList.remove(key);
            }
        }
        del(record);
    }

    /**
     * 立即保存到数据库，主要适用于 app
     *
     * @param record 要保存的对象
     */
    public void postUpdate(T record) {
        saveUpdate(record, true);
        save(0);
    }

    /**
     * 立即保存到数据库，主要适用于 app
     *
     * @param record 要保存的对象
     */
    public void postDelete(T record) {
        saveDelete(record, true);
        save(0);
    }

    @Deprecated // 请改使用 post(maxSize)
    public synchronized void save(int maxSize) {
        post(maxSize);
    }

    /**
     * 保存到数据库
     *
     * @param maxSize 最大保存笔数
     */
    public synchronized void post(int maxSize) {
        int count = updateList.size() + deleteList.size();
        if (count == 0) {
            return;
        }

        String tableName = DaoUtil.getTableName(clazz);
        if (tableName == null) {
            throw new RuntimeException("tableName is null");
        }

        try (MysqlClient client = this.getMysql().getClient()) {
            // opera.setPreview(true);
            int total = 0;
            // update
            for (T srcRecord : updateList.keySet()) {
                T newRecord = updateList.get(srcRecord);
                BigUpdateSql.exec(client.getConnection(), srcRecord, newRecord, updateMode, false);
                updateList.remove(srcRecord);
                total++;
                if (maxSize > 0 && total == maxSize) {
                    return;
                }
            }
            // delete
            for (String key : deleteList.keySet()) {
                T record = deleteList.get(key);
                BigDeleteSql.exec(client.getConnection(), record, false);
                deleteList.remove(key);
                total++;
                if (maxSize > 0 && total == maxSize) {
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public T get(Object... keys) {
        String key = buildKey(keys);
        return items.get(key);
    }

    @SuppressWarnings("unchecked")
    public T getClone(Object... keys) {
        String key = buildKey(keys);
        T record = items.get(key);
        if (record == null) {
            return null;
        }

        T newRecord = (T) cloneObject(record);
        updateOldList.put(key, record);
        return newRecord;
    }

    protected String buildKey(Object... keys) {
        StringBuffer sb = new StringBuffer();
        for (Object key : keys) {
            String str = null;
            if (key == null) {
                throw new RuntimeException("key is null!");
            }

            if (key instanceof String) {
                str = (String) key;
            } else {
                str = key.toString();
            }
            sb.append(".").append(str);
        }
        return sb.substring(1);
    }

    @SuppressWarnings("unchecked")
    public T getRedis(String key) {
        this.redisEnabled = true;

        T record = items.get(key);
        if (record != null) {
            return (T) cloneObject(record);
        }
        String result;
        try (Jedis jedis = JedisFactory.getJedis()) {
            result = jedis.hget(getTableId(), key);
            if (result == null) {
                return null;
            }
        }

        try {
            record = (T) Utils.deserializeToObject(result);
            items.put(key, record);
            return (T) cloneObject(record);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void put(T value) {
        String key = getPrimaryKey(value);
        items.put(key, value);
        if (redisEnabled) {
            try (Jedis jedis = JedisFactory.getJedis()) {
                String result = Utils.serializeToString(value);
                jedis.hset(this.getTableId(), key, result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void del(T record) {
        String key = getPrimaryKey(record);
        items.remove(key);
        if (this.redisEnabled) {
            try (Jedis jedis = JedisFactory.getJedis()) {
                jedis.hdel(getTableId(), key);
            }
        }
    }

    public Set<String> keySet() {
        return items.keySet();
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        if (this.tableId != tableId) {
            if (this.storageThread != null) {
                this.storageThread.setName(tableId + ".storage");
            }
            this.tableId = tableId;
        }
    }

    public int size() {
        return items.size();
    }

    public void clearForced() {
        updateOldList.clear();
        updateList.clear();
        deleteList.clear();
        items.clear();
    }

    public void clear() {
        int count = updateList.size() + deleteList.size();
        if (count > 0) {
            log.info("updateList size {}", updateList.size());
            log.info("deleteLise size {}", deleteList.size());
            throw new RuntimeException("delta is not null");
        }
        if (this.redisEnabled) {
            try (Jedis jedis = JedisFactory.getJedis()) {
                jedis.del(getTableId());
            }
        }
        items.clear();
    }

    public boolean isRedisEnabled() {
        return redisEnabled;
    }

    public BigTable<T> setRedisEnabled(boolean redisEnabled) {
        this.redisEnabled = redisEnabled;
        return this;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getPrimaryKey(T record) {
        StringBuffer sb = new StringBuffer();
        try {
            ClassData classData = ClassFactory.get(record.getClass());
            for (String key : classData.getSearchKeys()) {
                Field field = classData.getFields().get(key);
                Object value = field.get(record);
                if (value == null) {
                    throw new RuntimeException(String.format("%s value is null", field.getName()));
                }
                if (sb.length() > 0) {
                    sb.append(".");
                }
                if (value instanceof String) {
                    sb.append((String) value);
                } else {
                    sb.append(value.toString());
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public void printDebug() {
        for (String key : keySet()) {
            System.out.println(this.getTableId() + ":" + key);
        }
    }

    public BigStorage getStorage() {
        return storage;
    }

    public BigControl getControl() {
        return control;
    }

    public void setControl(BigControl control) {
        if (control == null) {
            throw new RuntimeException("control is null.");
        }
        control.registerTable(this);
        storage.setControl(control);
        this.control = control;
        if (control.getActive().get()) {
            this.startStorage();
        }
    }

    public void startStorage() {
        log.debug("startStorage");
        control.getActive().set(true);
        if (storageThread == null) {
            storageThread = new Thread(this.storage, this.getTableId() + ".storage");
            storageThread.start();
        }
    }

    public void stopStorage() {
        if (storageThread == null) {
            return;
        }
        log.debug("stopStorage");
        control.getActive().set(false);
        while (storageThread.getState() != State.TERMINATED) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        storageThread = null;
    }

    public UpdateMode getUpdateMode() {
        return updateMode;
    }

    public void setUpdateMode(UpdateMode updateMode) {
        this.updateMode = updateMode;
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
