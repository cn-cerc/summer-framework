package cn.cerc.db.core;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cn.cerc.core.ISession;
import cn.cerc.db.mysql.MysqlServerMaster;
import cn.cerc.db.mysql.Transaction;

public class TransactionTest implements IHandle {
    private ISession session;
    private MysqlServerMaster conn;

    @Before
    public void setUp() {
        session = new StubSession();
        conn = this.getMysql();
    }

    @Test
    @Ignore
    public void test_0() throws SQLException {
        // value + 0
        try (Transaction tx = new Transaction(this)) {
            conn.execute("update Dept set amount_=amount_err+1 where uid_=1");
            conn.execute("update Dept set amount_=amount_+1 where uid_=1");
            System.out.println("main commit: " + tx.commit());
        }
    }

    @Test
    @Ignore
    public void test_1() throws SQLException {
        // value + 0
        try (Transaction tx = new Transaction(this)) {
            conn.execute("update Dept set amount_=amount_err+1 where uid_=1");
            System.out.println("main commit: " + tx.commit());
            conn.execute("update Dept set amount_=amount_+1 where uid_=1");
            System.out.println("main commit: " + tx.commit());
        }
    }

    @Test
    @Ignore
    public void test_2() throws SQLException {
        // value + 1
        try (Transaction tx = new Transaction(this)) {
            conn.execute("update Dept set amount_=amount_+1 where uid_=1");
            System.out.println("main commit: " + tx.commit());
            conn.execute("update Dept set amount_=amount_+1 where uid_=1");
            System.out.println("main commit: " + tx.commit());
        }
    }

    @Test
    @Ignore
    public void test_3() throws SQLException {
        // value + 1
        try (Transaction tx = new Transaction(this)) {
            conn.execute("update Dept set amount_=amount_+1 where uid_=1");
            System.out.println("main commit: " + tx.commit());
            conn.execute("update Dept set amount_=amount_err+1 where uid_=1");
            System.out.println("main commit: " + tx.commit());
        }
    }

    @Test
    @Ignore
    public void test_4() throws SQLException {
        // value + 3
        try (Transaction tx = new Transaction(this)) {
            conn.execute("update Dept set amount_=amount_+1 where uid_=1");
            child_ok();
            conn.execute("update Dept set amount_=amount_+1 where uid_=1");
            System.out.println("main commit: " + tx.commit());
        }
    }

    @Test
    @Ignore
    public void test_5() throws SQLException {
        // value + 0
        try (Transaction tx = new Transaction(this)) {
            conn.execute("update Dept set amount_=amount_+1 where uid_=1");
            child_error();
            conn.execute("update Dept set amount_=amount_+1 where uid_=1");
            System.out.println("main commit: " + tx.commit());
        }
    }

    private void child_ok() {
        try (Transaction tx = new Transaction(this)) {
            conn.execute("update Dept set amount_=amount_+1 where uid_=1");
            System.out.println("child commit: " + tx.commit());
        }
    }

    private void child_error() {
        try (Transaction tx = new Transaction(this)) {
            conn.execute("update Dept set amount_=amount_error+1 where uid_=1");
            System.out.println("child commit: " + tx.commit());
        }
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
