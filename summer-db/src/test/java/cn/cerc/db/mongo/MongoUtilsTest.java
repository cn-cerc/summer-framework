package cn.cerc.db.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.Document;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;

import cn.cerc.db.core.StubSession;

public class MongoUtilsTest {
    private static final Logger log = LoggerFactory.getLogger(MongoUtilsTest.class);

    private static MongoUtils utils;

    @BeforeClass
    public static void init() {
        StubSession handle = new StubSession();
        utils = new MongoUtils(handle);
    }

    /**
     * 单条添加
     *
     * @Description
     * @author rick_zhou
     */
    @Test
    public void add() {
        String collName = "add-coll";
        MongoCollection<Document> coll = utils.findDBCollection(collName);
        coll.drop();// delete all
        log.info("清空" + collName);
        // insert doc
        for (int i = 0; i < 10; i++) {
            Document doc = new Document();
            doc.append("name", "testName" + i);
            doc.append("age", "age" + i);
            doc.append("phone", "phone" + i);
            List<Document> listDoc = new ArrayList<>();
            // 多个地址
            for (int j = 0; j < 10; j++) {
                Document address = new Document();
                address.append("address" + j, "address" + j);
                listDoc.add(address);
            }
            doc.append("addressList", listDoc);
            utils.addDocument(coll, doc);
            log.info("单条添加了第" + i + "条记录到" + collName);
        }
    }

    /**
     * 批量添加数据
     *
     * @Description
     * @author rick_zhou
     */
    @Test
    public void batchAdd() {
        String collName = "batch-add-Coll";
        MongoCollection<Document> coll = utils.findDBCollection(collName);
        coll.drop();// delete all
        log.info("清空" + collName);
        List<Document> batchList = getListDocument();
        utils.batchAddDocument(coll, batchList);
        log.info("批量添加" + batchList.size() + "条记录到" + collName);
    }

    /**
     * 修改数据
     *
     * @Description
     * @author rick_zhou
     */
    @Test
    public void update() {
        String collName = "batch-add-updateColl";
        MongoCollection<Document> coll = utils.findDBCollection(collName);
        coll.drop();// delete all
        log.info("清空" + collName);
        // insert doc
        List<Document> batchList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Document doc = new Document();
            doc.append("update-name", "testName" + i);
            doc.append("update-age", "age" + i);
            doc.append("update-phone", "phone" + i);
            List<Document> listDoc = new ArrayList<>();
            // 多个地址
            for (int j = 0; j < 10; j++) {
                Document address = new Document();
                address.append("batch-address" + j, "address" + j);
                listDoc.add(address);
            }
            doc.append("batch-addressList", listDoc);
            batchList.add(doc);
        }
        utils.batchAddDocument(coll, batchList);
        log.info("批量添加" + batchList.size() + "条要被修改的记录到" + collName);
        // 修改
        for (int i = 0; i < 10; i++) {
            BasicDBObject filter = new BasicDBObject("update-name", "testName" + i);
            Document res = utils.findDocument(coll, null, filter).get(0);
            log.info("查找到一条要修改的记录");
            res.put("addKey", "addKey" + i);
            res.put("updateKey", "updateKey" + i);
            res.put("update-name", "修改之后的名称" + i);
            utils.updateOne(coll, res);
            log.info("修改了第" + i + "条记录");
        }
    }

    /**
     * 列表查询
     *
     * @Description
     * @author rick_zhou
     */
    @Test
    public void findList() {
        String collName = "find-list-add-Coll";
        MongoCollection<Document> coll = utils.findDBCollection(collName);
        coll.drop();// delete all
        log.info("清空" + collName);
        List<Document> batchList = getListDocument();
        utils.batchAddDocument(coll, batchList);
        log.info("批量添加" + batchList.size() + "条记录到" + collName);
        // 模糊匹配
        Pattern pattern = Pattern.compile("^.*age.*$", Pattern.CASE_INSENSITIVE);
        // 完全匹配
        // Pattern pattern = Pattern.compile("^age$",
        // Pattern.CASE_INSENSITIVE);
        // 右匹配
        // Pattern pattern = Pattern.compile("^.*age$",
        // Pattern.CASE_INSENSITIVE);
        // 左匹配
        // Pattern pattern = Pattern.compile("^age.*$",
        // Pattern.CASE_INSENSITIVE);
        BasicDBObject filter = new BasicDBObject("batch-age", pattern);
        List<Document> listDoc = utils.findDocument(coll, null, filter);
        for (int i = 0; i < listDoc.size(); i++) {
            log.info("query document info:" + listDoc.get(i).toString());
        }
    }

    @Test
    public void delete() {
        String collName = "delete-add-Coll";
        MongoCollection<Document> coll = utils.findDBCollection(collName);
        coll.drop();// delete all
        log.info("清空" + collName);
        List<Document> batchList = getListDocument();
        utils.batchAddDocument(coll, batchList);

        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                continue;
            }
            BasicDBObject filter = new BasicDBObject("batch-name", "testName" + i);
            Document res = utils.findOneDocument(coll, null, filter);
            log.info("删除记录" + res.toString());
            utils.delete(coll, res);
        }
    }

    @Test
    public void projectionFind() {
        String collName = "exception-add-Coll";
        MongoCollection<Document> coll = utils.findDBCollection(collName);
        coll.drop();// delete all
        log.info("清空" + collName);
        List<Document> batchList = getListDocument();
        utils.batchAddDocument(coll, batchList);

//        BasicDBObject filter = new BasicDBObject("batch-name", "testName0");

        BasicDBObject projection = new BasicDBObject("batch-name", 1);
        projection.put("_id", 0);
//        Document res = utils.findOneDocument(coll, projection, filter);
    }

    // create test data
    private List<Document> getListDocument() {
        // insert doc
        List<Document> batchList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Document doc = new Document();
            doc.append("batch-name", "testName" + i);
            doc.append("batch-age", "age" + i);
            doc.append("batch-phone", "phone" + i);
            List<Document> listDoc = new ArrayList<>();
            // 多个地址
            for (int j = 0; j < 10; j++) {
                Document address = new Document();
                address.append("batch-address" + j, "address" + j);
                listDoc.add(address);
            }
            doc.append("batch-addressList", listDoc);
            batchList.add(doc);
        }
        return batchList;
    }

}
