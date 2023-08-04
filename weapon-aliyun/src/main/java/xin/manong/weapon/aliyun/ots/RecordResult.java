package xin.manong.weapon.aliyun.ots;

import org.apache.commons.lang3.StringUtils;
import xin.manong.weapon.base.record.KVRecord;

/**
 * 数据结果
 *
 * @author frankcl
 * @date 2023-08-03 16:36:32
 */
public class RecordResult {

    /**
     * 成功true，失败false
     */
    public boolean success;
    /**
     * 错误信息
     */
    public String message;
    /**
     * 结果数据
     */
    public KVRecord record;

    /**
     * 构建成功数据结果
     *
     * @param record 数据
     * @return 成功数据结果
     */
    public static RecordResult buildSuccessResult(KVRecord record) {
        RecordResult result = new RecordResult();
        result.success = true;
        result.record = record;
        return result;
    }

    /**
     * 构建失败数据结果
     *
     * @param record 数据
     * @param message 错误信息
     * @return 失败数据结果
     */
    public static RecordResult buildFailResult(KVRecord record, String message) {
        RecordResult result = new RecordResult();
        result.success = false;
        result.record = record;
        result.message = StringUtils.isEmpty(message) ? "" : message;
        return result;
    }
}
