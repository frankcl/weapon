package xin.manong.weapon.base.collections;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * B+树实现
 *
 * @author frankcl
 * @date 2023-10-25 11:37:21
 */
public class BTree<K, V> implements Iterable<Entry<K, V>> {

    private static final int MAX_M = 21;

    private int size;
    private final int m;
    private final int n;
    private final Comparator<? super K> comparator;
    private Node<K> currentNode;

    public BTree(int m) {
        this(m, null);
    }

    public BTree(int m, Comparator<? super K> comparator) {
        if (m < 3) throw new IllegalArgumentException("m must be greater than 2");
        this.size = 0;
        this.m = m > MAX_M ? MAX_M : m;
        this.n = (this.m - 1) / 2 + 1;
        this.comparator = comparator;
        this.currentNode = null;
    }

    /**
     * 元素数量
     *
     * @return 元素数量
     */
    public int size() {
        return size;
    }

    /**
     * 是否为空
     *
     * @return 为空返回true，否则返回false
     */
    public boolean isEmpty() {
        return currentNode == null;
    }

    /**
     * 添加数据
     *
     * @param key 数据key
     * @param value 数据值
     * @return 成功返回true，否则返回false
     */
    public boolean add(K key, V value) {
        if (key == null) throw new IllegalArgumentException("key is not allowed to be null");
        if (value == null) throw new IllegalArgumentException("value is not allowed to be null");
        if (currentNode == null) {
            Leaf<K, V> leaf = new Leaf<>(new Entry<>(key, value), comparator);
            currentNode = leaf;
            return true;
        }
        Leaf<K, V> leaf = findLeaf(key);
        if (!leaf.add(key, value)) return false;
        size++;
        if (leaf.entries.size() <= m) return true;
        //TODO
        return true;
    }

    /**
     * 移除数据
     *
     * @param key 数据key
     * @return 成功返回true，否则返回false
     */
    public boolean remove(K key) {
        if (key == null) throw new IllegalArgumentException("key is not allowed to be null");
        return false;
    }

    /**
     * 搜索数据
     *
     * @param key 数据key
     * @return 存在返回数据，否则返回null
     */
    public V search(K key) {
        if (key == null) throw new IllegalArgumentException("key is not allowed to be null");
        if (currentNode == null) return null;
        Leaf<K, V> leaf = findLeaf(key);
        if (leaf == null) return null;
        return leaf.search(key);
    }

    /**
     * 搜索数据范围
     *
     * @param startKey 起始key
     * @param endKey 结束key
     * @return 返回在起始key（包含）和结束key（包含）范围内数据
     */
    public List<V> search(K startKey, K endKey) {
        if (startKey == null || endKey == null) {
            throw new IllegalArgumentException("key is not allowed to be null");
        }
        if (compare(startKey, endKey, comparator) > 0) {
            throw new IllegalArgumentException("start key is greater than end key");
        }
        List<V> values = new ArrayList<>();
        if (currentNode == null) return values;
        Leaf<K, V> leaf = findLeaf(startKey);
        if (leaf == null) return values;
        while (leaf != null) {
            List<V> searchResults = leaf.search(startKey, endKey);
            if (searchResults.isEmpty()) break;
            values.addAll(searchResults);
            leaf = leaf.next;
        }
        return values;
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new EntryIterator();
    }

    /**
     * 合并叶子
     *
     * @param leaf1 叶子节点
     * @param leaf2 叶子节点
     * @return 合并叶子节点
     */
    private Leaf<K, V> merge(Leaf<K, V> leaf1, Leaf<K, V> leaf2) {
        return null;
    }

    /**
     * 合并中间节点
     *
     * @param node1 中间节点
     * @param node2 中间节点
     * @return 合并中间节点
     */
    private Node<K> merge(Node<K> node1, Node<K> node2) {
        return null;
    }

    /**
     * 分裂叶子节点
     *
     * @param leaf 叶子节点
     * @return 分裂结果
     */
    private Leaf<K, V>[] split(Leaf<K, V> leaf) {
        Leaf<K, V>[] leaves = new Leaf[2];
        return leaves;
    }

