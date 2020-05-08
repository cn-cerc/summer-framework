package cn.cerc.sms;

import java.util.ArrayList;
import java.util.List;

public class LambdaTest {
    public static void main(String[] args) {
        List<User> userList = initList(5000);
        for (int i = 1; i < 100; i++) {
            System.out.println("--------------------第" + i + "次");

            long t1 = System.nanoTime();
            testLambda(userList);

            long t2 = System.nanoTime();

            testForeach(userList);
            long t3 = System.nanoTime();

            System.out.println("lambda  ---" + (t2 - t1) / 1000 + " μs");
            System.out.println("foreach ---" + (t3 - t2) / 1000 + " μs");
        }
    }

    /**
     * 初始化测试集合
     */
    private static List<User> initList(int size) {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            userList.add(new User("user" + i, String.valueOf(i)));
        }
        return userList;
    }

    /**
     * 增强for测试
     */
    private static void testForeach(List<User> userList) {
        for (User user : userList) {
            user.hashCode();
        }
    }

    /**
     * lambda forEach测试
     */
    private static void testLambda(List<User> userList) {
        userList.forEach(user -> user.hashCode());
    }

}
