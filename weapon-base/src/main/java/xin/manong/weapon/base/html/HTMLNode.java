package xin.manong.weapon.base.html;

import org.jsoup.nodes.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * HTML节点信息
 *
 * @author frankcl
 * @date 2022-09-15 13:34:42
 */
public class HTMLNode {

    /* 文本字符数 */
    public int textCount;
    /* 锚文本字符数 */
    public int anchorTextCount;
    /* 子节点数 */
    public int nodeCount;
    /* 子锚节点数 */
    public int anchorNodeCount;
    /* 段落节点数 */
    public int paragraphNodeCount;
    /* 文本节点密度 */
    public double density;
    /* 子节点文本节点密度和 */
    public double sumDensity;
    /* 分数 */
    public double score;
    /* DOM节点 */
    public Node node;
    /* 分段文本字符数列表 */
    public List<Integer> segmentTextCounts;
    /* 子节点列表 */
    public List<HTMLNode> childNodes;
    /* 父节点 */
    public HTMLNode parentNode;

    public HTMLNode(Node node) {
        this.node = node;
        this.segmentTextCounts = new ArrayList<>();
        this.childNodes = new ArrayList<>();
    }

    @Override
    public int hashCode() {
        return node.hashCode();
    }
}
