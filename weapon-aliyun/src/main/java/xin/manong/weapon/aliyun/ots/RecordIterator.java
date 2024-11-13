package xin.manong.weapon.aliyun.ots;

import com.alicloud.openservices.tablestore.model.Row;
import xin.manong.weapon.base.record.KVRecord;

import java.util.Iterator;

/**
 * 数据迭代器
 * 负责迭代OTS范围数据，将Row对象转换为KVRecord对象
 *
 * @author frankcl
 * @date 2022-08-08 14:31:02
 */
public class RecordIterator {

    private final Iterator<Row> iterator;

    public RecordIterator(Iterator<Row> iterator) {
        this.iterator = iterator;
    }

    /**
     * 是否存在迭代数据
     *
     * @return 存在数据返回true，否则返回false
     */
    public boolean hasNext() {
        return iterator.hasNext();
    }

    /**
     * 迭代数据
     *
     * @return 数据
     */
    public KVRecord next() {
        Row row = iterator.next();
        if (row == null) return null;
        return OTSConverter.convertRecord(row);
    }
}
