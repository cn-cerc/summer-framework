package cn.cerc.db.redis;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.IRecord;
import cn.cerc.core.Record;
import cn.cerc.core.TDate;
import cn.cerc.core.TDateTime;

public class RedisRecord implements IRecord {
    private static final Logger log = LoggerFactory.getLogger(RedisRecord.class);

    private String key;
    private boolean existsData = false;
    private int expires = 3600; // 单位：秒

    private Record record = new Record();
    private boolean modified = false;

    // 缓存对象
    private boolean connected;


    public RedisRecord() {
        super();
    }

    public RedisRecord(Class<?> clazz) {
        super();
        this.setKey(clazz.getName());
    }

    public RedisRecord(Object... keys) {
        super();
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < keys.length; i++) {
            if (i > 0) {
                str.append(".");
            }
            str.append(keys[i]);
        }
        setKey(str.toString());
    }

    public final void post() {
        if (this.modified) {
            try {
                Redis.set(key, record.toString(), this.expires);
                log.debug("cache set:" + key + ":" + record.toString());
                this.modified = false;
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    public boolean isNull() {
        return !this.existsData;
    }

    public void setNull(String field) {
        setField(field, null);
    }

    public String getKey() {
        return key;
    }

    public RedisRecord setKey(String key) {
        if (this.key != null) {
            throw new RuntimeException("[CacheQuery] param init error");
        }
        if (key == null) {
            throw new RuntimeException("[CacheQuery] param init error");
        }
        this.key = key;

        connected = true;
        existsData = false;
        String recordStr = Redis.get(key);
        log.debug("cache get: {} - {}" , key  , recordStr);
        if (recordStr != null && !"".equals(recordStr)) {
            try {
                record.setJSON(recordStr);
                existsData = true;
            } catch (Exception e) {
                log.error("cache data error：" + recordStr, e);
                e.printStackTrace();
            }
        }
        return this;
    }

    public void clear() {
        if (this.existsData) {
            // log.info("cache delete:" + key.toString());
            Redis.delete(key);
            this.modified = false;
            this.existsData = false;
        }
        record.clear();
        record.getFieldDefs().clear();
    }

    public boolean hasValue(String field) {
        return !isNull() && getString(field) != null && !"".equals(getString(field)) && !"{}".equals(getString(field));
    }

    public int getExpires() {
        return expires;
    }

    public void setExpires(int expires) {
        this.expires = expires;
    }

    public boolean Connected() {
        return connected;
    }

    @Override
    public boolean getBoolean(String field) {
        return record.getBoolean(field);
    }

    @Override
    public int getInt(String field) {
        return record.getInt(field);
    }

    @Override
    public double getDouble(String field) {
        return record.getDouble(field);
    }

    @Override
    public String getString(String field) {
        return record.getString(field);
    }

    @Override
    public TDate getDate(String field) {
        return record.getDate(field);
    }

    @Override
    public TDateTime getDateTime(String field) {
        return record.getDateTime(field);
    }

    @Override
    public IRecord setField(String field, Object value) {
        this.modified = true;
        record.setField(field, value);
        return this;
    }

    @Override
    public String toString() {
        if (record != null) {
            return record.toString();
        } else {
            return null;
        }
    }

    public Record getRecord() {
        return this.record;
    }

    @Override
    public boolean exists(String field) {
        return record.exists(field);
    }

    @Override
    public Object getField(String field) {
        return record.getField(field);
    }

    @Override
    public BigInteger getBigInteger(String field) {
        return record.getBigInteger(field);
    }

    @Override
    public BigDecimal getBigDecimal(String field) {
        return record.getBigDecimal(field);
    }
}
