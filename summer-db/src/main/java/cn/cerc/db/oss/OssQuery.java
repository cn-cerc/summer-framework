package cn.cerc.db.oss;

import cn.cerc.core.DataQuery;
import cn.cerc.core.IHandle;
import cn.cerc.db.queue.OssOperator;

import java.io.ByteArrayInputStream;

public class OssQuery extends DataQuery {
    private static final long serialVersionUID = 1L;
    private OssConnection connection;
    private OssOperator operator;
    // 文件名称
    private String fileName;
    private OssMode ossMode = OssMode.create;

    public OssQuery(IHandle handle) {
        super(handle);
        connection = (OssConnection) this.handle.getProperty(OssConnection.sessionId);
    }

    @Override
    public DataQuery open() {
        try {
            this.fileName = getOperator().findTableName(this.getSqlText().getText());
            if (ossMode == OssMode.readWrite) {
                String value = connection.getContent(this.fileName);
                if (value != null) {
                    this.setJSON(value);
                    this.setActive(true);
                }
            }
            return this;
        } catch (Exception e) {
            throw new RuntimeException("语法为: select * from objectId");
        }
    }

    /**
     * 删除文件
     */
    public void remove() {
        connection.delete(this.fileName);
    }

    @Override
    public void save() {
        String content = this.getJSON();
        connection.upload(fileName, new ByteArrayInputStream(content.getBytes()));
    }

    @Override
    public OssOperator getOperator() {
        if (operator == null) {
            operator = new OssOperator();
        }
        return operator;
    }

    public OssMode getOssMode() {
        return ossMode;
    }

    public void setOssMode(OssMode ossMode) {
        this.ossMode = ossMode;
    }

    @Override
    public OssQuery add(String sql) {
        super.add(sql);
        return this;
    }

    @Override
    public OssQuery add(String format, Object... args) {
        super.add(format, args);
        return this;
    }

}
