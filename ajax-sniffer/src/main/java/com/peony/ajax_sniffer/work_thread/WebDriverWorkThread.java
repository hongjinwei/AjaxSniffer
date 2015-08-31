package com.peony.ajax_sniffer.work_thread;

import java.sql.Timestamp;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.peony.ajax_sniffer.App;
import com.peony.ajax_sniffer.InfoSource;
import com.peony.ajax_sniffer.Utils;
import com.peony.ajax_sniffer.WebDriverClassifier;

public class WebDriverWorkThread implements Runnable {

	private static final String log_file = "C:/Users/BAO/Desktop/workfile/log.txt";

	public void run() {
		int count = 0;
		while (true) {
			InfoSource info = App.getInfoSource();
			if (info == null) {
				System.out.println("没有信息源了！");
				break;
			} else {
				System.out.println(new Timestamp(System.currentTimeMillis()));
				if (WebDriverClassifier.isAjax(info.getUrl())) {
					App.addAjax(info);
					String content = "ajax website: " + info.getId() + " " + info.getUrl() + " " + info.getWebsite();
					System.out.println(content);
					try {
						Utils.appendContentToFile(log_file, content);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					String content = "not ajax website: " + info.getId() + " " + info.getUrl() + " " + info.getWebsite();
					System.out.println(content);
					try {
						Utils.appendContentToFile(log_file, content);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
			count++;
			if (count % 10 == 0) {
				System.out.println("还有 " + App.getInfosRemain() + "个网站");
				System.out.println("找到ajax网站 ：" + App.getAjaxSize() + "个");
			}
		}
	}

}
