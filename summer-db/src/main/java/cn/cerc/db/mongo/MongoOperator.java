package cn.cerc.db.mongo;

import java.util.Date;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import cn.cerc.core.IDataOperator;
import cn.cerc.core.IHandle;
import cn.cerc.core.Record;
import cn.cerc.core.TDateTime;

public class MongoOperator implements IDataOperator {
    private String tableName;
    private MongoConnection connection;

    public MongoOperator(IHandle handle) {
        this.connection = (MongoConnection) handle.getProperty(MongoConnection.sessionId);
    }

    @Override
    public boolean insert(Record record) {
        MongoCollection<Document> coll = connection.getClient().getCollection(this.tableName);
        Document doc = getValue(record);
        coll.insertOne(doc);
        return true;
    }

    @Override
    public boolean update(Record record) {
        MongoCollection<Document> coll = connection.getClient().getCollection(this.tableName);
        Document doc = getValue(record);
        String uid = record.getString("_id");
        Object key = "".equals(uid) ? "null" : new ObjectId(uid);
        UpdateResult res = coll.replaceOne(Filters.eq("_id", key), doc);
        return res.getModifiedCount() == 1;
    }

    @Override
    public boolean delete(Record record) {
        MongoCollection<Document> coll = connection.getClient().getCollection(this.tableName);
        String uid = record.getString("_id");
        Object key = "".equals(uid) ? "null" : new ObjectId(uid);
        DeleteResult res = coll.deleteOne(Filters.eq("_id", key));
        return res.getDeletedCount() == 1;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    private Document getValue(Record record) {
        Document doc = new Document();
        for (int i = 0; i < record.getFieldDefs().size(); i++) {
            String field = record.getFieldDefs().getFields().get(i);
            if (field.equals("_id"))
                continue;
            Object obj = record.getField(field);
            if (obj instanceof TDateTime)
                doc.append(field, ((TDateTime) obj).toString());
            else if (obj instanceof Date)
                doc.append(field, (new TDateTime((Date) obj)).toString());
            else
                doc.append(field, obj);
        }
        return doc;
    }
}
