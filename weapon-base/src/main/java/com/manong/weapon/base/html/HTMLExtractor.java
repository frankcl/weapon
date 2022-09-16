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
    public static Element extractMainElement(String html, String url) {
        if (StringUtils.isEmpty(html)) {
            logger.error("page HTML is empty");
            return null;
        }
        Document document = StringUtils.isEmpty(url) ? Jsoup.parse(html) : Jsoup.parse(html, url);
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
     * 构建HTML内容
     * 以分段元素p构建HTML内容
     *
     * @param mainElement 主体元素
     * @return HTML内容
     */
    public static String buildMainHTML(Element mainElement) {
        if (mainElement == null) return "";
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
            Boolean block = parentNode instanceof Element && ((Element) parentNode).isBlock() &&
                    parentNode.childNodeSize() == 1 ? true : false;
            Element htmlElement = new Element(block ? "p" : "span");
            htmlElement.appendChild(node.clone());
            htmlElements.add(htmlElement);
        } else if (node instanceof Element) {
            Element element = (Element) node;
            String tagName = element.tagName();
            if (!isVisible(element)) return htmlElements;
            if (tagName.equals("br")) {
                Element htmlElement = new Element("br");
                htmlElements.add(htmlElement);
                return htmlElements;
            } else if (tagName.equals("img") || tagName.equals("video")) {
                Element htmlElement = tagName.equals("img") ?
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
                Element htmlElement = new Element("p");
                Element child = children.get(0);
                if (child.tagName().equals("p")) htmlElement.appendChildren(child.childNodes());
                else htmlElement.appendChild(child);
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
     * 构建图片元素
     *
     * @param imageElement 原始图片元素
     * @return 新构建图片元素
     */
    private static Element buildImageElement(Element imageElement) {
        Element htmlElement = new Element("img");
        String sourceURL = imageElement.attr("abs:src");
        if (StringUtils.isEmpty(sourceURL)) return null;
        if (sourceURL.startsWith("//")) sourceURL = String.format("http:%s", sourceURL);
        htmlElement.attr("src", sourceURL);
        return htmlElement;
    }

    /**
     * 构建视频元素
     *
     * @param videoElement 原始视频元素
     * @return 新构建视频元素
     */
    private static Element buildVideoElement(Element videoElement) {
        Element htmlElement = new Element("video");
        String sourceURL = videoElement.attr("abs:src");
        if (StringUtils.isEmpty(sourceURL)) return null;
        if (sourceURL.startsWith("//")) sourceURL = String.format("http:%s", sourceURL);
        htmlElement.attr("src", sourceURL);
        return htmlElement;
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
            if (!isVisible(element) || tagName.equals("br")) return;
            for (Node childNode : element.childNodes()) {
                if (childNode instanceof Comment) continue;
                HTMLNode childHTMLNode = new HTMLNode(childNode);
                computeScore(childHTMLNode);
                accumulateChildNode(htmlNode, childHTMLNode);
            }
            htmlNode.nodeCount++;
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
