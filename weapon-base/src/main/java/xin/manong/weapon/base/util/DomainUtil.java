package xin.manong.weapon.base.util;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 域名工具
 *
 * @author frankcl
 * @date 2022-08-08 10:14:44
 */
public class DomainUtil {

    private final static Set<String> RESERVED_DOMAINS = new HashSet<String>() {
        {
            add("com");
            add("org");
            add("edu");
            add("gov");
            add("net");
        }
    };
    public final static Map<String, String> INTERNATIONAL_TOP_DOMAINS = new HashMap<String, String>() {
        {
            put("com", "商业机构");
            put("edu", "教育机构");
            put("gov", "政府部门");
            put("int", "国际组织");
            put("mil", "美国军事部门");
            put("net", "网络组织");
            put("org", "非盈利组织");
            put("biz", "商业");
            put("info", "网络信息服务组织");
            put("pro", "会计、律师和医生");
            put("name", "个人");
            put("museum", "博物馆");
            put("coop", "商业合作团体");
            put("aero", "航空工业");
            put("xxx", "成人、色情网站");
            put("idv", "个人");
            put("xin", "诚信机构或个人");
            put("top", "高端、顶级");
            put("xyz", "无限制，企业或个人");
            put("vip", "重要尊贵");
            put("win", "");
            put("red", "红色、吉祥、热情、勤奋");
            put("wang", "华人域名");
            put("mobi", "手机移动");
            put("travel", "旅游网站");
            put("club", "俱乐部等在线社区");
            put("post", "邮政");
            put("rec", "娱乐机构");
            put("asia", "亚洲机构");
            put("art", "艺术文化领域服务");
            put("firm", "公司企业");
            put("nom", "个人");
            put("store", "销售类公司企业");
            put("web", "从事WWW活动的机构");
            put("fans", "粉丝");
            put("ren", "人");
            put("city", "城市");
        }
    };
    public final static Map<String, String> COUNTRY_TOP_DOMAINS = new HashMap<String, String>() {
        {
            put("ac", "亚森松岛");
            put("ad", "安道尔");
            put("ae", "阿拉伯联合酋长国");
            put("af", "阿富汗");
            put("ag", "安提瓜和巴布达");
            put("ai", "安圭拉");
            put("al", "阿尔巴尼亚");
            put("am", "亚美尼亚");
            put("an", "荷属安地列斯群岛");
            put("ao", "安哥拉");
            put("aq", "南极洲");
            put("ar", "阿根廷");
            put("as", "美属萨摩亚");
            put("at", "奥地利");
            put("au", "澳大利亚");
            put("aw", "阿鲁巴");
            put("az", "阿塞拜疆");
            put("ba", "波斯尼亚和黑塞哥维那");
            put("bb", "巴巴多斯");
            put("bd", "孟加拉国");
            put("be", "比利时");
            put("bf", "布基纳法索");
            put("bg", "保加利亚");
            put("bh", "巴林");
            put("bi", "布隆迪");
            put("bj", "贝宁");
            put("bm", "百慕大");
            put("bn", "文莱");
            put("bo", "玻利维亚");
            put("br", "巴西");
            put("bs", "巴哈马");
            put("bt", "不丹");
            put("bv", "布维岛");
            put("bw", "博茨瓦纳");
            put("by", "白俄罗斯");
            put("bz", "伯利兹");
            put("ca", "加拿大");
            put("cc", "可可群岛");
            put("cd", "刚果民主共和国");
            put("cf", "中非共和国");
            put("cg", "刚果");
            put("ch", "瑞士");
            put("ci", "科特迪瓦");
            put("ck", "库克群岛");
            put("cl", "智利");
            put("cm", "喀麦隆");
            put("cn", "中国内地");
            put("co", "哥伦比亚");
            put("cr", "哥斯达黎加");
            put("cu", "古巴");
            put("cv", "佛得角");
            put("cx", "圣诞岛");
            put("cy", "塞浦路斯");
            put("cz", "捷克共和国");
            put("de", "德国");
            put("dj", "吉布提");
            put("dk", "丹麦");
            put("dm", "多米尼克");
            put("do", "多米尼加共和国");
            put("dz", "阿尔及利亚");
            put("ec", "厄瓜多尔");
            put("ee", "爱沙尼亚");
            put("eg", "埃及");
            put("eh", "西撒哈拉");
            put("er", "厄立特里亚");
            put("es", "西班牙");
            put("et", "埃塞俄比亚");
            put("eu", "欧洲联盟");
            put("fi", "芬兰");
            put("fj", "斐济");
            put("fk", "福克兰群岛");
            put("fm", "密克罗尼西亚联邦");
            put("fo", "法罗群岛");
            put("fr", "法国");
            put("ga", "加蓬");
            put("gd", "格林纳达");
            put("ge", "格鲁吉亚");
            put("gf", "法属圭亚那");
            put("gg", "格恩西岛");
            put("gh", "加纳");
            put("gi", "直布罗陀");
            put("gl", "格陵兰");
            put("gm", "冈比亚");
            put("gn", "几内亚");
            put("gp", "瓜德罗普");
            put("gq", "赤道几内亚");
            put("gr", "希腊");
            put("gs", "南乔治亚岛和南桑德韦奇岛");
            put("gt", "危地马拉");
            put("gu", "关岛");
            put("gw", "几内亚比绍");
            put("gy", "圭亚那");
            put("hk", "中国香港");
            put("hm", "赫德和麦克唐纳群岛");
            put("hn", "洪都拉斯");
            put("hr", "克罗地亚");
            put("ht", "海地");
            put("hu", "匈牙利");
            put("id", "印度尼西亚");
            put("ie", "爱尔兰");
            put("il", "以色列");
            put("im", "马恩岛");
            put("in", "印度");
            put("io", "英属印度洋地区");
            put("iq", "伊拉克");
            put("ir", "伊朗");
            put("is", "冰岛");
            put("it", "意大利");
            put("je", "泽西岛");
            put("jm", "牙买加");
            put("jo", "约旦");
            put("jp", "日本");
            put("ke", "肯尼亚");
            put("kg", "吉尔吉斯斯坦");
            put("kh", "柬埔寨");
            put("ki", "基里巴斯");
            put("km", "科摩罗");
            put("kn", "圣基茨和尼维斯");
            put("kp", "朝鲜");
            put("kr", "韩国");
            put("kw", "科威特");
            put("ky", "开曼群岛");
            put("kz", "哈萨克斯坦");
            put("la", "老挝");
            put("lb", "黎巴嫩");
            put("lc", "圣卢西亚");
            put("li", "列支敦士登");
            put("lk", "斯里兰卡");
            put("lr", "利比里亚");
            put("ls", "莱索托");
            put("lt", "立陶宛");
            put("lu", "卢森堡");
            put("lv", "拉脱维亚");
            put("ly", "利比亚");
            put("ma", "摩洛哥");
            put("mc", "摩纳哥");
            put("md", "摩尔多瓦");
            put("mg", "马达加斯加");
            put("mh", "马绍尔群岛");
            put("mk", "马其顿");
            put("ml", "马里");
            put("mm", "缅甸");
            put("mn", "蒙古");
            put("mo", "中国澳门");
            put("mp", "北马里亚纳群岛");
            put("mq", "马提尼克岛");
            put("mr", "毛里塔尼亚");
            put("ms", "蒙特塞拉特岛");
            put("mt", "马耳他");
            put("mu", "毛里求斯");
            put("mv", "马尔代夫");
            put("mw", "马拉维");
            put("mx", "墨西哥");
            put("my", "马来西亚");
            put("mz", "莫桑比克");
            put("na", "纳米比亚");
            put("nc", "新喀里多尼亚");
            put("ne", "尼日尔");
            put("nf", "诺福克岛");
            put("ng", "尼日利亚");
            put("ni", "尼加拉瓜");
            put("nl", "荷兰");
            put("no", "挪威");
            put("np", "尼泊尔");
            put("nr", "瑙鲁");
            put("nu", "纽埃岛");
            put("nz", "新西兰");
            put("om", "阿曼");
            put("pa", "巴拿马");
            put("pe", "秘鲁");
            put("pf", "法属波利尼西亚");
            put("pg", "巴布亚新几内亚");
            put("ph", "菲律宾");
            put("pk", "巴基斯坦");
            put("pl", "波兰");
            put("pm", "圣皮埃尔岛及密客隆岛");
            put("pn", "皮特凯恩群岛");
            put("pr", "波多黎各");
            put("ps", "巴勒斯坦");
            put("pt", "葡萄牙");
            put("pw", "帕劳");
            put("py", "巴拉圭");
            put("qa", "卡塔尔");
            put("re", "留尼汪");
            put("ro", "罗马尼亚");
            put("ru", "俄罗斯");
            put("rw", "卢旺达");
            put("sa", "沙特阿拉伯");
            put("sb", "所罗门群岛");
            put("sc", "塞舌尔");
            put("sd", "苏丹");
            put("se", "瑞典");
            put("sg", "新加坡");
            put("sh", "圣赫勒拿岛");
            put("si", "斯洛文尼亚");
            put("sj", "斯瓦尔巴岛和扬马延岛");
            put("sk", "斯洛伐克");
            put("sl", "塞拉利昂");
            put("sm", "圣马力诺");
            put("sn", "塞内加尔");
            put("so", "索马里");
            put("sr", "苏里南");
            put("st", "圣多美和普林西比");
            put("sv", "萨尔瓦多");
            put("sy", "叙利亚");
            put("sz", "斯威士兰");
            put("tc", "特克斯和凯科斯群岛");
            put("td", "乍得");
            put("tf", "法属南部领土");
            put("tg", "多哥");
            put("th", "泰国");
            put("tj", "塔吉克斯坦");
            put("tk", "托克劳");
            put("tl", "东帝汶");
            put("tm", "土库曼斯坦");
            put("tn", "突尼斯");
            put("to", "汤加");
            put("tp", "东帝汶");
            put("tr", "土耳其");
            put("tt", "特立尼达和多巴哥");
            put("tv", "图瓦卢");
            put("tw", "中国台湾");
            put("tz", "坦桑尼亚");
            put("ua", "乌克兰");
            put("ug", "乌干达");
            put("uk", "英国");
            put("um", "美国本土外小岛屿");
            put("us", "美国");
            put("uy", "乌拉圭");
            put("uz", "乌兹别克斯坦");
            put("va", "梵蒂冈");
            put("vc", "圣文森特和格林纳丁斯");
            put("ve", "委内瑞拉");
            put("vg", "英属维尔京群岛");
            put("vi", "美属维尔京群岛");
            put("vn", "越南");
            put("vu", "瓦努阿图");
            put("wf", "瓦利斯和富图纳群岛");
            put("ws", "萨摩亚");
            put("ye", "也门");
            put("yt", "马约特岛");
            put("yu", "塞尔维亚和黑山");
            put("yr", "耶纽");
            put("za", "南非");
            put("zm", "赞比亚");
            put("zw", "津巴布韦");
        }
    };
    public final static Map<String, String> CN_REGION_DOMAINS = new HashMap<String, String>() {
        {
            put("ac.cn", "科学院系统");
            put("ah.cn", "安徽省");
            put("bj.cn", "北京市");
            put("com.cn", "商业系统");
            put("cq.cn", "重庆市");
            put("fj.cn", "福建省");
            put("gd.cn", "广东省");
            put("gov.cn", "政府部门");
            put("gs.cn", "甘肃省");
            put("gx.cn", "广西自治区");
            put("gz.cn", "贵州省");
            put("ha.cn", "河南省");
            put("hb.cn", "湖北省");
            put("he.cn", "河北省");
            put("hi.cn", "海南省");
            put("hk.cn", "中国香港");
            put("hl.cn", "黑龙江省");
            put("hn.cn", "湖南省");
            put("jl.cn", "吉林省");
            put("js.cn", "江苏省");
            put("jx.cn", "江西省");
            put("ln.cn", "辽宁省");
            put("mo.cn", "中国澳门");
            put("net.cn", "邮电部门");
            put("nm.cn", "内蒙古自治区");
            put("nx.cn", "宁夏回族自治区");
            put("org.cn", "社会组织");
        }
    };

    /**
     * 根据站点获取一级域名
     *
     * @param host 站点
     * @return 一级域名
     */
    public static String getDomain(String host) {
        if (StringUtils.isEmpty(host)) return host;
        boolean countryDomain = false;
        StringBuilder domain = new StringBuilder();
        String[] segments = host.split("\\.");
        for (int i = segments.length - 1; i >= 0 && i >= segments.length - 3; i--) {
            String segment = segments[i];
            if (i == segments.length - 1) {
                if (INTERNATIONAL_TOP_DOMAINS.containsKey(segment) || COUNTRY_TOP_DOMAINS.containsKey(segment)) {
                    if (COUNTRY_TOP_DOMAINS.containsKey(segment)) countryDomain = true;
                    domain.append(segment);
                    continue;
                }
                break;
            } else if (i == segments.length - 2) {
                String str = String.format("%s.%s", segment, domain);
                domain.insert(0, ".").insert(0, segment);
                if ((countryDomain && RESERVED_DOMAINS.contains(segment)) ||
                        CN_REGION_DOMAINS.containsKey(str)) continue;
                break;
            }
            if (!segment.equals("www")) domain.insert(0, ".").insert(0, segment);
        }
        return domain.isEmpty() ? host : domain.toString();
    }
}
