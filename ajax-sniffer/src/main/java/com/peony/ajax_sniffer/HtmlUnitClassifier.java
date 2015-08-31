package com.peony.ajax_sniffer;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.peony.util.StringUtils;

public class HtmlUnitClassifier {

	private static final Logger LOGGER = LoggerFactory.getLogger(HtmlUnitClassifier.class);

	public static Set<String> getRawUrls(String url) {
		Set<String> urlSet = new HashSet<String>();
		try {
			Document doc = Jsoup.parse(new URL(url), 10000);
			Elements eles = doc.getElementsByTag("a");
			for (Element ele : eles) {
				String uri = ele.attr("href");
				if (!StringUtils.isEmpty(uri)) {
					urlSet.add(uri);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return urlSet;
	}

	public static boolean isUrl(String href, String mainUrl) {
		try {
			String url = Utils.absUrl(href, mainUrl);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static Type classifier(String requestUrl) {
		WebClient webclient = new WebClient();
		webclient.setJavaScriptEnabled(true);
		webclient.setCssEnabled(false);
		webclient.setTimeout(Integer.MAX_VALUE);
		webclient.setRedirectEnabled(true);
		webclient.setThrowExceptionOnScriptError(true);
		webclient.setTimeout(10000);
		webclient.setJavaScriptTimeout(100l);
		int count = 0;
		try {
			Set<String> rawUrls = getRawUrls(requestUrl);
			HtmlPage htmlPage = webclient.getPage(requestUrl);
			webclient.waitForBackgroundJavaScript(10000);
			DomNodeList<HtmlElement> list = htmlPage.getElementsByTagName("a");
			for (HtmlElement ele : list) {
				String url = ele.getAttribute("href");
				String title = ele.asText();
				if (!rawUrls.contains(url)) {
					url = Utils.absUrl(url, requestUrl);
					if (!StringUtils.isEmpty(url)) {
						LOGGER.debug(title + " " + url);
						count++;
					}
				}
			}
			LOGGER.debug(htmlPage.asXml());
			return (count > 3) ? Type.AJAX : Type.UNSURE;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return Type.AJAX;
	}

	public static void main(String[] args) {
		System.out.println("Hello World!");
		System.out.println(System.getProperty("java.version"));
		String wangyi = "http://news.163.com/localnews/";
		String toutiao = "http://toutiao.com/news_society/";
		String kankan = "http://js.kankancity.com/tags/861/";
		String url = "http://bbs.yingchengnet.com/forum-21-1.html?mobile=no";
		System.out.println(classifier(url));
	}
}
