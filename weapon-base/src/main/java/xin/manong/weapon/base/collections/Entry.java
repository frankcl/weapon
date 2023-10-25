package xin.manong.weapon.base.collections;

import java.util.Objects;

/**
 * key及value数据
 *
 * @author frankcl
 * @date 2023-10-25 15:37:12
 */
public class Entry<K, V> {

    private final K key;
    private V value;

    public Entry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Entry)) return false;
        Entry<?, ?> e = (Entry<?, ?>) o;
        return Objects.equals(key, e.getKey()) && Objects.equals(value, e.getValue());
    }

    @Override
    public int hashCode() {
        int keyHash = key == null ? 0 : key.hashCode();
        int valueHash = value == null ? 0 : value.hashCode();
        return keyHash ^ valueHash;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }

    /**
     * 获取数据key
     *
     * @return 数据key
     */
    public K getKey() {
        return key;
    }

    /**
     * 获取数据值
     *
     * @return 数据值
     */
    public V getValue() {
        return value;
    }

    /**
     * 设置数据值
     *
     * @param value 数据值
     */
    public void setValue(V value) {
        this.value = value;
    }
}
