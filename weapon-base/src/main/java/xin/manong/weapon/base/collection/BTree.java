package xin.manong.weapon.base.collection;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * B+树：阶数m平衡树实现(3<=m<=31，缺省m=13)
 * 1. 所有数据保存在叶子节点，非叶子节点为索引节点
 * 2. 叶子节点数据数量及非叶子节点索引(孩子)数量不小于(m-1)/2+1，不大于m
 * 3. 非叶子节点索引存储下层节点最大key及指向下层节点的引用
 * 4. 非叶子根结点允许最少2个索引(孩子)
 * 5. 支持正向和逆向数据迭代
 * 6. 支持自定义数据比较器
 *
 * @author frankcl
 * @date 2023-10-25 11:37:21
 */
public class BTree<K, V> implements Iterable<Entry<K, V>> {

    private static final int MAX_M = 31;
    private static final int DEFAULT_MAX_M = 13;

    /* 当前数据数量 */
    private int size;
    /* 阶数 */
    private final int m;
    /* 最小孩子数量或数据数量：(m-1)/2+1 */
    private final int n;
    /* 排序比较器 */
    private final Comparator<? super K> comparator;
    /* 根结点 */
    private Node root;

    public BTree() {
        this(DEFAULT_MAX_M);
    }

    public BTree(int m) {
        this(m, null);
    }

    public BTree(int m, Comparator<? super K> comparator) {
        if (m < 3) throw new IllegalArgumentException("m must be greater than 2");
        this.size = 0;
        this.m = Math.min(m, MAX_M);
        this.n = (this.m - 1) / 2 + 1;
        this.comparator = comparator;
        this.root = null;
    }

    /**
     * 数据数量
     *
     * @return 数据数量
     */
    public int size() {
        return size;
    }

    /**
     * 是否为空
     *
     * @return 没有数据返回true，否则返回false
     */
    public boolean isEmpty() {
        return root == null;
    }

    /**
     * 添加数据
     * 1. 如果数据key已经存在，则使用value覆盖原值，返回false
     * 2. 添加数据之后，如果破坏节点结构规则，需调整节点
     *   a）如果节点数据量(索引量)大于m，需要分裂节点
     *   b）如果节点最大值改变，需要修改其父节点索引数据
     *   c）以上a）和b）操作可能产生链式影响
     *
     * @param key 数据key，如果key为null抛出异常NullPointerException
     * @param value 数据值，如果value为null抛出异常NullPointerException
     * @return 如果key不存在返回true，否则返回false
     */
    public boolean add(K key, V value) {
        if (key == null) throw new NullPointerException();
        if (value == null) throw new NullPointerException();
        if (root == null) {
            root = new Leaf(new Entry<>(key, value), comparator);
            size++;
            return true;
        }
        Leaf leaf = findLeaf(key);
        assert leaf != null;
        K removedKey = compare(key, leaf.getMaxKey(), comparator) > 0 ? leaf.getMaxKey() : null;
        if (!leaf.add(key, value)) return false;
        postAdd(leaf, removedKey);
        size++;
        return true;
    }

    /**
     * 移除数据
     * 1. 如果数据key存在则删除数据并返回数据值，否则不做任何操作并返回null
     * 2. 删除数据后，如果破坏节点结构规则，需调整节点
     *   a）如果节点数据量(索引量)小于(m-1)/2+1，进行以下步骤调整
     *     1）如果兄弟节点数据量(索引量)大于m，则像兄弟节点借取一个数据(索引)，否则进行2）
     *     2）与兄弟节点合并
     *     3）1）和2）会对父节点索引造成影响，需要调整父节点索引数据
     *   b）删除数据可能影响父节点索引数据，需要对父节点索引数据进行调整
     *   c）以上a）和b）操作可能造成链式影响
     *
     * @param key 数据key，如果key为null抛出异常NullPointerException
     * @return 成功返回数据值，否则返回null
     */
    public V remove(K key) {
        if (key == null) throw new NullPointerException();
        Leaf leaf = findLeaf(key);
        if (leaf == null) return null;
        V removedValue = leaf.remove(key);
        if (removedValue == null) return null;
        postRemove(leaf, key);
        size--;
        return removedValue;
    }

