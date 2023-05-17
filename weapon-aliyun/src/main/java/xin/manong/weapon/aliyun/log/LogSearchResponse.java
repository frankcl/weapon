package xin.manong.weapon.aliyun.log;

import xin.manong.weapon.base.record.KVRecords;

/**
 * 日志搜索响应
 *
 * @author frankcl
 * @date 2023-02-28 15:37:33
 */
public class LogSearchResponse {

    public boolean status;
    public Long totalCount;
    public String message;
    public KVRecords records;

    /**
     * 构建错误响应
     *
     * @param message 错误信息
     * @return 错误响应对象
     */
    public static LogSearchResponse buildError(String message) {
        LogSearchResponse response = new LogSearchResponse();
        response.status = false;
        response.message = message == null ? "" : message;
        return response;
    }

    /**
     * 构建成功响应
     *
     * @param records 数据列表
     * @param totalCount 总数
     * @return 成功响应对象
     */
    public static LogSearchResponse buildOK(KVRecords records, Long totalCount) {
        LogSearchResponse response = new LogSearchResponse();
        response.status = true;
        response.totalCount = totalCount;
        response.records = records;
        return response;
    }
}
