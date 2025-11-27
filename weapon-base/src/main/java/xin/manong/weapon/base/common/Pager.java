package xin.manong.weapon.base.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页对象
 *
 * @author frankcl
 * @date 2022-09-21 11:26:50
 */
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Pager<T> implements Serializable {

    public static final int DEFAULT_PAGE_NUM = 1;
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 页码：从1开始
     */
    @JsonProperty("page_num")
    public Integer pageNum;
    /**
     * 分页数量
     */
    @JsonProperty("page_size")
    public Integer pageSize;
    /**
     * 总数
     */
    @JsonProperty("total")
    public Long total;
    /**
     * 数量关系
     * 相等：eq
     * 大于等于：gte
     */
    @JsonProperty("relation")
    public String relation;
    /**
     * 数据列表
     */
    @JsonProperty("records")
    public List<T> records = new ArrayList<>();

    /**
     * 创建空分页
     *
     * @param pageNum 页码
     * @param pageSize 分页数量
     * @return 空分页
     * @param <T> 数据类型
     */
    public static <T> Pager<T> empty(int pageNum, int pageSize) {
        Pager<T> pager = new Pager<>();
        pager.pageNum = pageNum > 0 ? pageNum : DEFAULT_PAGE_NUM;
        pager.pageSize = pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE;
        pager.total = 0L;
        pager.relation = "eq";
        pager.records = new ArrayList<>();
        return pager;
    }
}