    /**
     * 移除第一个元素
     *
     * @return 成功返回移除元素，否则返回null
     */
    public Entry<K, V> removeFirst() {
        Leaf firstLeaf = getFirstLeaf();
        if (firstLeaf == null || firstLeaf.isEmpty()) return null;
        Entry<K, V> entry = firstLeaf.entries.remove(0);
        postRemove(firstLeaf, entry.getKey());
        size--;
        return entry;
    }

    /**
     * 移除最后一个元素
     *
     * @return 成功返回移除元素，否则返回null
     */
    public Entry<K, V> removeLast() {
        Leaf lastLeaf = getLastLeaf();
        if (lastLeaf == null || lastLeaf.isEmpty()) return null;
        Entry<K, V> entry = lastLeaf.entries.remove(lastLeaf.entries.size() - 1);
        postRemove(lastLeaf, entry.getKey());
        size--;
        return entry;
    }

    /**
     * 获取第一个元素
     *
     * @return 存在返回第一个元素，否则返回null
     */
    public Entry<K, V> getFirst() {
        Leaf firstLeaf = getFirstLeaf();
        if (firstLeaf == null || firstLeaf.isEmpty()) return null;
        return firstLeaf.entries.get(0);
    }

    /**
     * 获取最后一个元素
     *
     * @return 存在返回最后一个元素，否则返回null
     */
    public Entry<K, V> getLast() {
        Leaf lastLeaf = getLastLeaf();
        if (lastLeaf == null || lastLeaf.isEmpty()) return null;
        return lastLeaf.entries.get(lastLeaf.entries.size() - 1);
    }

    /**
     * 搜索数据
     *
     * @param key 数据key，如果key为null抛出异常NullPointerException
     * @return 如果key存在返回数据，否则返回null
     */
    public V search(K key) {
        if (key == null) throw new NullPointerException();
        if (root == null) return null;
        Leaf leaf = findLeaf(key);
        if (leaf == null) return null;
        return leaf.search(key);
    }

    /**
     * 搜索数据范围
     * 如果startKey大于endKey抛出异常IllegalArgumentException
     *
     * @param startKey 起始key，如果key为null抛出异常NullPointerException
     * @param endKey 结束key，如果key为null抛出异常NullPointerException
     * @return 返回在起始key（包含）和结束key（包含）范围内数据
     */
    public List<V> search(K startKey, K endKey) {
        if (startKey == null || endKey == null) {
            throw new NullPointerException();
        }
        if (compare(startKey, endKey, comparator) > 0) {
            throw new IllegalArgumentException("start key is greater than end key");
        }
        List<V> values = new ArrayList<>();
        if (root == null) return values;
        Leaf leaf = findLeaf(startKey);
        if (leaf == null) return values;
        while (leaf != null) {
            List<V> searchResults = leaf.search(startKey, endKey);
            if (searchResults.isEmpty()) break;
            values.addAll(searchResults);
            leaf = leaf.next;
        }
        return values;
    }

    /**
     * 格式化BTree数据
     *
     * @return 格式化字符串
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Leaf leaf = getFirstLeaf();
        while (leaf != null && leaf.entries != null) {
            for (Entry<K, V> entry : leaf.entries) {
                if (builder.length() > 0) builder.append(",");
                builder.append(entry.getKey()).append("=").append(entry.getValue());
            }
            leaf = leaf.next;
        }
        builder.insert(0, "[").append("]");
        return builder.toString();
    }

    /**
     * 获取数据迭代器
     *
     * @return 数据迭代器
     */
    @NotNull
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
     * 添加数据后处理
     * 1. 如果添加数据影响节点最大值，调整父节点索引数据
     * 2. 如果节点数据(索引)数量大于m，则分裂节点
     * 3. 分裂节点可能需要调整父节点索引数据
     * 4. 以上操作可能产生链式影响
     *
     * @param node 添加数据的叶子节点
     */
    private void postAdd(Node node, K removedKey) {
        while (node != null) {
            Node parent = node.parent;
            boolean rebuild = rebuildNode(parent, node, removedKey);
            boolean split = splitNode(node);
            if (!rebuild && !split) break;
            node = parent;
        }
    }

