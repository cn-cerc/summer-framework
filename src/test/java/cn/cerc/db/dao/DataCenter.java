package cn.cerc.db.dao;

public class DataCenter extends CustomControl {

    private static DataCenter instance;

    private DaoPerson person;

    public synchronized static DataCenter getInstance() {
        if (instance == null) {
            instance = new DataCenter();
            instance.init();
        }
        return instance;
    }

    private void init() {
        System.gc();
        person = new DaoPerson(this);
        person.open();
    }

    public DaoPerson getPerson() {
        return person;
    }

}
