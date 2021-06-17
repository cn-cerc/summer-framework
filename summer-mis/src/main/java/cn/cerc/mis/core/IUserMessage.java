package cn.cerc.mis.core;

import java.util.List;

import cn.cerc.core.Record;
import cn.cerc.mis.message.MessageLevel;
import cn.cerc.mis.message.MessageProcess;

/**
 * 数据库消息队列
 * 
 * @author ZhangGong
 *
 */
public interface IUserMessage {
    /**
     * 取出所有的等待处理的消息列表
     */
    List<String> getWaitList();
    /**
     * 增加新的消息，并返回消息编号（msgID）
     */
    String appendRecord(String corpNo, String userCode, MessageLevel level, String subject, String content,
            MessageProcess process);
    /**
     * 读取待处理的任务：队列服务
     */
    Record readAsyncService(String msgId);
    /**
     * 更新任务处理进度：队列服务
     */
    boolean updateAsyncService(String msgId, String content, MessageProcess process);
}