    /**
     * 分裂节点
     * 将节点一分为二，并重建父子节点关系
     *
     * @param node 节点
     * @return 分裂成功返回true，否则返回false
     */
    private boolean splitNode(Node node) {
        if (node.getChildCount() <= m) return false;
        Node parent = node.parent;
        if (parent == null) {
            parent = new Node(comparator);
            root = parent;
        }
        List<Node> nodes = node.split();
        parent.removeChild(node.getMaxKey());
        parent.addChild(new Entry<>(nodes.get(0).getMaxKey(), nodes.get(0)));
        parent.addChild(new Entry<>(nodes.get(1).getMaxKey(), nodes.get(1)));
        return true;
    }

    /**
     * 重建节点
     * 1. 移除原先子节点
     * 2. 添加新子节点
     *
     * @param parent 重建节点
     * @param node 新子节点
     * @param removedKey 移除key
     * @return 重建成功返回true，否则返回null
     */
    private boolean rebuildNode(Node parent, Node node, K removedKey) {
        if (removedKey == null || parent == null) return false;
        Entry<K, Node> removedChild = parent.removeChild(removedKey);
        if (removedChild != null) parent.addChild(new Entry<>(node.getMaxKey(), node));
        return removedChild != null;
    }

    /**
     * 从叶子借数据
     * 借取数据可能需要调整父节点索引数据
     *
     * @param leaf 借取数据叶子节点
     * @param borrowedLeaf 被借邻居叶子
     */
    private void borrowFromLeaf(Leaf leaf, Leaf borrowedLeaf) {
        int c = borrowedLeaf.compareTo(leaf);
        Entry<K, V> borrowedEntry = c < 0 ? borrowedLeaf.removeLast() : borrowedLeaf.removeFirst();
        assert borrowedEntry != null;
        K borrowedKey = borrowedEntry.getKey();
        K leafMaxKey = leaf.getMaxKey();
        if (!leaf.add(borrowedKey, borrowedEntry.getValue())) throw new RuntimeException("borrow leaf failed");
        postBorrow(borrowedLeaf, borrowedKey, leaf, leafMaxKey, c);
    }

    /**
     * 从中间节点借数据
     * 借取数据可能需要调整父节点索引数据
     *
     * @param node 借取数据中间节点
     * @param borrowedNode 被借邻居中间节点
     */
    private void borrowFromNode(Node node, Node borrowedNode) {
        int c = borrowedNode.compareTo(node);
        Entry<K, Node> borrowedEntry = c < 0 ? borrowedNode.removeLastChild() : borrowedNode.removeFirstChild();
        K borrowedKey = borrowedEntry.getKey();
        K nodeMaxKey = node.getMaxKey();
        node.addChild(borrowedEntry);
        postBorrow(borrowedNode, borrowedKey, node, nodeMaxKey, c);
    }

    /**
     * 借取数据后处理
     * 1. 如果借取邻居最大key，则修改父节点对邻居节点引用key
     * 2. 如果借取邻居最小key，则修改父节点对借取节点引用key
     *
     * @param borrowedNode 被借邻居节点
     * @param borrowedKey 借取key
     * @param currentNode 借取数据节点
     * @param currentMaxKey 借取数据节点当前最大key
     * @param c 被借邻居节点与借取数据节点比较结果
     */
    private void postBorrow(Node borrowedNode, K borrowedKey,
                            Node currentNode, K currentMaxKey, int c) {
        Node parent = borrowedNode.parent;
        if (parent == null) return;
        if (c < 0) {
            parent.removeChild(borrowedKey);
            parent.addChild(new Entry<>(borrowedNode.getMaxKey(), borrowedNode));
            return;
        }
        parent.removeChild(currentMaxKey);
        parent.addChild(new Entry<>(currentNode.getMaxKey(), currentNode));
    }

