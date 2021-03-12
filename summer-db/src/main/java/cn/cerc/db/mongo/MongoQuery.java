package cn.cerc.db.mongo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bson.Document;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;

import cn.cerc.core.ClassResource;
import cn.cerc.core.DataSet;
import cn.cerc.core.DataSetState;
import cn.cerc.core.IDataOperator;
import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.core.Utils;
import cn.cerc.db.SummerDB;
import cn.cerc.db.core.CustomHandle;
import cn.cerc.db.core.DataQuery;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.ISupportSession;
import cn.cerc.db.mysql.SqlOperator;

public class MongoQuery extends DataQuery {
    private static final long serialVersionUID = -1262005194419604476L;
    private static final ClassResource res = new ClassResource(MongoQuery.class, SummerDB.ID);

    private MongoConnection connection = null;
    // 数据库保存操作执行对象
    private IDataOperator operator;
    // 仅当batchSave为true时，delList才有记录存在
    private List<Record> delList = new ArrayList<>();

    public MongoQuery(ISession session) {
        super(session);
        connection = (MongoConnection) session.getProperty(MongoConnection.sessionId);
    }

    public MongoQuery(ISupportSession owner) {
        this(owner.getSession());
    }

    @Override
    public DataQuery open() {
        String table = SqlOperator.findTableName(this.getSqlText().getText());
        // 查找业务ID对应的数据
        MongoCollection<Document> coll = connection.getClient().getCollection(table);
        // 增加查询条件
        BasicDBObject filter = decodeWhere(this.getSqlText().getText());
        // 增加排序条件
        BasicDBObject sort = decodeOrder(this.getSqlText().getText());
        // 执行查询
        ArrayList<Document> list = coll.find(filter).sort(sort).into(new ArrayList<>());
        // 数据不存在,则状态不为更新,并返回一个空数据
        if (list.isEmpty()) {
            return this;
        }

        for (Document doc : list) {
            Record record = append().getCurrent();
            for (String field : doc.keySet()) {
                if ("_id".equals(field)) {
                    Object uid = doc.get(field);
                    record.setField(field, uid != null ? uid.toString() : uid);
                } else {
                    record.setField(field, doc.get(field));
                }
            }
            record.setState(DataSetState.dsNone);
        }
        this.first();
        this.active = true;
        return this;
    }

    // 将sql指令查询条件改为MongoDB格式
    protected BasicDBObject decodeWhere(String sql) {
        BasicDBObject filter = new BasicDBObject();
        int offset = sql.toLowerCase().indexOf("where");
        if (offset > -1) {
            int endIndex = sql.toLowerCase().indexOf("order");
            String[] items;
            if (endIndex > -1) {
                items = sql.substring(offset + 5, endIndex).split(" and ");
            } else {
                items = sql.substring(offset + 5).split(" and ");
            }
            for (String item : items) {
                if (item.split(">=").length == 2) {
                    setCondition(filter, item, ">=");
                } else if (item.split("<=").length == 2) {
                    setCondition(filter, item, "<=");
                } else if (item.split("<>").length == 2) {
                    setCondition(filter, item, "<>");
                } else if (item.split("=").length == 2) {
                    setCondition(filter, item, "=");
                } else if (item.split(">").length == 2) {
                    setCondition(filter, item, ">");
                } else if (item.split("<").length == 2) {
                    setCondition(filter, item, "<");
                } else if (item.split("like").length == 2) {
                    String[] tmp = item.split("like");
                    String field = tmp[0].trim();
                    String value = tmp[1].trim();
                    if (value.startsWith("'") && value.endsWith("'")) {
                        // 不区分大小写的模糊搜索
                        Pattern queryPattern = Pattern.compile(value.substring(1, value.length() - 1),
                                Pattern.CASE_INSENSITIVE);
                        filter.append(field, queryPattern);
                    } else {
                        throw new RuntimeException(String.format(res.getString(1, "模糊查询条件：%s 必须为字符串"), item));
                    }
                } else if (item.split("in").length == 2) {
                    String[] tmp = item.split("in");
                    String field = tmp[0].trim();
                    String value = tmp[1].trim();
                    if (value.startsWith("(") && value.endsWith(")")) {
                        BasicDBList values = new BasicDBList();
                        for (String str : value.substring(1, value.length() - 1).split(",")) {
                            if (str.startsWith("'") && str.endsWith("'")) {
                                values.add(str.substring(1, str.length() - 1));
                            } else {
                                values.add(str);
                            }
                        }
                        filter.put(field, new BasicDBObject("$in", values));
                    } else {
                        throw new RuntimeException(String.format(res.getString(2, "in查询条件：%s 必须有带有()"), item));
                    }
                } else {
                    throw new RuntimeException(String.format(res.getString(3, "暂不支持的查询条件：%s"), item));
                }
            }
        }
        return filter;
    }

