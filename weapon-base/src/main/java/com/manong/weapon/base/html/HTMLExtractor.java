package com.manong.weapon.base.html;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * HTML正文抽取器
 *
 * @author frankcl
 * @date 2022-09-15 13:26:07
 */
public class HTMLExtractor {

    private final static Logger logger = LoggerFactory.getLogger(HTMLExtractor.class);

    private final static Set<String> EXCLUDE_NODES = new HashSet<String>() {{
        add("script");
        add("noscript");
        add("style");
        add("iframe");
        add("br");
        add("select");
        add("input");
        add("button");
        add("comment");
    }};

    /**
     * 抽取HTML主体元素
     *
     * @param url 网页URL
     * @param html 网页HTML
     * @return 存在返回正文主体元素，否则返回null
     */
    public static Element extractMainElement(String url, String html) {
        if (StringUtils.isEmpty(url)) {
            logger.error("page URL is empty");
            return null;
        }
        if (StringUtils.isEmpty(html)) {
            logger.error("page HTML is empty");
            return null;
        }
        Document document = Jsoup.parse(html, url);
        document.select(String.join(",", EXCLUDE_NODES)).remove();
        Element body = document.body();
        if (body == null) {
            logger.warn("page body is not found");
            return null;
        }
        HTMLNode bodyNode = new HTMLNode(body);
        computeScore(bodyNode);
        return selectMainElement(bodyNode);
    }

    /**
     * 构建主体HTML内容
     *
     * @param mainElement 主体元素
     * @return HTML内容
     */
    public static String buildMainHTML(Element mainElement) {
        List<Element> htmlElements = new ArrayList<>();
        for (Node childNode : mainElement.childNodes()) {
            htmlElements.addAll(buildHTMLElements(childNode));
        }
        StringBuffer buffer = new StringBuffer();
        for (Element htmlElement : htmlElements) {
            if (buffer.length() > 0) buffer.append("\n");
            buffer.append(htmlElement.outerHtml());
        }
        return buffer.toString();
    }

    /**
     * 构建HTML元素列表
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
            Boolean block = parentNode instanceof Element && ((Element) parentNode).isBlock() &&
                    parentNode.childNodeSize() == 1 ? true : false;
            Element htmlElement = new Element(block ? "p" : "span");
            htmlElement.appendChild(node.clone());
            htmlElements.add(htmlElement);
        } else if (node instanceof Element) {
            Element element = (Element) node;
            if (element.tagName().equals("img")) {
                htmlElements.add(element.clone());
                return htmlElements;
            }
            List<Element> children = new ArrayList<>();
            for (Node childNode : element.childNodes()) children.addAll(buildHTMLElements(childNode));
            if (children.isEmpty()) return htmlElements;
            if (!element.isBlock() || !containsInline(children)) {
                htmlElements.addAll(children);
                return htmlElements;
            } else if (children.size() == 1) {
                Element htmlElement = new Element("p");
                Element child = children.get(0);
                if (child.tagName().equals("img")) htmlElement.appendChild(child);
                else htmlElement.appendChildren(child.childNodes());
                htmlElements.add(htmlElement);
                return htmlElements;
            }
            Element htmlElement = new Element("p");
            htmlElement.appendChildren(children);
            htmlElements.add(htmlElement);
        }
        return htmlElements;
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
            if (!isVisible(element)) return;
            for (Node childNode : element.childNodes()) {
                if (childNode instanceof Comment) continue;
                HTMLNode childHTMLNode = new HTMLNode(childNode);
                computeScore(childHTMLNode);
                accumulateChildNode(htmlNode, childHTMLNode);
            }
            htmlNode.nodeCount++;
            String tagName = element.tagName();
            if (tagName.equals("p")) htmlNode.paragraphNodeCount++;
            else if (tagName.equals("a")) {
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
        String style = element.attr("style");
        return style != null && (style.replaceAll("\\s", "").indexOf("display:none") == -1);
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
        double sum = 0d, mean = 0d;
        for (Integer count : segmentTextCounts) sum += count;
        mean = sum / segmentTextCounts.size();
        sum = 0d;
        for (Integer count : segmentTextCounts) sum += (count - mean) * (count - mean);
        sum = sum / segmentTextCounts.size();
        return Math.sqrt(sum + 1);
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
        htmlNodes.add(bodyNode);
        HTMLNode mainNode = bodyNode;
        while (!htmlNodes.isEmpty()) {
            HTMLNode htmlNode = htmlNodes.remove(0);
            if (htmlNode.score > mainNode.score) mainNode = htmlNode;
            if (htmlNode.childNodes != null) htmlNodes.addAll(htmlNode.childNodes);
        }
        return (Element) mainNode.node;
    }
}