    /**
     * 借用或合并节点
     * 1. 如果邻居节点数据(索引)数量大于m，则从邻居节点借取数据
     * 2. 如果邻居节点无法借取数据，则和邻居节点合并
     *
     * @param node 处理节点
     * @return 借用或合并成功返回true，否则返回false
     */
    private boolean borrowMergeNode(Node node) {
        Node parent = node.parent;
        if (node.getChildCount() >= n || parent == null) return false;
        K maxKey = node.getMaxKey();
        Entry<K, Node> siblingEntry = parent.getAvailableSibling(maxKey);
        if (siblingEntry == null) {
            throw new RuntimeException(String.format("sibling not found for key[%s]", maxKey.toString()));
        }
        Node sibling = siblingEntry.getValue();
        if (sibling.getChildCount() > n) {
            if (node.isLeaf()) borrowFromLeaf((Leaf) node, (Leaf) sibling);
            else borrowFromNode(node, sibling);
            return true;
        }
        Node newNode = node.merge(sibling);
        parent.removeChild(sibling.getMaxKey());
        parent.removeChild(node.getMaxKey());
        parent.addChild(new Entry<>(newNode.getMaxKey(), newNode));
        return true;
    }

    /**
     * 删除节点后操作
     * 删除数据之后，可能破坏节点结构规则，需调整节点
     * 1. 如果删除数据为当前节点最大key，则需要调整父节点索引结构
     * 2. 如果节点数据(索引)数量小于(m-1)/2+1，则需要向邻居节点借取数据(索引)或与邻居节点合并
     * 3. 1和2操作可能产生链式影响
     * 4. 为保证非叶子根结点最少有2个孩子节点，需要删除无效根节点，调整根节点位置
     *
     * @param node 删除叶子节点
     * @param removedKey 删除key
     */
    private void postRemove(Node node, K removedKey) {
        while (node != null && node.parent != null) {
            Node parent = node.parent;
            boolean rebuild = rebuildNode(parent, node, removedKey);
            boolean borrowMerge = borrowMergeNode(node);
            if (!rebuild && !borrowMerge) break;
            node = parent;
        }
        /*
         * 避免存在单孩子的非叶子根节点
         */
        if (node != null && !node.isLeaf() && node.getChildCount() == 1) {
            root = node.removeFirstChild().getValue();
        }
        /*
         * 如果根节点为没有数据的叶子节点，将根节点设置为null
         */
        if (root != null && root.isEmpty()) root = null;
    }

    /**
     * 获取合适容纳key的叶子结点
     * 1. key小于等于叶子节点的最大key，则返回该叶子节点
     * 2. key大于所有叶子节点key，则返回最后一个叶子节点
     *
     * @param key 数据key
     * @return 存在返回叶子结点，否则返回null
     */
    private Leaf findLeaf(K key) {
        if (root == null) return null;
        Node node = root;
        while (!node.isLeaf()) node = node.findChild(key);
        return (Leaf) node;
    }

    /**
     * 获取第一个叶子节点
     *
     * @return 第一个叶子节点
     */
    private Leaf getFirstLeaf() {
        if (root == null) return null;
        Node node = root;
        while (!node.isLeaf()) {
            node = node.children.get(0).getValue();
        }
        return (Leaf) node;
    }

    /**
     * 获取最后一个叶子节点
     *
     * @return 最后一个叶子节点
     */
    private Leaf getLastLeaf() {
        if (root == null) return null;
        Node node = root;
        while (!node.isLeaf()) {
            node = node.children.get(node.children.size() - 1).getValue();
        }
        return (Leaf) node;
    }

    /**
     * 比较数据key
     *
     * @param key1 比较key
     * @param key2 比较key
     * @param comparator 比较器
     * @return 大于返回正数，小于返回负数，等于返回0
     * @param <K> key类型
     */
    @SuppressWarnings("unchecked")
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
    private static <K, V> int search(List<Entry<K, V>> entries, K key,
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

        protected int cursor;
        protected int prevCursor;
        protected Leaf leafCursor;
        protected Leaf prevLeafCursor;

        public EntryIterator() {
            cursor = 0;
            prevCursor = -1;
            leafCursor = getFirstLeaf();
            prevLeafCursor = null;
        }

        @Override
        public boolean hasNext() {
            return leafCursor != null;
        }

        @Override
        public Entry<K, V> next() {
            if (leafCursor == null) return null;
            prevCursor = cursor;
            prevLeafCursor = leafCursor;
            Entry<K, V> entry = leafCursor.entries.get(cursor++);
            if (cursor >= leafCursor.entries.size()) {
                leafCursor = leafCursor.next;
                cursor = 0;
            }
            return entry;
        }

        @Override
        public void remove() {
            if (prevLeafCursor == null) return;
            Entry<K, V> nextEntry = leafCursor == null ? null : leafCursor.entries.get(cursor);
            Entry<K, V> entry = prevLeafCursor.entries.get(prevCursor);
            prevLeafCursor.remove(entry.getKey());
            postRemove(prevLeafCursor, entry.getKey());
            prevLeafCursor = null;
            size--;
            if (nextEntry == null) return;
            leafCursor = findLeaf(nextEntry.getKey());
            assert leafCursor != null;
            cursor = search(leafCursor.entries, nextEntry.getKey(), comparator);
        }
    }