    /**
     * 分裂中间节点
     *
     * @param node 中间节点
     * @return 分裂结果
     */
    private Node<K>[] split(Node<K> node) {
        Node<K>[] nodes = new Node[2];
        return nodes;
    }

    /**
     * 获取合适容纳key的叶子结点
     * 1. key小于等于叶子节点的最大key，则返回该叶子节点
     * 2. key大于所有叶子节点key，则返回最后一个叶子节点
     *
     * @param key 数据key
     * @return 存在返回叶子结点，否则返回null
     */
    private Leaf<K, V> findLeaf(K key) {
        if (currentNode == null) return null;
        Node<K> node = currentNode;
        while (!(node instanceof Leaf)) node = node.findChild(key);
        return (Leaf<K, V>) node;
    }

    /**
     * 获取第一个叶子节点
     *
     * @return 第一个叶子节点
     */
    private Leaf<K, V> getFirstLeaf() {
        if (currentNode == null) return null;
        Node<K> node = currentNode;
        while (!(node instanceof Leaf)) {
            node = node.children.get(0).getValue();
        }
        return (Leaf<K, V>) node;
    }

    /**
     * 获取最后一个叶子节点
     *
     * @return 最后一个叶子节点
     */
    private Leaf<K, V> getLastLeaf() {
        if (currentNode == null) return null;
        Node<K> node = currentNode;
        while (!(node instanceof Leaf)) {
            node = node.children.get(node.children.size() - 1).getValue();
        }
        return (Leaf<K, V>) node;
    }

    /**
     * 比较数据key
     *
     * @param key1 比较key
     * @param key2 比较key
     * @param comparator 比较器
     * @return 大于返回正数，小于返回负数，等于返回0
     * @param <K>
     */
    private static <K> int compare(K key1, K key2, Comparator<? super K> comparator) {
        return comparator == null ? ((Comparable<? super K>) key1).compareTo(key2) :
                comparator.compare(key1, key2);
    }

    /**
     * 二分查找指定key
     *
     * @param entries 查找列表
     * @param key 查找key
     * @param comparator 比较器
     * @return 存在key返回下标位置，否则返回-1
     * @param <K> 数据key
     * @param <V> 数据值
     */
    public static <K, V> int search(List<Entry<K, V>> entries, K key,
                                    Comparator<? super K> comparator) {
        if (key == null) return -1;
        if (entries == null || entries.isEmpty()) return -1;
        int start = 0, end = entries.size() - 1, mid = (start + end) / 2;
        while (true) {
            Entry<K, V> entry = entries.get(mid);
            int c = compare(key, entry.getKey(), comparator);
            if (c == 0) return mid;
            else if (c < 0) end = mid - 1;
            else start = mid + 1;
            if (start > end) break;
            mid = (start + end) / 2;
        }
        return -1;
    }

    /**
     * 数据迭代器
     */
    class EntryIterator implements Iterator<Entry<K, V>> {

        private int cursor;
        private Leaf<K, V> leafCursor;

        public EntryIterator() {
            cursor = 0;
            leafCursor = getFirstLeaf();
        }

        @Override
        public boolean hasNext() {
            return leafCursor != null;
        }

        @Override
        public Entry<K, V> next() {
            if (leafCursor == null) return null;
            Entry<K, V> entry = leafCursor.entries.get(cursor);
            if (cursor == leafCursor.entries.size() - 1) {
                leafCursor = leafCursor.next;
                cursor = 0;
            }
            return entry;
        }

        @Override
        public void remove() {

        }
    }

    /**
     * 中间节点
     *
     * @param <K> 数据key
     */
    class Node<K> {

        /* 孩子节点 */
        private List<Entry<K, Node<K>>> children;
        /* 父节点 */
        protected Node<K> parent;
        /* 比较器 */
        protected Comparator<? super K> comparator;

        public Node(Comparator<? super K> comparator) {
            assert comparator != null;
            this.comparator = comparator;
        }

