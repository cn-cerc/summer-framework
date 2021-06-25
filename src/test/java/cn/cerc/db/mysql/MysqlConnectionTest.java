package cn.cerc.db.mysql;

import cn.cerc.db.core.Handle;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.StubSession;

public class MysqlConnectionTest {

    public static void main(String[] args) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                IHandle handle = new Handle(new StubSession());
                try (Transaction tx = new Transaction(handle)) {
                    MysqlQuery ds = new MysqlQuery(handle);
                    ds.add("select * from sql_test");
                    ds.open();
//                    while (ds.fetch()) {
//                        ds.edit();
//                        ds.setField("num_", ds.getInt("num_") + 1);
//                        ds.post();
//                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    ds.append();
                    ds.setField("code_", "a1");
                    ds.setField("name_", "xxx");
                    ds.setField("num_", 1);
                    ds.post();

                    ds.append();
                    ds.setField("code_", "a1");
                    ds.setField("name_", "xxx");
                    ds.setField("num_", 2);
                    ds.post();
                    tx.commit();
                }
            }
        };
//        new Thread(task).start();
        new Handle(new StubSession()).getMysql().execute("DELETE FROM sql_test");

        for (int i = 0; i < 20; i++) {
            new Thread(task).start();
        }
    }

}