    /**
     * 逆向数据迭代器
     */
    final class ReversedEntryIterator extends EntryIterator {

        public ReversedEntryIterator() {
            leafCursor = getLastLeaf();
            prevLeafCursor = null;
            cursor = leafCursor == null ? -1 : leafCursor.entries.size() - 1;
            prevCursor = -1;
        }

        @Override
        public Entry<K, V> next() {
            if (leafCursor == null) return null;
            prevCursor = cursor;
            prevLeafCursor = leafCursor;
            Entry<K, V> entry = leafCursor.entries.get(cursor--);
            if (cursor < 0) {
                leafCursor = leafCursor.prev;
                cursor = leafCursor == null ? -1 : leafCursor.entries.size() - 1;
            }
            return entry;
        }
    }

    /**
     * 非叶子节点
     */
    class Node implements Comparable<Node> {

        /* 孩子节点 */
        private List<Entry<K, Node>> children;
        /* 父节点 */
        protected Node parent;
        /* 数据比较器 */
        protected Comparator<? super K> comparator;

        public Node(Comparator<? super K> comparator) {
            this.comparator = comparator;
        }

        public Node(List<Entry<K, Node>> children, Comparator<? super K> comparator) {
            this(comparator);
            assert children != null && !children.isEmpty();
            this.children = children;
        }

        /**
         * 获取最大key
         *
         * @return 最大key
         */
        public K getMaxKey() {
            return children == null || children.isEmpty() ? null : children.get(children.size() - 1).getKey();
        }

        /**
         * 获取最小key
         *
         * @return 最小key
         */
        public K getMinKey() {
            return children == null || children.isEmpty() ? null : children.get(0).getKey();
        }

        /**
         * 获取子节点数量
         *
         * @return 子节点数量
         */
        public int getChildCount() {
            return children == null ? 0 : children.size();
        }

        /**
         * 是否为空
         *
         * @return 没有孩子返回true，否则返回false
         */
        public boolean isEmpty() {
            return children == null || children.isEmpty();
        }

        /**
         * 判断是否为叶子节点
         *
         * @return 叶子节点返回true，其他返回false
         */
        public boolean isLeaf() {
            return false;
        }

        /**
         * 分裂节点
         * 一分为二，保证分裂后节点孩子数量相当
         *
         * @return 分裂后节点
         */
        public List<Node> split() {
            if (children == null || children.size() < m) {
                throw new RuntimeException(String.format("not allowed to be split for node[%d]",
                        children == null ? 0 : children.size()));
            }
            int pos = (int) Math.ceil(children.size() * 1.0 / 2);
            Node first = new Node(comparator);
            first.parent= parent;
            for (int i = 0; i < pos; i++) first.addChild(children.get(i));
            Node second = new Node(comparator);
            second.parent= parent;
            for (int i = pos; i < children.size(); i++) second.addChild(children.get(i));
            return Arrays.asList(first, second);
        }

        /**
         * 合并节点
         *
         * @param node 待合并节点，如果节点为null或节点孩子为空抛出异常NullPointerException
         * @return 合并结果
         */
        public Node merge(Node node) {
            if (node == null) throw new NullPointerException();
            if (node.children == null || node.children.isEmpty()) {
                throw new IllegalArgumentException("merged node is empty");
            }
            if (node.parent != parent) throw new IllegalArgumentException("parent of merged node is not valid");
            int c = compareTo(node);
            Node newNode = new Node(comparator);
            if (c < 0) {
                for (Entry<K, Node> entry : children) newNode.addChild(entry);
                for (Entry<K, Node> entry : node.children) newNode.addChild(entry);
            } else {
                for (Entry<K, Node> entry : node.children) newNode.addChild(entry);
                for (Entry<K, Node> entry : children) newNode.addChild(entry);
            }
            newNode.parent = parent;
            return newNode;
        }

