package xin.manong.weapon.base.collection;

import java.util.Comparator;

/**
 * @author frankcl
 * @date 2023-04-27 20:22:57
 */
public class RecordComparator implements Comparator<Record> {

    @Override
    public int compare(Record record1, Record record2) {
        if (record1 == record2) return 0;
        if (record1 == null) return -1;
        if (record2 == null) return 1;
        return record1.key.compareTo(record2.key);
    }
}
