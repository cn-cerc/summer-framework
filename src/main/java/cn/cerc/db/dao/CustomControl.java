package cn.cerc.db.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CustomControl implements BigControl {
    private static AtomicBoolean active = new AtomicBoolean(false);
    private static List<BigTable<?>> tables = new ArrayList<>();

    @Override
    public AtomicBoolean getActive() {
        return active;
    }

    @Override
    public void registerTable(BigTable<?> table) {
        if (!tables.contains(table)) {
            tables.add(table);
        }
    }

    public List<BigTable<?>> getTables() {
        return tables;
    }

    public void start() {
        active.set(true);
        for (BigTable<?> table : tables) {
            table.startStorage();
        }
    }

    public void stop() {
        active.set(false);
        for (BigTable<?> table : tables) {
            table.stopStorage();
        }
    }
}
