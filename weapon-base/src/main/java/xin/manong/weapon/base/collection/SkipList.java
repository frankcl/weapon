package xin.manong.weapon.base.collection;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

/**
 * 跳表实现
 * 1. 数据节点层数大于0小于等于31，缺省层数13
 * 2. 支持获取和删除首元素和尾元素
 * 3. 支持数据正向和逆向迭代
 * 4. 支持自定义数据比较器
 *
 * @author frankcl
 * @date 2023-10-17 15:46:37
 */
public class SkipList<K, V> implements Iterable<Entry<K, V>> {

    private static final int DEFAULT_MAX_LEVEL = 13;
    private static final int MAX_MAX_LEVEL = 31;

    /* 当前层数 */
    private int level;
    /* 数据数量 */
    private int size;
    /* 最大层数 */
    private final int maxLevel;
    /* 头节点 */
    private final Node<K, V> headNode;
    /* 尾节点 */
    private final Node<K, V> tailNode;
    /* 数据比较器 */
    private final Comparator<? super K> comparator;
    /* 随机数生成器 */
    private final Random random;

    public SkipList() {
        this(DEFAULT_MAX_LEVEL, null);
    }

    public SkipList(int maxLevel) {
        this(maxLevel, null);
    }

    public SkipList(Comparator<? super K> comparator) {
        this(DEFAULT_MAX_LEVEL, comparator);
    }

    public SkipList(int maxLevel, Comparator<? super K> comparator) {
        if (maxLevel <= 0) throw new IllegalArgumentException(String.format("illegal max level[%d]", maxLevel));
        this.level = 0;
        this.size = 0;
        this.random = new Random();
        this.maxLevel = maxLevel > MAX_MAX_LEVEL ? MAX_MAX_LEVEL : maxLevel;
        this.comparator = comparator;
        headNode = new Node<>(null, this.maxLevel);
        tailNode = new Node<>(null, this.maxLevel);
        for (int i = 0; i < this.maxLevel; i++) {
            headNode.nextNodes[i] = tailNode;
            tailNode.prevNodes[i] = headNode;
        }
    }

    /**
     * 添加数据
     * 1. 数据key存在更新数据值，不存在添加数据
     * 2. 新增数据节点层数随机生成，保证最大层数不超过当前层数加1
     * 3. 添加节点后需要调整前后相关节点的前向及后向节点引用
     *
     * @param key 数据key，如果key为null抛出异常NullPointerException
     * @param value 数据值，如果value为null抛出异常NullPointerException
     * @return 如果key存在，使用value覆盖原值并返回false，否则返回true
     */
    public boolean add(K key, V value) {
        if (key == null) throw new NullPointerException();
        if (value == null) throw new NullPointerException();
        Node[] updateNodes = new Node[maxLevel];
        Node<K, V> node = headNode;
        for (int i = level - 1; i >= 0; i--) {
            while (compare(key, node.nextNodes[i]) > 0) node = node.nextNodes[i];
            updateNodes[i] = node;
        }
        node = node.nextNodes[0];
        if (compare(key, node) == 0) {
            node.entry.setValue(value);
            return false;
        }
        int nodeLevel = randomLevel();
        if (nodeLevel > level) {
            nodeLevel = ++level;
            updateNodes[nodeLevel-1] = headNode;
        }
        Node<K, V> newNode = new Node(new Entry(key, value), nodeLevel);
        for (int i = nodeLevel - 1; i >= 0; i--) {
            node = updateNodes[i];
            newNode.nextNodes[i] = node.nextNodes[i];
            newNode.prevNodes[i] = node;
            node.nextNodes[i].prevNodes[i] = newNode;
            node.nextNodes[i] = newNode;
        }
        size++;
        return true;
    }

    /**
     * 根据key删除数据
     * 1. 如果key不存在，不进行任何操作并返回null，否则删除数据并返回数据值
     * 2. 删除数据节点后需要进行以下调整
     *   a）需要调整前后向相关节点的节点引用
     *   b）可能需要降低跳表当前层数
     *
     * @param key 数据key，如果key为null抛出异常NullPointerException
     * @return 成功返回数据值，否则返回null
     */
    public V remove(K key) {
        if (key == null) throw new NullPointerException();
        Node<K, V> node = headNode;
        for (int i = level - 1; i >= 0; i--) {
            while (compare(key, node.nextNodes[i]) > 0) node = node.nextNodes[i];
        }
        node = node.nextNodes[0];
        if (compare(key, node) != 0) return null;
        V value = node.entry.getValue();
        removeNode(node);
        return value;
    }

    /**
     * 根据key获取值
     *
     * @param key 数据key，如果key为null抛出异常NullPointerException
     * @return 如果存在返回数据值，否则返回null
     */
    public V get(K key) {
        if (key == null) throw new NullPointerException();
        Node<K, V> node = headNode;
        for (int i = level - 1; i >= 0; i--) {
            while (compare(key, node.nextNodes[i]) > 0) node = node.nextNodes[i];
        }
        node = node.nextNodes[0];
        if (compare(key, node) == 0) return node.entry.getValue();
        return null;
    }

    /**
     * 移除首元素
     * 1. 如果跳表为空返回null
     * 2. 删除首元素，需要进行相关调整
     *   a）需要调整前后向相关节点的节点引用
     *   b）可能需要降低跳表当前层数
     *
     * @return 如果表为空返回null，否则返回首元素
     */
    public Entry<K, V> removeFirst() {
        Node<K, V> node = headNode.nextNodes[0];
        if (node == tailNode) return null;
        removeNode(node);
        return node.entry;
    }

