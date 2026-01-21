package xin.manong.weapon.base.milvus;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索响应
 *
 * @author frankcl
 * @date 2026-01-21 13:18:40
 */
public class MilvusSearchResponse<T> {

    public long sessionTs;
    public List<MilvusRecord<T>> records = new ArrayList<>();

    /**
     * 添加数据
     *
     * @param record 数据
     */
    public void addRecord(MilvusRecord<T> record) {
        if (records == null) records = new ArrayList<>();
        records.add(record);
    }

    /**
     * 解包数据
     *
     * @return 数据列表
     */
    public List<T> unwrap() {
        return records.stream().map(record -> record.value).toList();
    }
}
