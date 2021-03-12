package cn.cerc.ui.page.service;

import cn.cerc.core.ClassResource;
import cn.cerc.core.DataSet;
import cn.cerc.core.IUserLanguage;
import cn.cerc.core.Record;
import cn.cerc.core.TDateTime;
import cn.cerc.db.mysql.BuildQuery;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.db.mysql.Transaction;
import cn.cerc.db.oss.OssConnection;
import cn.cerc.mis.core.CustomService;
import cn.cerc.mis.core.DataValidateException;
import cn.cerc.mis.language.R;
import cn.cerc.ui.SummerUI;

/**
 * 文件上传服务
 */
public class SvrFileUpload extends CustomService implements IUserLanguage {
    private final ClassResource res = new ClassResource(this, SummerUI.ID);

    /**
     * 文件上传表
     */
    private static final String TABLE_FILEUPLOADS = "file_uploads";

    public boolean search() throws DataValidateException {
        Record headIn = getDataIn().getHead();
        DataValidateException.stopRun(res.getString(1, "请指定单号"), !headIn.hasValue("tbNo"));

        BuildQuery f = new BuildQuery(this);
        f.add("select * from %s", TABLE_FILEUPLOADS);
        f.byField("CorpNo_", getCorpNo());
        f.byField("TBNo_", headIn.getString("tbNo"));

        getDataOut().appendDataSet(f.open());
        return true;
    }

    public boolean append() throws DataValidateException {
        Record headIn = getDataIn().getHead();
        DataValidateException.stopRun(res.getString(2, "上传失败，单别不能为空！"), !headIn.hasValue("tb"));
        DataValidateException.stopRun(res.getString(3, "上传失败，单号不能为空！"), !headIn.hasValue("tbNo"));

        String tb = headIn.getString("tb");
        String tbNo = headIn.getString("tbNo");

        try (Transaction tx = new Transaction(this)) {
            SqlQuery ds = new SqlQuery(this);
            DataSet dataIn = getDataIn();
            while (dataIn.fetch()) {
                Record current = dataIn.getCurrent();

                DataValidateException.stopRun(res.getString(4, "上传失败，文件大小不能为空！"), !current.hasValue("size"));
                DataValidateException.stopRun(res.getString(5, "上传失败，文件名不能为空！"), !current.hasValue("name"));
                DataValidateException.stopRun(res.getString(6, "上传失败，文件路径不能为空！"), !current.hasValue("path"));

                ds.clear();
                ds.add("select * from %s", TABLE_FILEUPLOADS);
                ds.add("where Path_='%s'", current.getString("path").trim());
                ds.setMaximum(1);
                ds.open();
                if (!ds.eof()) {
                    DataValidateException.stopRun(String.format(res.getString(7, "%s 文件已存在！"), ds.getString("Name_")), true);
                }

                ds.append();
                ds.setField("CorpNo_", getCorpNo());
                ds.setField("TB_", tb);
                ds.setField("TBNo_", tbNo);
                ds.setField("Name_", current.getString("name").trim());
                ds.setField("Path_", current.getString("path").trim());
                ds.setField("Size_", current.getInt("size"));
                ds.setField("AppUser_", getUserCode());
                ds.setField("AppDate_", TDateTime.now());
                ds.post();
            }

            return tx.commit();
        }
    }

    public boolean delete() throws DataValidateException {
        Record headIn = getDataIn().getHead();
        DataValidateException.stopRun(res.getString(8, "请指定单号！"), !headIn.hasValue("tbNo"));
        DataValidateException.stopRun(res.getString(9, "请指定文件名！"), !headIn.hasValue("name"));

        String tbNo = headIn.getString("tbNo").trim();
        String name = headIn.getString("name").trim();

        try (Transaction tx = new Transaction(this)) {
            SqlQuery ds = new SqlQuery(this);
            ds.add("select * from %s", TABLE_FILEUPLOADS);
            ds.add("where CorpNo_='%s'", getCorpNo());
            ds.add("and TBNo_='%s'", tbNo);
            ds.add("and Name_='%s'", name);
            ds.open();
            DataValidateException.stopRun(res.getString(10, "删除失败，文件不存在！"), ds.eof());

            OssConnection oss = (OssConnection) getProperty(OssConnection.sessionId);

            while (ds.fetch()) {
                oss.delete(ds.getString("Path_"));

                ds.delete();
            }

            return tx.commit();
        }
    }

    @Override
    public String getLanguageId() {
        return R.getLanguageId(getHandle());
    }
}
