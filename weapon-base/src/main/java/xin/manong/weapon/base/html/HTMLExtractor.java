package xin.manong.weapon.base.html;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.base.util.CommonUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTML正文抽取器
 *
 * @author frankcl
 * @date 2022-09-15 13:26:07
 */
public class HTMLExtractor {

    private final static Logger logger = LoggerFactory.getLogger(HTMLExtractor.class);

    private final static String TAG_NAME_PARAGRAPH = "p";
    private final static String TAG_NAME_SPAN = "span";
    private final static String TAG_NAME_BR = "br";
    private final static String TAG_NAME_IMAGE = "img";
    private final static String TAG_NAME_VIDEO = "video";
    private final static String TAG_NAME_ANCHOR = "a";
    private final static String TAG_NAME_SECTION = "section";

    private final static String ATTR_NAME_WIDTH = "width";
    private final static String ATTR_NAME_HEIGHT = "height";
    private final static String ATTR_NAME_SRC = "src";
    private final static String ATTR_NAME_SOURCE = "source";
    private final static String ATTR_NAME_ABS_SRC = "abs:src";
    private final static String ATTR_NAME_ABS_DATA_SRC = "abs:data-src";
    private final static String ATTR_NAME_STYLE = "style";

    private final static String PROTOCOL_PREFIX_FORMAT = "http:%s";

    private final static Pattern DATE_TIME_PATTERN1 = Pattern.compile(
            "([1-2][0-9]{3})[^0-9]{1,5}?([0-1]?[0-9])[^0-9]{1,5}?([0-9]{1,2})[^0-9]{1,5}?([0-2]?[0-9])[^0-9]{1,5}?([0-9]{1,2})[^0-9]{1,5}?([0-9]{1,2})");
    private final static Pattern DATE_TIME_PATTERN2 = Pattern.compile(
            "([1-2][0-9]{3})[^0-9]{1,5}?([0-1]?[0-9])[^0-9]{1,5}?([0-9]{1,2})[^0-9]{1,5}?([0-2]?[0-9])[^0-9]{1,5}?([0-9]{1,2})");

    private final static Set<String> EXCLUDE_NODES = new HashSet<String>() {{
        add("script");
        add("noscript");
        add("style");
        add("iframe");
        add("select");
        add("input");
        add("button");
    }};

    /**
     * 抽取HTML主体元素
     * 针对新闻文章网页生效
     *
     * @param html 网页HTML
     * @param url 网页URL
     * @return 存在返回正文主体元素，否则返回null
     */
    public static Element mainHTMLElement(String html, String url) {
        if (StringUtils.isEmpty(html)) {
            logger.error("page HTML is empty");
            return null;
        }
        Document document = StringUtils.isEmpty(url) ? Jsoup.parse(html) : Jsoup.parse(html, url);
        document.select(String.join(",", EXCLUDE_NODES)).remove();
        Element body = document.body();
        HTMLNode bodyNode = new HTMLNode(body);
        computeScore(bodyNode);
        return selectMainElement(bodyNode);
    }

