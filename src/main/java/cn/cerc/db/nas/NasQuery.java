package cn.cerc.db.nas;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.DataSet;
import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.core.SqlText;
import cn.cerc.core.Utils;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.queue.QueueOperator;

public class NasQuery extends DataSet implements IHandle {
    private static final Logger log = LoggerFactory.getLogger(NasQuery.class);
    private static final long serialVersionUID = 1L;
    // 文件目录
    private String filePath;
    // 文件名称
    private String fileName;
    private QueueOperator operator;
    private NasModel nasMode = NasModel.create;
    private SqlText sqlText = new SqlText();
    private boolean active;
    private ISession session;

    public NasQuery(IHandle handle) {
        super();
        this.session = handle.getSession();
    }

    public NasQuery open() {
        try {
            this.fileName = this.getSqlText().getText().substring(this.getSqlText().getText().indexOf("select") + 6,
                    this.getSqlText().getText().indexOf("from")).trim();
            this.filePath = SqlText.findTableName(this.getSqlText().getText());
        } catch (Exception e) {
            throw new RuntimeException("command suggest: select fileName from filePath");
        }
        // 校验数据
        if (Utils.isEmpty(this.filePath)) {
            throw new RuntimeException("please enter the file path");
        }
        if (nasMode == NasModel.readWrite) {
            File file = FileUtils.getFile(this.filePath, this.fileName);
            try {
                this.setJSON(FileUtils.readFileToString(file, StandardCharsets.UTF_8.name()));
                this.setActive(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    // 删除文件或目录
    @Override
    protected final void deleteStorage(Record record) {
        File file = FileUtils.getFile(this.filePath, this.fileName);
        FileUtils.deleteQuietly(file);
        log.info("文件:" + file.getPath() + "删除成功");
    }

    public void save() {
        File file = FileUtils.getFile(this.filePath, this.fileName);
        try {
            String content = this.getJSON();
            FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8.name(), false);// 不存在则创建,存在则不追加到文件末尾
        } catch (IOException e) {
            log.info("文件:" + file.getPath() + "保存失败");
            e.printStackTrace();
        }
        log.info("文件:" + file.getPath() + "保存成功");
    }

    public QueueOperator getOperator() {
        if (operator == null) {
            operator = new QueueOperator();
        }
        return operator;
    }

    public NasModel getNasMode() {
        return nasMode;
    }

    public void setNasMode(NasModel nasMode) {
        this.nasMode = nasMode;
    }

    public NasQuery add(String sql) {
        sqlText.add(sql);
        return this;
    }

    public NasQuery add(String format, Object... args) {
        sqlText.add(format, args);
        return this;
    }

    @Override
    public ISession getSession() {
        return session;
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public SqlText getSqlText() {
        return sqlText;
    }

}
