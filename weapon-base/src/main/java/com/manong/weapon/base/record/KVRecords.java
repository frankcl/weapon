package com.manong.weapon.base.record;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * KV数据列表
 *
 * @author frankcl
 * @create 2019-05-27 13:23
 */
public class KVRecords implements Serializable {

    private final static Logger logger = LoggerFactory.getLogger(KVRecords.class);

    private final List<KVRecord> kvRecords;

    public KVRecords() {
        kvRecords = new ArrayList<>();
    }

    /**
     * 拷贝
     *
     * @return 拷贝数据
     */
    public KVRecords copy() {
        KVRecords kvRecords = new KVRecords();
        for (KVRecord kvRecord : this.kvRecords) {
            kvRecords.addRecord(kvRecord.copy());
        }
        return kvRecords;
    }

    /**
     * 获取KVRecord数量
     *
     * @return 大于等于0的值
     */
    public int getRecordCount() {
        return kvRecords == null ? 0 : kvRecords.size();
    }

    /**
     * 判断KVRecord列表是否为空
     *
     * @return 为空返回true，否则返回false
     */
    public boolean isEmpty() {
        return kvRecords == null || kvRecords.isEmpty();
    }

    /**
     * 获取指定下标的KVRecord
     *
     * @param index 下标，必须大于等于0且小于记录数
     * @return 如果下标合法返回KVRecord，否则返回null
     */
    public KVRecord getRecord(int index) {
        if (index < 0 || index >= getRecordCount()) {
            logger.error("index[{}] out of record range[{}, {})", index, 0, getRecordCount());
            return null;
        }
        return kvRecords.get(index);
    }

    /**
     * 添加KVRecord
     * 如果KVRecord为null，不生效
     *
     * @param kvRecord 添加KVRecord
     */
    public void addRecord(KVRecord kvRecord) {
        if (kvRecord == null) return;
        kvRecords.add(kvRecord);
    }

    /**
     * 添加KVRecord列表
     * 如果KVRecord列表为null或者为空，不生效
     *
     * @param kvRecords 添加KVRecord列表
     */
    public void addRecords(KVRecords kvRecords) {
        if (kvRecords == null || kvRecords.isEmpty()) return;
        this.kvRecords.addAll(kvRecords.kvRecords);
    }

    /**
     * 清除所有记录
     */
    public void clear() {
        kvRecords.clear();
    }

    @Override
    public String toString() {
        int count = getRecordCount();
        StringBuffer buffer = new StringBuffer();
        buffer.append("kv record num[").append(count).append("]").append("\n");
        for (int i = 0; i < count; i++) {
            buffer.append(getRecord(i).toString()).append("\n");
        }
        return buffer.toString();
    }
}
