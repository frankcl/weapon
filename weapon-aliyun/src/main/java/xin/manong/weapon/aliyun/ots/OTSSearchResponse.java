package xin.manong.weapon.aliyun.ots;

import xin.manong.weapon.base.record.KVRecords;

/**
 * OTS搜索响应
 *
 * @author frankcl
 * @date 2023-02-28 15:37:33
 */
public class OTSSearchResponse {

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
    public static OTSSearchResponse buildError(String message) {
        OTSSearchResponse response = new OTSSearchResponse();
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
    public static OTSSearchResponse buildOK(KVRecords records, Long totalCount) {
        OTSSearchResponse response = new OTSSearchResponse();
        response.status = true;
        response.totalCount = totalCount;
        response.records = records;
        return response;
    }
}