    /**
     * 从正文主体中抽取发布时间
     * 如果无法抽取返回null
     *
     * @param mainElement 主体元素
     * @return 成功返回毫秒时间戳，否则返回null
     */
    public static Long publishTime(Element mainElement) {
        Element element = mainElement;
        for (int i = 0; i < 6; i++) {
            if (element == null) return null;
            String html = element.outerHtml();
            Matcher matcher = DATE_TIME_PATTERN1.matcher(html);
            if (matcher.find()) {
                return CommonUtil.stringToTime(String.format("%s-%s-%s %s:%s:%s",
                        matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4),
                        matcher.group(5), matcher.group(6)), null);
            } else {
                matcher = DATE_TIME_PATTERN2.matcher(html);
                if (matcher.find()) return CommonUtil.stringToTime(String.format("%s-%s-%s %s:%s",
                        matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4),
                        matcher.group(5)), "yyyy-MM-dd HH:mm");
            }
            do {
                if (element.tag().getName().equalsIgnoreCase("body")) return null;
                element = element.parent();
            } while (element != null && element.childNodeSize() == 1);
        }
        return null;
    }

    /**
     * 格式化HTML内容
     * 以分段元素p构建HTML内容
     *
     * @param html HTML内容
     * @param url 网页URL
     * @return 格式化HTML内容
     */
    public static String formatHTML(String html, String url) {
        if (StringUtils.isEmpty(html)) return "";
        Document document = StringUtils.isEmpty(url) ? Jsoup.parse(html) : Jsoup.parse(html, url);
        document.select(String.join(",", EXCLUDE_NODES)).remove();
        Element body = document.body();
        return formatHTMLElement(body);
    }

    /**
     * 格式化HTML内容
     * 以分段元素p构建HTML内容
     *
     * @param htmlElement HTML元素
     * @return HTML内容
     */
    public static String formatHTMLElement(Element htmlElement) {
        if (htmlElement == null) return "";
        List<Element> htmlElements = new ArrayList<>();
        for (Node childNode : htmlElement.childNodes()) {
            htmlElements.addAll(buildHTMLElements(childNode));
        }
        StringBuilder builder = new StringBuilder();
        for (Element element : htmlElements) {
            if (builder.length() > 0) builder.append("\n");
            builder.append(element.outerHtml());
        }
        return builder.toString();
    }

    /**
     * 构建HTML元素列表
     * 以分段元素p构建HTML内容
     *
     * @param node HTML节点
     * @return HTML元素列表
     */
    private static List<Element> buildHTMLElements(Node node) {
        List<Element> htmlElements = new ArrayList<>();
        if (node instanceof TextNode) {
            TextNode textNode = (TextNode) node;
            if (textNode.text().trim().isEmpty()) return htmlElements;
            Node parentNode = node.parent();
            boolean block = parentNode instanceof Element &&
                    ((Element) parentNode).isBlock() && parentNode.childNodeSize() == 1;
            Element htmlElement = new Element(block ? TAG_NAME_PARAGRAPH : TAG_NAME_SPAN);
            htmlElement.appendChild(node.clone());
            htmlElements.add(htmlElement);
        } else if (node instanceof Element) {
            Element element = (Element) node;
            String tagName = element.tagName();
            if (!isVisible(element)) return htmlElements;
            if (tagName.equals(TAG_NAME_BR)) {
                Element htmlElement = new Element(TAG_NAME_BR);
                htmlElements.add(htmlElement);
                return htmlElements;
            } else if (tagName.equals(TAG_NAME_IMAGE) || tagName.equals(TAG_NAME_VIDEO)) {
                Element htmlElement = tagName.equals(TAG_NAME_IMAGE) ?
                        buildImageElement(element) : buildVideoElement(element);
                if (htmlElement != null) htmlElements.add(htmlElement);
                return htmlElements;
            }
            List<Element> children = new ArrayList<>();
            for (Node childNode : element.childNodes()) children.addAll(buildHTMLElements(childNode));
            if (children.isEmpty()) return htmlElements;
            if (!element.isBlock() || !containsInline(children)) {
                htmlElements.addAll(children);
                return htmlElements;
            } else if (children.size() == 1) {
                Element htmlElement = new Element(TAG_NAME_PARAGRAPH);
                Element child = children.get(0);
                if (child.tagName().equals(TAG_NAME_PARAGRAPH)) htmlElement.appendChildren(child.childNodes());
                else htmlElement.appendChild(child);
                htmlElements.add(htmlElement);
                return htmlElements;
            }
            Element htmlElement = new Element(TAG_NAME_PARAGRAPH);
            htmlElement.appendChildren(children);
            htmlElements.add(htmlElement);
        }
        return htmlElements;
    }

    /**
     * 构建图片元素
     *
     * @param imageElement 原始图片元素
     * @return 新构建图片元素
     */
    private static Element buildImageElement(Element imageElement) {
        Element htmlElement = new Element(TAG_NAME_IMAGE);
        String sourceURL = imageElement.attr(ATTR_NAME_ABS_SRC);
        if (StringUtils.isEmpty(sourceURL)) sourceURL = imageElement.attr(ATTR_NAME_ABS_DATA_SRC);
        if (StringUtils.isEmpty(sourceURL)) return null;
        fillMediaElement(htmlElement, sourceURL, imageElement);
        return htmlElement;
    }

    /**
     * 构建视频元素
     *
     * @param videoElement 原始视频元素
     * @return 新构建视频元素
     */
    private static Element buildVideoElement(Element videoElement) {
        Element htmlElement = new Element(TAG_NAME_VIDEO);
        String sourceURL = videoElement.attr(ATTR_NAME_ABS_SRC);
        if (StringUtils.isEmpty(sourceURL)) {
            Element sourceElement = findFirstChildElement(videoElement, ATTR_NAME_SOURCE);
            if (sourceElement == null) return null;
            sourceURL = sourceElement.attr(ATTR_NAME_ABS_SRC);
            if (StringUtils.isEmpty(sourceURL)) return null;
        }
        fillMediaElement(htmlElement, sourceURL, videoElement);
        return htmlElement;
    }

    /**
     * 填充媒体元素：video和image
     *
     * @param destElement 目标元素
     * @param sourceURL URL
     * @param sourceElement 来源元素
     */
    private static void fillMediaElement(Element destElement,
                                         String sourceURL, Element sourceElement) {
        if (sourceURL.startsWith("//")) sourceURL = String.format(PROTOCOL_PREFIX_FORMAT, sourceURL);
        destElement.attr(ATTR_NAME_SRC, sourceURL);
        String width = sourceElement.attr(ATTR_NAME_WIDTH);
        if (!StringUtils.isEmpty(width)) destElement.attr(ATTR_NAME_WIDTH, width);
        String height = sourceElement.attr(ATTR_NAME_HEIGHT);
        if (!StringUtils.isEmpty(height)) destElement.attr(ATTR_NAME_HEIGHT, height);
    }

    /**
     * 根据标签名找到第一个子元素
     *
     * @param element 元素
     * @param tagName 标签名
     * @return 返回指定标签名的第一个元素，不存在返回null
     */
    private static Element findFirstChildElement(Element element, String tagName) {
        if (element == null) return null;
        for (Element child : element.children()) {
            if (child.tagName().equalsIgnoreCase(tagName)) return child;
        }
        return null;
    }

    /**
     * 是否包含内敛元素
     *
     * @param elements 元素集合
     * @return 包含返回true，否则返回false
     */
    private static boolean containsInline(List<Element> elements) {
        for (Element element : elements) {
            if (!element.isBlock()) return true;
        }
        return false;
    }

    /**
     * 计算节点分数
     *
     * @param htmlNode HTML节点
     */
    private static void computeScore(HTMLNode htmlNode) {
        if (htmlNode.node instanceof TextNode) {
            TextNode textNode = (TextNode) htmlNode.node;
            int textCount = textNode.text().trim().length();
            htmlNode.textCount = textCount;
            if (textCount > 0) htmlNode.segmentTextCounts.add(textCount);
        } else if (htmlNode.node instanceof Element) {
            Element element = (Element) htmlNode.node;
            String tagName = element.tagName();
            if (!isVisible(element) || tagName.equals(TAG_NAME_BR)) return;
            for (Node childNode : element.childNodes()) {
                if (childNode instanceof Comment) continue;
                HTMLNode childHTMLNode = new HTMLNode(childNode);
                childHTMLNode.parentNode = htmlNode;
                computeScore(childHTMLNode);
                accumulateChildNode(htmlNode, childHTMLNode);
            }
            htmlNode.nodeCount++;
            if (tagName.equals(TAG_NAME_PARAGRAPH) || tagName.equals(TAG_NAME_SECTION)) htmlNode.paragraphNodeCount++;
            else if (tagName.equals(TAG_NAME_ANCHOR)) {
                htmlNode.anchorNodeCount++;
                htmlNode.anchorTextCount = htmlNode.textCount;
            }
            int pureTextCount = htmlNode.textCount - htmlNode.anchorTextCount;
            int pureNodeCount = htmlNode.nodeCount - htmlNode.anchorNodeCount;
            htmlNode.density = pureNodeCount == 0 || pureTextCount == 0 ? 0d : pureTextCount * 1.0d / pureNodeCount;
            double var = computeVariance(htmlNode.segmentTextCounts);
            htmlNode.score = Math.log(var) * htmlNode.sumDensity * Math.log(htmlNode.textCount -
                    htmlNode.anchorTextCount + 1) * Math.log10(htmlNode.paragraphNodeCount + 2);
        }
    }

    /**
     * 判断元素节点可见性
     *
     * @param element 元素节点
     * @return 可见返回true，否则返回false
     */
    private static boolean isVisible(Element element) {
        String style = element.attr(ATTR_NAME_STYLE);
        style = style.replaceAll("\\s", "");
        return !style.contains("display:none");
    }

    /**
     * 计算文本长度方差
     *
     * @param segmentTextCounts 分段文本长度列表
     * @return 方差
     */
    private static double computeVariance(List<Integer> segmentTextCounts) {
        if (segmentTextCounts == null || segmentTextCounts.isEmpty()) return 0d;
        if (segmentTextCounts.size() == 1) return segmentTextCounts.get(0) * 1.0d / 2;
        double sum = 0d;
        for (Integer count : segmentTextCounts) sum += count;
        double mean = sum / segmentTextCounts.size();
        sum = 0d;
        for (Integer count : segmentTextCounts) sum += (count - mean) * (count - mean);
        return Math.sqrt(sum / segmentTextCounts.size() + 1);
    }

    /**
     * 累计子节点信息
     *
     * @param htmlNode 父节点
     * @param childHTMLNode 子节点
     */
    private static void accumulateChildNode(HTMLNode htmlNode, HTMLNode childHTMLNode) {
        htmlNode.textCount += childHTMLNode.textCount;
        htmlNode.anchorTextCount += childHTMLNode.anchorTextCount;
        htmlNode.nodeCount += childHTMLNode.nodeCount;
        htmlNode.anchorNodeCount += childHTMLNode.anchorNodeCount;
        htmlNode.paragraphNodeCount += childHTMLNode.paragraphNodeCount;
        htmlNode.sumDensity += childHTMLNode.density;
        htmlNode.segmentTextCounts.addAll(childHTMLNode.segmentTextCounts);
        htmlNode.childNodes.add(childHTMLNode);
    }

    /**
     * 挑选主体元素：分数最高元素
     *
     * @param bodyNode body节点
     * @return 主体元素
     */
    private static Element selectMainElement(HTMLNode bodyNode) {
        List<HTMLNode> htmlNodes = new LinkedList<>();
        int heapSize = 3;
        PriorityQueue<HTMLNode> nodeQueue = new PriorityQueue<>(heapSize,
                Comparator.comparingDouble(n -> n.score));
        htmlNodes.add(bodyNode);
        while (!htmlNodes.isEmpty()) {
            HTMLNode htmlNode = htmlNodes.remove(0);
            if (Double.isNaN(htmlNode.score) || !(htmlNode.node instanceof Element)) continue;
            if (nodeQueue.size() < heapSize) nodeQueue.offer(htmlNode);
            else if (nodeQueue.peek().score < htmlNode.score) {
                nodeQueue.poll();
                nodeQueue.offer(htmlNode);
            }
            if (htmlNode.childNodes != null) htmlNodes.addAll(htmlNode.childNodes);
        }
        htmlNodes = new ArrayList<>(nodeQueue);
        htmlNodes.sort((n1, n2) -> Double.compare(n2.score, n1.score));
        if (htmlNodes.isEmpty()) return (Element) bodyNode.node;
        HTMLNode mainHTMLNode = selectMainHTMLNode(htmlNodes);
        return (Element) mainHTMLNode.node;
    }

    /**
     * 选择更大范围的主体节点
     *
     * @param htmlNodes 节点列表
     * @return 主体节点
     */
    private static HTMLNode selectMainHTMLNode(List<HTMLNode> htmlNodes) {
        HTMLNode mainHTMLNode = htmlNodes.get(0);
        HTMLNode mainParentNode = findParentHTMLNode(mainHTMLNode);
        if (mainParentNode == null) return mainHTMLNode;
        NodeStat nodeStat = new NodeStat();
        nodeStat.nodeCount = 1;
        nodeStat.textCount = mainHTMLNode.textCount;
        for (int i = 1; i < htmlNodes.size(); i++) {
            HTMLNode htmlNode = htmlNodes.get(i);
            if (htmlNode == mainParentNode) {
                nodeStat.nodeCount += 1;
                nodeStat.textCount = mainParentNode.textCount;
                break;
            }
            HTMLNode parentNode = findParentHTMLNode(htmlNode);
            if (parentNode == null || parentNode != mainParentNode) continue;
            if (htmlNode.textCount < 300 && htmlNode.textCount * 1.0 / mainHTMLNode.textCount < 0.4d) continue;
            nodeStat.nodeCount += 1;
            nodeStat.textCount += htmlNode.textCount;
        }
        return nodeStat.nodeCount > 1 && nodeStat.textCount * 1.0 /
                mainParentNode.textCount >= 0.8 ? mainParentNode : mainHTMLNode;
    }

    /**
     * 获取非独生子女父亲节点
     *
     * @param htmlNode 节点
     * @return 非独生子女父亲节点，如果没有返回null
     */
    private static HTMLNode findParentHTMLNode(HTMLNode htmlNode) {
        if (htmlNode == null) return null;
        HTMLNode parentHTMLNode = htmlNode.parentNode;
        while (parentHTMLNode != null) {
            if (parentHTMLNode.node.childNodeSize() != 1) return parentHTMLNode;
            if (parentHTMLNode.parentNode == null) return parentHTMLNode;
            parentHTMLNode = parentHTMLNode.parentNode;
        }
        return null;
    }

    /**
     * 节点统计信息
     */
    private static class NodeStat {
        public int nodeCount;
        public int textCount;
    }
}
