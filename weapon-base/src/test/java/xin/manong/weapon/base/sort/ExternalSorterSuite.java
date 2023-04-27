package xin.manong.weapon.base.sort;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

/**
 * @author frankcl
 * @date 2023-04-27 16:32:04
 */
public class ExternalSorterSuite {

    private String tempDirectory = "./temp/sort/";

    @Test
    public void testSortRecord() throws Exception {
        ExternalSorter<Record> sorter = new ExternalSorter<>(Record.class, new RecordComparator(), tempDirectory);
        sorter.registerSerializeClass(HashMap.class);
        sorter.setMaxCacheRecordNum(2);
        sorter.setMaxOpenFileNum(2);

        sorter.addRecord(new Record("abc"));
        sorter.addRecord(new Record("efc"));
        sorter.addRecord(new Record("aac"));
        sorter.addRecord(new Record("bfb").put("k1", 123L));
        sorter.addRecord(new Record("ddc"));
        sorter.addRecord(new Record("cbf"));
        sorter.addRecord(new Record("gfc").put("k1", "v1"));
        {
            Record record = sorter.getRecord();
            Assert.assertEquals("aac", record.key);
        }
        {
            Record record = sorter.getRecord();
            Assert.assertEquals("abc", record.key);
        }
        {
            Record record = sorter.getRecord();
            Assert.assertEquals("bfb", record.key);
        }
        {
            Record record = sorter.getRecord();
            Assert.assertEquals("cbf", record.key);
        }
        {
            Record record = sorter.getRecord();
            Assert.assertEquals("ddc", record.key);
        }
        {
            Record record = sorter.getRecord();
            Assert.assertEquals("efc", record.key);
        }
        {
            Record record = sorter.getRecord();
            Assert.assertEquals("gfc", record.key);
        }
        Assert.assertTrue(null == sorter.getRecord());
        Assert.assertTrue(null == sorter.getRecord());

        sorter.close();
    }

    @Test
    public void testSortInt() throws Exception {
        ExternalSorter<Integer> sorter = new ExternalSorter<>(Integer.class, new IntComparator(), tempDirectory);
        sorter.setMaxCacheRecordNum(2);
        sorter.setMaxOpenFileNum(2);

        sorter.addRecord(new Integer(5));
        sorter.addRecord(new Integer(8));
        sorter.addRecord(new Integer(6));
        sorter.addRecord(new Integer(200));
        sorter.addRecord(new Integer(12));
        sorter.addRecord(new Integer(120));
        sorter.addRecord(new Integer(90));
        Assert.assertEquals(5, sorter.getRecord().intValue());
        Assert.assertEquals(6, sorter.getRecord().intValue());
        Assert.assertEquals(8, sorter.getRecord().intValue());
        Assert.assertEquals(12, sorter.getRecord().intValue());
        Assert.assertEquals(90, sorter.getRecord().intValue());
        Assert.assertEquals(120, sorter.getRecord().intValue());
        Assert.assertEquals(200, sorter.getRecord().intValue());
        Assert.assertTrue(sorter.getRecord() == null);
        Assert.assertTrue(sorter.getRecord() == null);

        sorter.close();
    }
}
