package com.manong.weapon.base.log;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author frankcl
 * @date 2022-07-23 13:07:45
 */
public class JSONLoggerSuite {

    JSONLogger logger;

    @Before
    public void setUp() {
        Set<String> keys = new HashSet();
        keys.add("k1");
        logger = new JSONLogger("test.log", keys);
    }

    @Test
    public void testLogger() {

        Map<String, Object> featureMap = new HashMap<>();
        featureMap.put("k1", "v1");
        featureMap.put("k2", 222);
        logger.commit(featureMap);
    }
}
