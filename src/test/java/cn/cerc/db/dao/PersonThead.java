package cn.cerc.db.dao;

public class PersonThead implements Runnable {

    public static void main(String[] args) {
        DataCenter.getInstance().start();
//        CustomControl control = new CustomControl(); // MainData
//        DaoPerson dao = new DaoPerson(control);
//        dao.setTableId("dao1");
//        dao.setDebugConnection(true);
//
//        StubPerson person = new StubPerson("abc");
//        dao.saveInsert(person, true);

        for (int i = 0; i < 100; i++) {
            new Thread(new PersonThead(), "线程" + i).start();
        }

    }

    @Override
    public void run() {
        DaoPerson dao = DataCenter.getInstance().getPerson();
        for (int i = 0; i < 100; i++) {
            StubPerson person = dao.getClone("abc");
            if (person == null)
                dao.saveInsert(new StubPerson("abc"), true);
            else
                dao.saveUpdate(person, true);
        }
    }

}
