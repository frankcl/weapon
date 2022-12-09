package xin.manong.weapon.base.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author frankcl
 * @create 2019-06-02 01:29
 */
public class JSONUtilSuite {

    private String jsonArrayFile = this.getClass().getResource("/json/json_array").getPath();
    private String jsonObjectFile = this.getClass().getResource("/json/json_object").getPath();

    @Test
    public void testMergeObject() {
        Assert.assertTrue(JSONUtil.merge((JSONObject) null, null) == null);
        {
            JSONObject object = new JSONObject();
            object.put("k1", "v1");
            JSONObject result = JSONUtil.merge(object, null);
            Assert.assertEquals(1, result.size());
            Assert.assertTrue(result.containsKey("k1"));
            Assert.assertEquals("v1", result.getString("k1"));

            result = JSONUtil.merge(null, object);
            Assert.assertEquals(1, result.size());
            Assert.assertTrue(result.containsKey("k1"));
            Assert.assertEquals("v1", result.getString("k1"));
        }
        {
            JSONObject object1 = new JSONObject();
            object1.put("k1", "v1");
            object1.put("k2", "v2");
            JSONObject object2 = new JSONObject();
            object2.put("k2", "v22");
            object2.put("k3", "v3");
            JSONObject result = JSONUtil.merge(object1, object2);
            Assert.assertEquals(3, result.size());
            Assert.assertTrue(result.containsKey("k1"));
            Assert.assertEquals("v1", result.getString("k1"));
            Assert.assertTrue(result.containsKey("k2"));
            Assert.assertEquals("v22", result.getString("k2"));
            Assert.assertTrue(result.containsKey("k3"));
            Assert.assertEquals("v3", result.getString("k3"));
        }
    }

    @Test
    public void testMergeArray() {
        JSONArray array1 = new JSONArray();
        array1.add("123");
        array1.add("456");
        JSONArray array2 = new JSONArray();
        array2.add("456");
        array2.add("abc");
        {
            JSONArray array = JSONUtil.merge(array1, null);
            Assert.assertEquals(2, array.size());
            Assert.assertEquals("123", array.getString(0));
            Assert.assertEquals("456", array.getString(1));
        }
        {
            JSONArray array = JSONUtil.merge(null, array2);
            Assert.assertEquals(2, array.size());
            Assert.assertEquals("456", array.getString(0));
            Assert.assertEquals("abc", array.getString(1));
        }
        {
            JSONArray array = JSONUtil.merge(array1, array2);
            Assert.assertEquals(3, array.size());
            Assert.assertEquals("123", array.getString(0));
            Assert.assertEquals("abc", array.getString(1));
            Assert.assertEquals("456", array.getString(2));
        }
    }

    @Test
    public void testGet() {
        String content = FileUtil.read(jsonArrayFile, Charset.forName("UTF-8"));
        JSONArray array = JSON.parseArray(content);
        {
            String companyId = (String) JSONUtil.get(array.getJSONObject(0), "company.id");
            Assert.assertTrue(companyId != null && companyId.equals("company_id1"));
        }
        {
            List<Object> objects = JSONUtil.get(array, "market.code");
            Assert.assertTrue(objects != null && objects.size() == 4);
            Assert.assertEquals("market_code11", objects.get(0));
            Assert.assertEquals("market_code12", objects.get(1));
            Assert.assertEquals("market_code21", objects.get(2));
            Assert.assertEquals("market_code22", objects.get(3));
        }
        {
            List<Object> objects = JSONUtil.get(array, "industry.id");
            Assert.assertTrue(objects != null && objects.size() == 2);
            Assert.assertEquals("industry_id1", objects.get(0));
            Assert.assertEquals("industry_id2", objects.get(1));
        }
        {
            Assert.assertTrue(null == JSONUtil.get(array, "industry.test"));
        }
    }

    @Test
    public void testDeepCopy() {
        JSONArray array = new JSONArray();
        JSONObject object1 = new JSONObject();
        object1.put("k1", "v1");
        array.add(object1);
        JSONObject object2 = new JSONObject();
        object2.put("k2", "v2");
        array.add(object2);

        JSONArray replica = JSONUtil.deepCopy(array);
        Assert.assertTrue(replica != array);
        Assert.assertEquals(2, replica.size());
        {
            JSONObject object = replica.getJSONObject(0);
            Assert.assertTrue(object != object1);
            Assert.assertEquals(1, object.size());
            Assert.assertEquals("v1", object.getString("k1"));
        }
        {
            JSONObject object = replica.getJSONObject(1);
            Assert.assertTrue(object != object2);
            Assert.assertEquals(1, object.size());
            Assert.assertEquals("v2", object.getString("k2"));
        }
    }

    @Test
    public void testSelectJSONObject() {
        String content = FileUtil.read(jsonObjectFile, Charset.forName("UTF-8"));
        JSONObject object = JSON.parseObject(content);
        Set<String> keys = new HashSet<>();
        keys.add("key1.company.id");
        keys.add("key1.company.category");
        keys.add("key1.description");
        keys.add("key2");
        keys.add("key3.array");
        JSONObject select = JSONUtil.select(object, keys);
        Assert.assertEquals(2, select.size());
    }

    @Test
    public void testSelectJSONArray() {
        String content = FileUtil.read(jsonArrayFile, Charset.forName("UTF-8"));
        JSONArray array = JSON.parseArray(content);
        Set<String> keys = new HashSet<>();
        keys.add("company.id");
        keys.add("market.name");
        JSONArray select = JSONUtil.select(array, keys);
        Assert.assertEquals(2, select.size());
    }
}
