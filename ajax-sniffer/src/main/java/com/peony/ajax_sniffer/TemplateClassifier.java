package com.peony.ajax_sniffer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.peony.util.http.HttpQuery;
import com.peony.util.template.URLTemplate;
import com.peony.util.template.URLTemplateBuilder;

public class TemplateClassifier {

	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

	private static final HashMap<String, Set<String>> map = new HashMap<String, Set<String>>();

	public static void cluster(String path, String url) {
		if (map.containsKey(path)) {
			Set<String> set = map.get(path);
			set.add(url);
		} else {
			Set<String> set = new HashSet<String>();
			set.add(url);
			map.put(path, set);
		}
	}

	public static Type classifier(String mainurl) {
		try {
			String html = HttpQuery.getInstance().get(mainurl).asString();
			Document doc = Jsoup.parse(html, mainurl);
			Elements eles = doc.select("a");
			for (Element ele : eles) {
				String uri = ele.absUrl("href");
				String title = ele.text();
				if (StringUtils.isEmpty(title) || StringUtils.isEmpty(uri)) {
					continue;
				}
				URLTemplate temp = URLTemplateBuilder.build(uri);
				String path = temp.getPaths().toString();
				cluster(path, uri);
				return Type.AJAX;
			}
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			return Type.ERROR;
		}
		return Type.UNSURE;
	}
}
