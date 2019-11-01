package cn.cerc.db.other;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TickCount {

    private long lastTime;

    public TickCount() {
        this.lastTime = System.currentTimeMillis();
    }

    public static void main(String[] args) {
        TickCount tick = new TickCount();
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(100);
                tick.print("test");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void print(String message) {
        log.info(String.format("%s tickCount: %s", message, System.currentTimeMillis() - lastTime));
        this.lastTime = System.currentTimeMillis();
    }
}
