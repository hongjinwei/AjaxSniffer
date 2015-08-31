package com.peony.ajax_sniffer;

import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.peony.util.StringUtils;
import com.peony.util.TimerUtils;
import com.peony.util.http.HttpQuery;

public class WebDriverClassifier {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverClassifier.class);

	public static boolean test(String url) {
		System.setProperty("webdriver.firefox.bin", "C:/Program Files (x86)/Mozilla Firefox/firefox.exe");
		WebDriver driver = new FirefoxDriver();
		// WebDriver driver = new HtmlUnitDriver(true);
		Set<String> rawUrls = new HashSet<String>();
		try {
			String html = HttpQuery.getInstance().get(url).asString();
			rawUrls = Utils.getRawUrlsAndTitle(html);
			System.out.println(rawUrls);
			driver.get(url);
			TimerUtils.delayForSeconds(10);
			String page = driver.getPageSource();
			Set<Element> all = Utils.getAllElementsByTag(page, "a");

			int count = 0;
			for (Element e : all) {
				String eurl = e.attr("href");
				String etitle = e.text();
				if (!StringUtils.isEmpty(eurl) && !StringUtils.isEmpty(etitle) && etitle.length() > 4 && !rawUrls.contains(eurl)
						&& !rawUrls.contains(etitle) && Utils.isLegalUrl(eurl)) {
					System.out.println(etitle + " " + eurl);
					count++;
				}
			}
			if (count >= 3) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return false;
		} finally {
			try {
				driver.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean isAjax(String url) {
		WebDriver driver = new FirefoxDriver();
		Set<String> rawUrls = new HashSet<String>();
		try {
			String html = HttpQuery.getInstance().get(url).asString();
			rawUrls = Utils.getRawUrlsAndTitle(html);
			driver.get(url);
			TimerUtils.delayForSeconds(10);
			String page = driver.getPageSource();
			Set<Element> all = Utils.getAllElementsByTag(page, "a");

			int count = 0;
			for (Element e : all) {
				String eurl = e.attr("href");
				String etitle = e.text();
				if (!StringUtils.isEmpty(eurl) && !StringUtils.isEmpty(etitle) && etitle.length() > 4 && !rawUrls.contains(eurl)
						&& !rawUrls.contains(etitle) && Utils.isLegalUrl(eurl)) {
					count++;
				}
			}
			if (count >= 3) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return false;
		} finally {
			try {
				driver.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static void testWebsite(String url) throws Exception {
		String html = HttpQuery.getInstance().get(url).asString();
		Element body = Jsoup.parse(html, url).body();
		Elements elements = body.select("a");
		for (Element e : elements) {
			String href = e.absUrl("href");
			String text = e.text();
			if (StringUtils.isEmpty(href) || StringUtils.isEmpty(text) || text.length() <= 5) {
				continue;
			}
			System.out.println(text);
		}
	}

	public static void main(String[] args) throws Exception{
		String url = "http://toutiao.com/news_society/";
		String url2 = "http://www.qstheory.cn/qsllqk/qunzhong/more.htm";
		String problem_url = "http://news.cntrades.com/search-htm-kw-%BA%B8%B9%DC.html";
		String p2 = "http://www.xinhuanet.com/comments/rd.htm";
		// System.out.println(test(p2));
		// try {
		// String html = HttpQuery.getInstance().get(p2).asString();
		// System.out.println(html);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		// try{
		// System.out.println(HttpQuery.getInstance().get(p2).asString());
		// }catch(Exception e){
		//
		// }
		testWebsite("http://www.xinhuanet.com/comments/rd.htm");
	}
}
