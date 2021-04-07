package cn.cerc.ui.grid;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.cerc.core.DataSet;

public class MutiPage {
    private static final Logger log = LoggerFactory.getLogger(MutiPage.class);
    // 数据源
    private DataSet dataSet;
    // 请求环境
    private HttpServletRequest request;
    // 总记录数
    private int recordCount;
    // 页面大小
    private int pageSize = 100;
    // 当前页
    private int current = 1;
    // 上一页
    private int prior;
    // 上一页
    private int next;
    // 开始记录
    private int begin;
    // 结束记录
    private int end;
    // 总页数
    private int count;

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
        reset();
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        reset();
    }

    public int getCurrent() {
        return current;
    }

    public int getPrior() {
        return prior;
    }

    public void setPrior(int prior) {
        this.prior = prior;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public int getCount() {
        this.count = recordCount / pageSize;
        if ((recordCount % pageSize) > 0) {
            this.count = this.count + 1;
        }
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public final int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public final int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    private void reset() {
        // set prior:
        if (current > 1) {
            this.prior = current - 1;
        } else {
            this.prior = 1;
        }
        // set next:
        if (current >= this.getCount()) {
            this.next = current;
        } else {
            this.next = current + 1;
        }
        // set begin:
        begin = (current - 1) * pageSize;
        if (begin < 0) {
            begin = 0;
        }
        if (begin >= recordCount) {
            begin = this.recordCount - 1;
        }
        // set end:
        end = (current) * pageSize - 1;
        if (end < 0) {
            end = 0;
        }
        if (end >= recordCount) {
            end = this.recordCount - 1;
        }
    }

    @Override
    public String toString() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public boolean isRange(int value) {
        return (value >= this.getBegin()) && (value <= this.getEnd());
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
        if (dataSet != null) {
            this.setRecordCount(dataSet.size());
            reset();
        }
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
        if (request != null) {
            String tmp = request.getParameter("pageno");
            if (tmp != null && !"".equals(tmp)) {
                int current = Integer.parseInt(tmp);
                if (current > 0 && current != this.current) {
                    this.current = current;
                    reset();
                }
            }
        }
    }
}
