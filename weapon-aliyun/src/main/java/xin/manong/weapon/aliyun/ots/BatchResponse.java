package xin.manong.weapon.aliyun.ots;

import java.util.ArrayList;
import java.util.List;

/**
 * 批处理响应
 *
 * @author frankcl
 * @date 2023-08-03 16:39:03
 */
public class BatchResponse {

    public boolean success;
    public List<RecordResult> recordResults;

    public BatchResponse() {
        success = true;
        recordResults = new ArrayList<>();
    }

    /**
     * 添加处理结果
     *
     * @param recordResult 数据结果
     */
    public void addRecordResult(RecordResult recordResult) {
        if (recordResult == null) return;
        recordResults.add(recordResult);
        if (!recordResult.success) success = false;
    }

    /**
     * 获取处理成功数据列表
     *
     * @return 处理成功数据列表
     */
    public List<RecordResult> getSuccessResults() {
        List<RecordResult> results = new ArrayList<>();
        for (RecordResult recordResult : recordResults) {
            if (recordResult.success) results.add(recordResult);
        }
        return results;
    }

    /**
     * 获取处理失败数据列表
     *
     * @return 处理失败数据列表
     */
    public List<RecordResult> getFailResults() {
        List<RecordResult> results = new ArrayList<>();
        for (RecordResult recordResult : recordResults) {
            if (!recordResult.success) results.add(recordResult);
        }
        return results;
    }
}
