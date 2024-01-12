package xin.manong.weapon.aliyun.mns;

import com.aliyun.mns.model.Message;

/**
 * MNS消息处理接口
 *
 * @author frankcl
 * @date 2024-01-12 11:43:14
 */
public interface MessageProcessor {

    /**
     * 处理消息
     *
     * @param message 消息
     * @return 成功返回true，否则返回false
     */
    boolean process(Message message);
}
