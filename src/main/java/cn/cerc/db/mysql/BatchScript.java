package cn.cerc.db.mysql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ISession;
import cn.cerc.core.Utils;
import cn.cerc.db.core.IHandle;

public class BatchScript implements IHandle {
    private static final Logger log = LoggerFactory.getLogger(BatchScript.class);

    private StringBuffer items = new StringBuffer();
    private ISession session;
    private MysqlServerMaster connection;
    private boolean newLine = false;

    public BatchScript(ISession session) {
        this.session = session;
        this.connection = this.getMysql();
    }

    public BatchScript(IHandle owner) {
        this(owner.getSession());
    }

    public BatchScript addSemicolon() {
        items.append(";" + Utils.vbCrLf);
        return this;
    }

    public BatchScript add(String sql) {
        items.append(sql.trim() + " ");
        if (newLine) {
            items.append(Utils.vbCrLf);
        }
        return this;
    }

    public BatchScript add(String format, Object... args) {
        items.append(String.format(format.trim(), args) + " ");
        if (newLine) {
            items.append(Utils.vbCrLf);
        }
        return this;
    }

    public StringBuffer getItems() {
        return items;
    }

    @Override
    public String toString() {
        return items.toString();
    }

    public void print() {
        String[] tmp = items.toString().split(";");
        for (String item : tmp) {
            if (!"".equals(item.trim())) {
                log.info(item.trim() + ";");
            }
        }
    }

    public BatchScript exec() {
        String[] tmp = items.toString().split(";");
        for (String item : tmp) {
            if (!"".equals(item.trim())) {
                log.debug(item.trim() + ";");
                connection.execute(item.trim());
            }
        }
        return this;
    }

    public boolean exists() {
        String[] tmp = items.toString().split(";");
        for (String item : tmp) {
            if (!"".equals(item.trim())) {
                log.debug(item.trim() + ";");
                MysqlQuery ds = new MysqlQuery(this);
                ds.add(item.trim());
                ds.open();
                if (ds.eof()) {
                    return false;
                }
            }
        }
        return tmp.length > 0;
    }

    public boolean isNewLine() {
        return newLine;
    }

    public void setNewLine(boolean newLine) {
        this.newLine = newLine;
    }

    public int size() {
        String[] tmp = items.toString().split(";");
        int len = 0;
        for (String item : tmp) {
            if (!"".equals(item.trim())) {
                len++;
            }
        }
        return len;
    }

    public String getItem(int i) {
        String[] tmp = items.toString().split(";");
        if (i < 0 && i > (tmp.length - 1)) {
            throw new RuntimeException("Command index out of range.");
        }
        return tmp[i].trim();
    }

    public BatchScript clean() {
        items = new StringBuffer();
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
}
