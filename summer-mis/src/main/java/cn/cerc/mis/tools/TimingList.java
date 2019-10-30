package cn.cerc.mis.tools;

import java.util.ArrayList;
import java.util.List;

public class TimingList {
    private List<Timing> items = new ArrayList<>();

    public Timing get(String name) {
        for (Timing timer : items) {
            if (name.equals(timer.getName())) {
                return timer;
            }
        }
        Timing obj = new Timing(name);
        items.add(obj);
        return obj;
    }

    /**
     * 此函数仅供手动测试时使用
     */
    public void print() {
        // 显示执行时间
        for (Timing timing : this.items) {
            System.out.println(String.format("%s, total: %d", timing.getName(), timing.getTotal()));
        }
    }

}