        /**
         * 寻找合适容纳key的孩子节点，满足容纳key的孩子节点条件
         * 1. key小于等于孩子节点的最大key，则返回该孩子节点
         * 2. key大于所有孩子节点key，则返回最后一个孩子节点
         *
         * @param key 数据key
         * @return 合适容纳key的孩子节点
         */
        public Node findChild(K key) {
            if (children == null || children.isEmpty()) return null;
            for (Entry<K, Node> entry : children) {
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
        public void addChild(Entry<K, Node> child) {
            if (children == null) children = new ArrayList<>();
            int pos = children.size();
            for (int i = 0; i < children.size(); i++) {
                Entry<K, Node> entry = children.get(i);
                int c = compare(child.getKey(), entry.getKey(), comparator);
                if (c == 0) throw new RuntimeException(String.format("child has existed for key[%s]", child.getKey()));
                else if (c > 0) continue;
                pos = i;
                break;
            }
            children.add(pos, child);
            child.getValue().parent = this;
        }

        /**
         * 删除孩子节点
         *
         * @param key 数据key
         * @return 成功返回孩子节点，否则返回null
         */
        public Entry<K, Node> removeChild(K key) {
            int pos = BTree.search(children, key, comparator);
            if (pos == -1) return null;
            Entry<K, Node> child = children.remove(pos);
            child.getValue().parent = null;
            return child;
        }

        /**
         * 删除第一个孩子节点
         *
         * @return 成功返回孩子节点，否则返回null
         */
        public Entry<K, Node> removeFirstChild() {
            if (children == null || children.isEmpty()) return null;
            Entry<K, Node> child = children.remove(0);
            child.getValue().parent = null;
            return child;
        }

        /**
         * 删除最后一个孩子节点
         *
         * @return 成功返回孩子节点，否则返回null
         */
        public Entry<K, Node> removeLastChild() {
            if (children == null || children.isEmpty()) return null;
            Entry<K, Node> child = children.remove(children.size() - 1);
            child.getValue().parent = null;
            return child;
        }

        /**
         * 根据数据key获取可用邻居节点
         * 1. 如果左邻居存在返回左邻居节点
         * 2. 如果右邻居存在返回右邻居节点
         * 3. 否则返回null
         *
         * @param key 数据key
         * @return 存在返回邻居节点，否则返回null
         */
        public Entry<K, Node> getAvailableSibling(K key) {
            Entry<K, Node> sibling = getPrevSibling(key);
            return sibling == null ? getNextSibling(key) : sibling;
        }

        /**
         * 获取key的前向邻居
         *
         * @param key 数据key
         * @return 存在返回邻居节点，否则返回null
         */
        public Entry<K, Node> getPrevSibling(K key) {
            int pos = BTree.search(children, key, comparator);
            if (pos - 1 < 0) return null;
            return children.get(pos - 1);
        }

        /**
         * 获取key的后向邻居
         *
         * @param key 数据key
         * @return 存在返回邻居节点，否则返回null
         */
        public Entry<K, Node> getNextSibling(K key) {
            int pos = BTree.search(children, key, comparator);
            if (pos == -1 || pos + 1 >= children.size()) return null;
            return children.get(pos + 1);
        }

        @Override
        public int compareTo(Node node) {
            K maxKey = getMaxKey();
            K minKey = node.getMinKey();
            return compare(maxKey, minKey, comparator);
        }
    }

    /**
     * 叶子结点
     *
     */
    final class Leaf extends Node {

        /* 数据列表 */
        private final List<Entry<K, V>> entries;
        /* 后序节点 */
        private Leaf next;
        /* 前序节点 */
        private Leaf prev;

        public Leaf(Entry<K, V> entry, Comparator<? super K> comparator) {
            super(comparator);
            assert entry != null;
            entries = new ArrayList<>();
            entries.add(entry);
        }

        public Leaf(List<Entry<K, V>> entries, Comparator<? super K> comparator) {
            super(comparator);
            assert entries != null && !entries.isEmpty();
            this.entries = entries;
        }

        /**
         * 获取数据数量
         *
         * @return 数据数量
         */
        @Override
        public int getChildCount() {
            return entries == null ? 0 : entries.size();
        }

        /**
         * 获取最大key
         *
         * @return 最大key
         */
        @Override
        public K getMaxKey() {
            return entries == null || entries.isEmpty() ? null : entries.get(entries.size() - 1).getKey();
        }

        /**
         * 获取最小key
         *
         * @return 最小key
         */
        @Override
        public K getMinKey() {
            return entries == null || entries.isEmpty() ? null : entries.get(0).getKey();
        }

        /**
         * 是否为空
         *
         * @return 无数据返回true，否则返回false
         */
        @Override
        public boolean isEmpty() {
            return entries == null || entries.isEmpty();
        }

        @Override
        public boolean isLeaf() {
            return true;
        }

        /**
         * 合并叶子节点
         * 1. 保证数据排序
         * 2. 调整前向及后向叶子节点引用
         *
         * @param node 叶子节点
         * @return 合并结果
         */
        @Override
        public Node merge(Node node) {
            if (!node.isLeaf()) throw new IllegalArgumentException("not leaf");
            Leaf leaf = (Leaf) node;
            if (leaf.entries == null || leaf.entries.isEmpty()) {
                throw new IllegalArgumentException("merged leaf is empty");
            }
            int c = compareTo(leaf);
            Leaf prevLeaf = c < 0 ? prev : leaf.prev;
            Leaf nextLeaf = c < 0 ? leaf.next : next;
            List<Entry<K, V>> newEntries = new ArrayList<>(c < 0 ? entries : leaf.entries);
            newEntries.addAll(c < 0 ? leaf.entries : entries);
            Leaf newLeaf = new Leaf(newEntries, comparator);
            newLeaf.prev = c < 0 ? prev : leaf.prev;
            newLeaf.next = c < 0 ? leaf.next : next;
            if (prevLeaf != null) prevLeaf.next = newLeaf;
            if (nextLeaf != null) nextLeaf.prev = newLeaf;
            return newLeaf;
        }

        /**
         * 分裂节点
         * 一分为二，如果数据数量小于m则抛出异常RuntimeException
         * 1. 保证分裂后叶子节点数据排序
         * 2. 调整分裂后叶子节点前向及后向节点引用
         *
         * @return 分裂后节点
         */
        @Override
        public List<Node> split() {
            if (entries == null || entries.size() < m) {
                throw new IllegalStateException(String.format("not allowed to be split for leaf[%d]",
                        entries == null ? 0 : entries.size()));
            }
            Leaf prevLeaf = prev;
            Leaf nextLeaf = next;
            int pos = (int) Math.ceil(entries.size() * 1.0 / 2);
            Leaf first = new Leaf(new ArrayList<>(entries.subList(0, pos)), comparator);
            Leaf second = new Leaf(new ArrayList<>(entries.subList(pos, entries.size())), comparator);
            first.next = second;
            first.prev = prev;
            second.prev = first;
            second.next = next;
            if (prevLeaf != null) prevLeaf.next = first;
            if (nextLeaf != null) nextLeaf.prev = second;
            return Arrays.asList(first, second);
        }

        /**
         * 添加数据
         * 1. 如果数据key存在则使用value覆盖原数据，并返回false
         * 2. 如果数据key不存在则添加数据
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
            entries.add(pos, new Entry<>(key, value));
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

        /**
         * 删除最后一个数据
         *
         * @return 成功返回数据，否则返回null
         */
        public Entry<K, V> removeLast() {
            if (entries == null || entries.isEmpty()) return null;
            return entries.remove(entries.size() - 1);
        }

        /**
         * 删除第一个数据
         *
         * @return 成功返回数据，否则返回null
         */
        public Entry<K, V> removeFirst() {
            if (entries == null || entries.isEmpty()) return null;
            return entries.remove(0);
        }

        @Override
        public int compareTo(Node node) {
            if (!node.isLeaf()) throw new IllegalArgumentException("node is not leaf");
            return super.compareTo(node);
        }
    }
}
