package cn.cerc.db.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BigStorage implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(BigStorage.class);

    private BigTable<?> table;
    private BigControl control;
    private int sleep = 1000;
    private int maximum = 100;

    public BigStorage(BigTable<?> table) {
        this.table = table;
    }

    @Override
    public void run() {
        if (control == null) {
            throw new RuntimeException("BigStorage.control is null!");
        }
        try {
            while (control.getActive().get()) {
                log.debug("check save");
                save();
                Thread.sleep(sleep);
            }
            saveAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveAll() throws Exception {
        int count = table.updateList.size() + table.deleteList.size();
        while (count > 100) {
            table.post(100);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count -= 100;
        }
        table.post(0);
    }

    public void save() {
        table.post(maximum);
    }

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    public int getSleep() {
        return sleep;
    }

    public void setSleep(int sleep) {
        this.sleep = sleep;
    }

    public BigControl getControl() {
        return control;
    }

    public void setControl(BigControl control) {
        this.control = control;
    }

}
