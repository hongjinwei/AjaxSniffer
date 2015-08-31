package com.peony.ajax_sniffer;

import java.io.FileWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.peony.util.StringUtils;
import com.peony.util.http.HttpQuery;

public class Utils {

	private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

	public static String absUrl(String baseUrl, String relUrl) {
		URL base;
		try {
			try {
				base = new URL(baseUrl);
			} catch (MalformedURLException e) {
				URL abs = new URL(relUrl);
				return abs.toExternalForm();
			}
			if (relUrl.startsWith("?"))
				relUrl = base.getPath() + relUrl;
			URL abs = new URL(base, relUrl);
			return abs.toExternalForm();
		} catch (MalformedURLException e) {
			return "";
		}
	}

	public static Set<String> getRawUrlsAndTitle(String html) throws Exception {
		Set<String> urlSet = new HashSet<String>();
		Document doc = Jsoup.parse(html);
		Elements eles = doc.getElementsByTag("a");
		for (Element ele : eles) {
			String uri = ele.attr("href");
			String title = ele.text();
			if (!StringUtils.isEmpty(uri)) {
				urlSet.add(uri);
			}
			if (!StringUtils.isEmpty(title)) {
				urlSet.add(title);
			}
		}
		return urlSet;
	}

	public static Set<Element> getAllElementsByTag(String html, String tag) {
		Set<Element> set = new HashSet<Element>();
		try {
			Document doc = Jsoup.parse(html);
			Elements eles = doc.getElementsByTag(tag);
			for (Element ele : eles) {
				set.add(ele);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return set;
	}

	public static boolean isLegalUrl(String url) {
		if (url.equals("#")) {
			return false;
		} else if (url.toLowerCase().startsWith("javascript")) {
			return false;
		}
		return true;
	}

	public static void appendContentToFile(String filename, String content) throws Exception {
		if (!content.endsWith("\n")) {
			content = content + "\n";
			// System.out.println(content);
		}
		FileWriter fw = new FileWriter(filename, true);
		fw.write(content);
		fw.close();
	}

	public static void main(String[] args) {
		try {
			appendContentToFile("C:\\Users\\BAO\\Desktop\\workfile\\test.log", "world");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
