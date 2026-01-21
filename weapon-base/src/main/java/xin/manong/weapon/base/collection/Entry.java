package xin.manong.weapon.base.collection;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * 数据key和数据值二元组
 *
 * @author frankcl
 * @date 2023-10-25 15:37:12
 */
@Getter
public final class Entry<K, V> {

    /* 数据key，不可更改 */
    private final K key;
    /* 数据值 */
    @Setter
    private V value;

    public Entry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * 1. 数据key和数据值相等则判定相等
     * 2. 对比对象为null或不是Entry对象，则判定不相等
     *
     * @param o 对比对象
     * @return 相等返回true，否则返回false
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Entry<?, ?> e)) return false;
        return Objects.equals(key, e.getKey()) && Objects.equals(value, e.getValue());
    }

    /**
     * 数据key和数据值hashCode取或
     *
     * @return hash值
     */
    @Override
    public int hashCode() {
        int keyHash = key == null ? 0 : key.hashCode();
        int valueHash = value == null ? 0 : value.hashCode();
        return keyHash ^ valueHash;
    }

    /**
     * 字符串格式化：key=value
     *
     * @return 格式化字符串
     */
    @Override
    public String toString() {
        return key + "=" + value;
    }

}