    /**
     * 移除尾元素
     * 1. 如果跳表为空返回null
     * 2. 删除尾元素，需要进行相关调整
     *   a）需要调整前后向相关节点的节点引用
     *   b）可能需要降低跳表当前层数
     *
     * @return 如果表为空返回null，否则返回尾元素
     */
    public Entry<K, V> removeLast() {
        Node<K, V> node = tailNode.prevNodes[0];
        if (node == headNode) return null;
        removeNode(node);
        return node.entry;
    }

    /**
     * 获取首元素，如果跳表为空返回null
     *
     * @return 成功返回元素值，否则返回null
     */
    public Entry<K, V> getFirst() {
        if (headNode.nextNodes[0] == tailNode) return null;
        return headNode.nextNodes[0].entry;
    }

    /**
     * 获取尾元素值，如果跳表为空返回null
     *
     * @return 成功返回元素值，否则返回null
     */
    public Entry<K, V> getLast() {
        if (tailNode.prevNodes[0] == headNode) return null;
        return tailNode.prevNodes[0].entry;
    }

    /**
     * 列表是否为空
     *
     * @return 列表为空返回true，否则返回false
     */
    public boolean isEmpty() {
        return headNode.nextNodes[0] == tailNode;
    }

    /**
     * 获取数据数量
     *
     * @return 数据数量
     */
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        Node<K, V> node = headNode.nextNodes[0];
        while (node != tailNode) {
            if (buffer.length() != 0) buffer.append(",");
            buffer.append(node.entry);
            node = node.nextNodes[0];
        }
        buffer.insert(0, "[").append("]");
        return buffer.toString();
    }

    /**
     * 获取数据迭代器
     *
     * @return 数据迭代器
     */
    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new EntryIterator();
    }

    /**
     * 获取逆向数据迭代器
     *
     * @return 逆向数据迭代器
     */
    public Iterator<Entry<K, V>> reversedIterator() {
        return new ReversedEntryIterator();
    }

    /**
     * 移除节点
     * 1. 移除节点
     * 2. 修改节点相关前后向节点的引用
     * 3. 降低跳表当前层数
     *
     * @param node 节点
     */
    private void removeNode(Node<K, V> node) {
        if (node == null) return;
        Node[] updateNodes = new Node[node.level];
        for (int i = 0; i < node.level; i++) updateNodes[i] = node.prevNodes[i];
        for (int i = 0; i < node.level; i++) {
            node.nextNodes[i].prevNodes[i] = updateNodes[i];
            updateNodes[i].nextNodes[i] = node.nextNodes[i];
        }
        while (level > 0 && headNode.nextNodes[level-1] == tailNode) level--;
        size--;
    }

    /**
     * 比较key和node
     *
     * @param key 数据key
     * @param node 节点
     * @return 比较结果
     */
    private int compare(K key, Node<K, V> node) {
        if (node == headNode) return 1;
        if (node == tailNode) return -1;
        return comparator == null ? ((Comparable<? super K>) key).compareTo(node.entry.getKey()) :
                comparator.compare(key, node.entry.getKey());
    }

    /**
     * 随机level生成
     *
     * @return level
     */
    private int randomLevel() {
        return random.nextInt(maxLevel) + 1;
    }

    /**
     * 跳表数据迭代器
     */
    final class EntryIterator implements Iterator<Entry<K, V>> {

        private Node<K, V> cursor;

        public EntryIterator() {
            cursor = headNode;
        }

        @Override
        public boolean hasNext() {
            return cursor != tailNode && cursor.nextNodes[0] != tailNode;
        }

        @Override
        public Entry<K, V> next() {
            if (cursor == tailNode || cursor.nextNodes[0] == tailNode) return null;
            Node<K, V> nextNode = cursor.nextNodes[0];
            cursor = nextNode;
            return nextNode.entry;
        }

        @Override
        public void remove() {
            if (cursor == headNode || cursor == tailNode) return;
            Node prevCursor = cursor.prevNodes[0];
            removeNode(cursor);
            cursor = prevCursor;
        }
    }

    /**
     * 跳表反向数据迭代器
     */
    final class ReversedEntryIterator implements Iterator<Entry<K, V>> {

        private Node<K, V> cursor;

        public ReversedEntryIterator() {
            cursor = tailNode;
        }

        @Override
        public boolean hasNext() {
            return cursor != headNode && cursor.prevNodes[0] != headNode;
        }

        @Override
        public Entry<K, V> next() {
            if (cursor == headNode || cursor.prevNodes[0] == headNode) return null;
            Node<K, V> prevNode = cursor.prevNodes[0];
            cursor = prevNode;
            return prevNode.entry;
        }

        @Override
        public void remove() {
            if (cursor == headNode || cursor == tailNode) return;
            Node nextCursor = cursor.nextNodes[0];
            removeNode(cursor);
            cursor = nextCursor;
        }
    }

    /**
     * 跳表节点
     *
     * @param <K> 数据key
     * @param <V> 数据值
     */
    static final class Node<K, V> {
        /* 层数 */
        private final int level;
        /* 数据 */
        private final Entry<K, V> entry;
        /* 后向节点引用 */
        private final Node<K, V>[] nextNodes;
        /* 前向节点引用 */
        private final Node<K, V>[] prevNodes;

        public Node(Entry<K, V> entry, int level) {
            assert level > 0;
            this.entry = entry;
            this.level = level;
            this.nextNodes = new Node[level];
            this.prevNodes = new Node[level];
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof Node)) return false;
            Node n = (Node) o;
            return entry.equals(n.entry);
        }

        @Override
        public int hashCode() {
            return entry.hashCode();
        }

        @Override
        public String toString() {
            return entry.toString();
        }
    }
}
