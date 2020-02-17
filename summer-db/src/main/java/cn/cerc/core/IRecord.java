package cn.cerc.core;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface IRecord {

    boolean exists(String field);

    boolean getBoolean(String field);

    int getInt(String field);

    BigInteger getBigInteger(String field);

    BigDecimal getBigDecimal(String field);

    double getDouble(String field);

    String getString(String field);

    TDate getDate(String field);

    TDateTime getDateTime(String field);

    IRecord setField(String field, Object value);

    Object getField(String field);

}
