package cn.cerc.core;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface IRecord {

    public boolean exists(String field);

    public boolean getBoolean(String field);

    public int getInt(String field);

    public BigInteger getBigInteger(String field);

    public BigDecimal getBigDecimal(String field);

    public double getDouble(String field);

    public String getString(String field);

    public TDate getDate(String field);

    public TDateTime getDateTime(String field);

    public IRecord setField(String field, Object value);

    public Object getField(String field);

}