    private void setCondition(BasicDBObject filter, String item, String symbol) {
        Map<String, String> compare = new HashMap<>();
        compare.put("=", "$eq");
        compare.put("<>", "$ne");
        compare.put(">", "$gt");
        compare.put(">=", "$gte");
        compare.put("<", "$lt");
        compare.put("<=", "$lte");
        String[] tmp = item.split(symbol);
        String field = tmp[0].trim();
        String value = tmp[1].trim();
        if (filter.get(field) != null) {
            if (value.startsWith("'") && value.endsWith("'")) {
                ((BasicDBObject) filter.get(field)).append(compare.get(symbol), value.substring(1, value.length() - 1));
            } else if (Utils.isNumeric(value)) {
                ((BasicDBObject) filter.get(field)).append(compare.get(symbol), Double.parseDouble(value));
            } else {
                ((BasicDBObject) filter.get(field)).append(compare.get(symbol), value);
            }
        } else {
            if (value.startsWith("'") && value.endsWith("'")) {
                filter.put(field, new BasicDBObject(compare.get(symbol), value.substring(1, value.length() - 1)));
            } else if (Utils.isNumeric(value)) {
                filter.put(field, new BasicDBObject(compare.get(symbol), Double.parseDouble(value)));
            } else {
                filter.put(field, new BasicDBObject(compare.get(symbol), value));
            }
        }
    }

    // 将sql指令排序条件改为MongoDB格式
    protected BasicDBObject decodeOrder(String sql) {
        BasicDBObject sort = new BasicDBObject();
        int offset = sql.toLowerCase().indexOf("order");
        if (offset == -1) {
            return sort;
        }
        String[] items = sql.substring(offset + 5).split(",");
        for (String item : items) {
            String str = item.trim();
            if (str.split(" ").length == 2) {
                String[] tmp = str.split(" ");
                if ("ASC".equals(tmp[1])) {
                    sort.append(tmp[0], 1);
                } else if ("DESC".equals(tmp[1])) {
                    sort.append(tmp[0], -1);
                } else {
                    throw new RuntimeException("暂不支持的排序条件：" + str);
                }
            } else {
                sort.append(str, 1);
            }
        }
        return sort;
    }

    @Override
    public void post() {
        if (this.isBatchSave()) {
            return;
        }
        Record record = this.getCurrent();
        if (record.getState() == DataSetState.dsInsert) {
            beforePost();
            getDefaultOperator().insert(record);
            super.post();
        } else if (record.getState() == DataSetState.dsEdit) {
            beforePost();
            getDefaultOperator().update(record);
            super.post();
        }
    }

    private IDataOperator getDefaultOperator() {
        if (operator == null) {
            MongoOperator obj = new MongoOperator(this.session);
            obj.setTableName(SqlOperator.findTableName(this.getSqlText().getText()));
            operator = obj;
        }
        return operator;
    }

    @Override
    public void delete() {
        Record record = this.getCurrent();
        super.delete();
        if (record.getState() == DataSetState.dsInsert) {
            return;
        }
        if (this.isBatchSave()) {
            delList.add(record);
        } else {
            getDefaultOperator().delete(record);
        }
    }

    @Override
    public void save() {
        if (!this.isBatchSave()) {
            throw new RuntimeException("batchSave is false");
        }
        IDataOperator operator = getDefaultOperator();
        // 先执行删除
        for (Record record : delList) {
            operator.delete(record);
        }
        delList.clear();
        // 再执行增加、修改
        this.first();
        while (this.fetch()) {
            if (this.getState().equals(DataSetState.dsInsert)) {
                beforePost();
                operator.insert(this.getCurrent());
                super.post();
            } else if (this.getState().equals(DataSetState.dsEdit)) {
                beforePost();
                operator.update(this.getCurrent());
                super.post();
            }
        }
    }

    @Override
    public IDataOperator getOperator() {
        return operator;
    }

    public void setOperator(IDataOperator operator) {
        this.operator = operator;
    }

    // 将通用类型，转成DataSet，方便操作
    public DataSet getChildDataSet(String field) {
        Object value = this.getField(field);
        if (value == null) {
            return null;
        }
        if (!(value instanceof List<?>)) {
            throw new RuntimeException("错误的数据类型！");
        }
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) value;
        DataSet dataSet = new DataSet();
        for (Map<String, Object> item : items) {
            Record record = dataSet.append().getCurrent();
            for (String key : item.keySet()) {
                record.setField(key, item.get(key));
            }
            record.setState(DataSetState.dsNone);
        }
        return dataSet;
    }

    // 将DataSet转成通用类型，方便存入MongoDB
    public void setChildDataSet(String field, DataSet dataSet) {
        List<Map<String, Object>> items = new ArrayList<>();
        for (Record child : dataSet.getRecords()) {
            items.add(child.getItems());
        }
        this.setField(field, items);
    }

    @SuppressWarnings("unchecked")
    public List<Object> assignList(String field) {
        Object value = this.getField(field);
        if (value == null) {
            List<Object> items = new ArrayList<>();
            this.setField(field, items);
            return items;
        }
        if (!(value instanceof List<?>)) {
            throw new RuntimeException("错误的数据类型！");
        }
        return (List<Object>) value;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> assignMap(String field) {
        Object value = this.getField(field);
        if (value == null) {
            Map<String, Object> items = new LinkedHashMap<>();
            this.setField(field, items);
            return items;
        }
        if (!(value instanceof List<?>)) {
            throw new RuntimeException("错误的数据类型！");
        }
        return (Map<String, Object>) value;
    }

    @Override
    public MongoQuery add(String sql) {
        super.add(sql);
        return this;
    }

    @Override
    public MongoQuery add(String format, Object... args) {
        super.add(format, args);
        return this;
    }
}
