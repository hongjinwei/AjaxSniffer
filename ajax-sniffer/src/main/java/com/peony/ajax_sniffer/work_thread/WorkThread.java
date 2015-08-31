package com.peony.ajax_sniffer.work_thread;

import com.peony.ajax_sniffer.App;
import com.peony.ajax_sniffer.InfoSource;

public class WorkThread implements Runnable {

	public void run() {
		int count = 0;
		while (true) {
			InfoSource info = App.getInfoSource();
			if (info == null) {
				System.out.println("没有信息源了！");
				break;
			} else {
				if (App.isAjax(info.getUrl())) {
					App.addAjax(info);
					System.out.println("ajax website: " + info.getId() + " " + info.getUrl());
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