package xin.manong.weapon.base.record;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author frankcl
 * @date 2022-12-14 16:43:04
 */
public class KVRecordConverterSuite {

    @Test
    public void testConvert2JSON() {
        Set<String> fields = new HashSet<>();
        fields.add("k1");
        fields.add("k3");
        fields.add("k4");
        KVRecord kvRecord = new KVRecord();
        kvRecord.put("k1", "v");
        kvRecord.put("k2", 1L);
        kvRecord.put("k3", JSON.parseArray("[1, 2]"));
        kvRecord.put("k4", JSON.parseObject("{\"name\": \"abc\"}"));
        JSONObject json = KVRecordConverter.convert2JSON(kvRecord, fields);
        Assert.assertEquals(3, json.size());
        Assert.assertTrue(json.containsKey("k1"));
        Assert.assertTrue(json.containsKey("k3"));
        Assert.assertTrue(json.containsKey("k4"));
        Assert.assertEquals("v", json.getString("k1"));
        Assert.assertEquals(2, json.getJSONArray("k3").size());
        Assert.assertEquals(1, json.getJSONArray("k3").get(0));
        Assert.assertEquals(2, json.getJSONArray("k3").get(1));
        Assert.assertEquals(1, json.getJSONObject("k4").size());
        Assert.assertTrue(json.getJSONObject("k4").containsKey("name"));
        Assert.assertEquals("abc", json.getJSONObject("k4").getString("name"));
    }
}
