package cn.cerc.db.mongo;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;

public class MongoUtils {
    private MongoDB connection;
    private MongoDatabase database;

    public MongoUtils(ISession session) {
        connection = (MongoDB) session.getProperty(MongoDB.SessionId);
        database = connection.getClient();
    }

    public MongoUtils(IHandle owner) {
        this(owner.getSession());
    }

    // 获取Collection by name
    public MongoCollection<Document> findDBCollection(String collectionName) {
        return database.getCollection(collectionName);
    }

    // 查找一条记录
    public Document findOneDocument(MongoCollection<Document> coll, BasicDBObject projection,
                                    BasicDBObject fileterBasiObject) {
        return findDocument(coll, projection, fileterBasiObject, null, null, null).get(0);
    }

    // 查询文档
    public List<Document> findDocument(MongoCollection<Document> coll, BasicDBObject projection,
                                       BasicDBObject fileterBasiObject) {
        return findDocument(coll, projection, fileterBasiObject, null, null, null);
    }

    // 查询文档
    public List<Document> findDocument(MongoCollection<Document> coll, BasicDBObject projection,
                                       BasicDBObject fileterBasiObject, BasicDBObject sort, Integer skip, Integer limit) {
        List<Document> list = null;
        if (skip != null) {
            skip = skip <= 0 ? 0 : skip;
        }
        if (limit != null) {
            limit = limit >= 5000 ? 5000 : limit;
        }
        if (projection == null) {
            projection = new BasicDBObject();
        }
        sort = sort == null ? new BasicDBObject("_id", 1) : sort;
        try {
            if (skip != null && limit != null) {
                list = coll.find(fileterBasiObject).projection(projection).sort(sort).skip(skip).limit(limit)
                        .into(new ArrayList<Document>());
            } else {
                list = coll.find(fileterBasiObject).projection(projection).sort(sort).into(new ArrayList<Document>());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }
        return list;
    }

    // 添加一个document记录
    public void addDocument(MongoCollection<Document> coll, Document doc) {
        try {
            coll.insertOne(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 批量添加document记录
    public void batchAddDocument(MongoCollection<Document> coll, List<Document> listDoc) {
        try {
            coll.insertMany(listDoc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 修改一行记录
    public void updateOne(MongoCollection<Document> coll, Document targetDoc) {
        Document filterDoc = new Document("_id", targetDoc.get("_id"));
        try {
            coll.replaceOne(filterDoc, targetDoc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 删除一行记录
    public void delete(MongoCollection<Document> coll, Document targetDoc) {
        Document filterDoc = new Document("_id", targetDoc.get("_id"));
        try {
            coll.deleteOne(filterDoc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
