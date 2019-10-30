package cn.cerc.mis.tools;

import com.google.gson.Gson;

public class Timing {
    private String name;
    private long startTime = 0;
    private long stopTime = 0;
    private int total = 0;

    public Timing() {

    }

    public Timing(String name) {
        this.setName(name);
    }

    public Timing start() {
        startTime = System.currentTimeMillis();
        return this;
    }

    public Timing stop() {
        stopTime = System.currentTimeMillis();
        total += (int) (stopTime - startTime);
        return this;
    }

    public int getTotal() {
        if (stopTime == 0)
            stop();
        return total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
