package cn.cerc.mis.message;

import cn.cerc.mis.queue.AsyncService;

public enum MessageProcess {
    stop, wait, working, ok, error;

    public String getTitle() {
        return AsyncService.getProcessTitle(this.ordinal());
    }
}
