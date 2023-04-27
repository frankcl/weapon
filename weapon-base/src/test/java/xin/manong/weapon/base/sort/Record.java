package xin.manong.weapon.base.sort;

import java.util.HashMap;
import java.util.Map;

/**
 * @author frankcl
 * @date 2023-04-27 20:21:41
 */
public class Record {

    public String key;
    public Map<String, Object> featureMap;

    public Record() {
    }

    public Record(String key) {
        this.key = key;
        this.featureMap = new HashMap<>();
    }

    public Record put(String key, Object value) {
        featureMap.put(key, value);
        return this;
    }
}
