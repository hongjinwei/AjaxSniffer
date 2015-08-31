package com.peony.ajax_sniffer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.peony.ajax_sniffer.work_thread.WebDriverWorkThread;
import com.peony.ajax_sniffer.work_thread.WorkThread;
import com.peony.util.TimerUtils;
import com.peony.util.contex.ContexAutoBuilder;
import com.peony.util.html.SourceLink;

public class App {

	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

	public static ContexAutoBuilder contexAutoBuilder = new ContexAutoBuilder();

	private static List<InfoSource> infos = new ArrayList<InfoSource>();

	private static List<InfoSource> ajaxs = new ArrayList<InfoSource>();

	private static final int AJAX_VERSION = 5;

	private static final String SELECT_SQL = "select * from wdyq_infosource where is_ajax=" + AJAX_VERSION + " or is_ajax = 6 and type <> 5 ";

	private static final String UPDATE_SQL = "update wdyq_infosource set is_ajax = " + (AJAX_VERSION + 1) + " where id = ?";

	synchronized public static InfoSource getInfoSource() {
		if (infos.size() <= 0) {
			return null;
		}
		InfoSource ans = infos.get(0);
		infos.remove(0);
		return ans;
	}

	synchronized public static int getInfosRemain() {
		return infos.size();
	}

	synchronized public static int getAjaxSize() {
		return ajaxs.size();
	}

	synchronized public static void addAjax(InfoSource info) {
		ajaxs.add(info);
	}

	public static void printMap(Map<String, Set<SourceLink>> map) {
		for (String key : map.keySet()) {
			System.out.print(key + " ");
			for (SourceLink link : map.get(key)) {
				System.out.println("<" + link.getUrl() + ", " + link.getText() + ">");
			}
		}
	}

	public static int fetchInfoSource() throws SQLException {
		Connection conn = ConnectionManager.getInstance().getDBConnection();
		int count = 0;
		try {
			PreparedStatement ps = conn.prepareStatement(SELECT_SQL);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				count++;
				int id = rs.getInt("id");
				String url = rs.getString("url");
				String website = rs.getString("website");
				infos.add(new InfoSource(id, url, website));
			}
			System.out.println("总共信息源有 " + count + "条");

		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			conn.close();
		}
		return count;
	}

	public static boolean isAjax(String url) {
		try {
			Map<String, Set<SourceLink>> map = contexAutoBuilder.build(url);
			return map.isEmpty();
		} catch (Exception e) {
			return false;
		}
	}

	public static void updateAjaxBatch(List<InfoSource> subList, PreparedStatement ps) throws SQLException {
		for (InfoSource info : subList) {
			try {
				ps.setInt(1, info.getId());
				ps.addBatch();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// int n = ps.executeUpdate();
		ps.executeBatch();
	}

	public static void updateAjaxAll(List<InfoSource> allAjax) throws SQLException {
		Connection conn = ConnectionManager.getInstance().getDBConnection();
		if (allAjax.size() <= 0) {
			System.out.println("没有ajax！");
		}
		try {
			PreparedStatement ps = conn.prepareStatement(UPDATE_SQL);
			int step = 100;
			int all = allAjax.size() / step;
			for (int i = 0; i <= all; i++) {
				if (i == allAjax.size() / step) {
					updateAjaxBatch(allAjax.subList(i * step, allAjax.size()), ps);
				} else {
					updateAjaxBatch(allAjax.subList(i * step, i * step + step), ps);
				}
				if (i % 10 == 0) {
					System.out.println("插入数据库完成" + i * 100.0 / all + "%");
				}
			}
			System.out.println("插入数据库完成 100% ！");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

	public static void start() {
		ConnectionManager.getInstance().init();
		try {
			int number = fetchInfoSource();
			System.out.println("完成查询！");

			ExecutorService threadPool = Executors.newFixedThreadPool(10);
			for (int i = 0; i < 5; i++) {
				threadPool.submit(new WorkThread());
			}

			while (true) {
				TimerUtils.delayForMinutes(1);
				if (getInfosRemain() == 0) {
					updateAjaxAll(ajaxs);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void startWebDriver() {
		ConnectionManager.getInstance().init();
		try {
			int number = fetchInfoSource();
			System.out.println("完成查询！");
			ExecutorService threadPool = Executors.newFixedThreadPool(10);
			for (int i = 0; i < 10; i++) {
				threadPool.submit(new WebDriverWorkThread());
			}

			// while (true) {
			// TimerUtils.delayForMinutes(1);
			// if (getInfosRemain() == 0) {
			// TimerUtils.delayForMinutes(1);
			// updateAjaxAll(ajaxs);
			// break;
			// }
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// ConnectionManager.getInstance().init();
		// System.out.println("Hello World!");
		// System.out.println(System.getProperty("java.version"));
		// String wangyi = "http://news.163.com/localnews/";
		// String toutiao = "http://toutiao.com/news_society/";
		// String kankan = "http://js.kankancity.com/tags/861/";
		// System.out.println(isAjax(wangyi));
		// System.out.println(isAjax(toutiao));
		// System.out.println(isAjax(kankan));
		// infos.add(new InfoSource(857, null));
		// try {
		// updateAjaxAll(infos);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		System.setProperty("webdriver.firefox.bin", "C:/Program Files (x86)/Mozilla Firefox/firefox.exe");

		startWebDriver();
	}
}