        /**
         * 寻找合适容纳key的孩子节点，满足容纳key的孩子节点条件
         * 1. key小于等于孩子节点的最大key，则返回该孩子节点
         * 2. key大于所有孩子节点key，则返回最后一个孩子节点
         *
         * @param key 数据key
         * @return 合适容纳key的孩子节点
         */
        public Node<K> findChild(K key) {
            for (Entry<K, Node<K>> entry : children) {
                if (compare(key, entry.getKey(), comparator) <= 0) return entry.getValue();
            }
            return children.get(children.size() - 1).getValue();
        }

        /**
         * 添加孩子节点
         * 如果存在相同key孩子节点，抛出异常
         *
         * @param child 孩子节点
         */
        public void addChild(Entry<K, Node<K>> child) {
            int pos = children.size();
            for (int i = 0; i < children.size(); i++) {
                Entry<K, Node<K>> entry = children.get(i);
                int c = compare(child.getKey(), entry.getKey(), comparator);
                if (c == 0) throw new RuntimeException(String.format("child has existed for key[%s]", child.getKey()));
                else if (c > 0) continue;
                pos = i;
                break;
            }
            children.add(pos, child);
        }

        /**
         * 删除孩子节点
         *
         * @param key 数据key
         * @return 成功返回孩子节点，否则返回null
         */
        public Entry<K, Node<K>> removeChild(K key) {
            int pos = BTree.search(children, key, comparator);
            if (pos == -1) return null;
            return children.remove(pos);
        }

        /**
         * 根据key获取孩子节点
         *
         * @param key 数据key
         * @return 存在返回孩子节点，否则返回null
         */
        public Entry<K, Node<K>> getChild(K key) {
            int pos = BTree.search(children, key, comparator);
            return pos == -1 ? null : children.get(pos);
        }
    }

    /**
     * 叶子结点
     *
     * @param <K> 数据key
     * @param <V> 数据值
     */
    final class Leaf<K, V> extends Node<K> {

        /* 数据列表 */
        private List<Entry<K, V>> entries;
        /* 后序节点 */
        private Leaf<K, V> next;
        /* 前序节点 */
        private Leaf<K, V> prev;

        public Leaf(Entry<K, V> entry, Comparator<? super K> comparator) {
            super(comparator);
            assert entry != null;
            entries = new ArrayList<>();
            entries.add(entry);
        }

        /**
         * 添加数据：key存在更新数据值，否则添加数据
         *
         * @param key 数据key
         * @param value 数据值
         * @return key存在返回false，否则返回true
         */
        public boolean add(K key, V value) {
            int pos = entries.size();
            for (int i = 0; i < entries.size(); i++) {
                Entry<K, V> entry = entries.get(i);
                int c = compare(key, entry.getKey(), comparator);
                if (c < 0) {
                    pos = i;
                    break;
                } else if (c == 0) {
                    entry.setValue(value);
                    return false;
                }
            }
            entries.add(pos, new Entry(key, value));
            return true;
        }

        /**
         * 移除数据
         *
         * @param key 数据key
         * @return 成功返回移除数据值，否则返回null
         */
        public V remove(K key) {
            int pos = BTree.search(entries, key, comparator);
            return pos == -1 ? null : entries.remove(pos).getValue();
        }

        /**
         * 搜索数据
         *
         * @param key 数据key
         * @return 存在返回数据，否则返回null
         */
        public V search(K key) {
            int pos = BTree.search(entries, key, comparator);
            return pos == -1 ? null : entries.get(pos).getValue();
        }

        /**
         * 范围搜索数据
         *
         * @param startKey 起始key
         * @param endKey 结束key
         * @return 数据列表）
         */
        public List<V> search(K startKey, K endKey) {
            List<V> values = new ArrayList<>();
            for (Entry<K, V> entry : entries) {
                if (compare(entry.getKey(), startKey, comparator) < 0) continue;
                if (compare(entry.getKey(), endKey, comparator) > 0) return values;
                values.add(entry.getValue());
            }
            return values;
        }
    }
}
