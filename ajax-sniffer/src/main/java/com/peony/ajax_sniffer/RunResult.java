package com.peony.ajax_sniffer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

public class RunResult {

	/**
	 * AJAX 版本号，如果在第几版本被排除，那么sql中的is_ajax字段为负几，如果是ajax则为正几 目前版本号为4
	 */
	private static int AJAX_VERSION = 7;

	private static int NOT_AJAX_VERSION = 0 - AJAX_VERSION;

	private static final String FILE_NAME = "/ajax7.txt";

	public static void run() {
		InputStream in = RunResult.class.getResourceAsStream(FILE_NAME);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String tmp;
		ConnectionManager.getInstance().init();
		int count1 = 0;
		int count2 = 0;
		try {
			Connection conn = ConnectionManager.getInstance().getDBConnection();
			String sql = "update wdyq_infosource set is_ajax = ? where id = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			try {
				while ((tmp = br.readLine()) != null) {
					if (tmp.startsWith("ajax website: ")) {
						String content = tmp.substring(14);
						String[] split_content = content.split(" ");
						String id = split_content[0];
						String url = split_content[1];
						System.out.println("    ajax: " + id + " " + url);
						int uid = Integer.parseInt(id);
						ps.setInt(1, AJAX_VERSION);
						ps.setInt(2, uid);
						count1++;
					} else if (tmp.startsWith("not ajax website: ")) {
						String content = tmp.substring(18);
						String[] split_content = content.split(" ");
						String id = split_content[0];
						String url = split_content[1];
						System.out.println("not ajax: " + id + " " + url);
						int uid = Integer.parseInt(id);
						ps.setInt(1, NOT_AJAX_VERSION);
						ps.setInt(2, uid);
						count2++;
					}
					ps.execute();
				}
				System.out.println("total ajax: " + count1 + " not ajax: " + count2);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	public static void init() {
		InputStream in = RunResult.class.getResourceAsStream("/result.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String tmp;
		ConnectionManager.getInstance().init();
		try {
			Connection conn = ConnectionManager.getInstance().getDBConnection();
			String sql = "update wdyq_infosource set is_ajax = ? where id = ?";
			String query = "select *  from wdyq_infosource where is_ajax=2 or is_ajax=1";
			PreparedStatement ps = conn.prepareStatement(query);
			Set<Integer> not_ajax = new HashSet<Integer>();
			try {
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					int id = rs.getInt("id");
					not_ajax.add(id);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			ps = conn.prepareStatement(sql);
			try {
				int count = 0;
				while ((tmp = br.readLine()) != null) {
					if (tmp.startsWith("ajax website: ")) {
						String content = tmp.substring(14);
						String[] split_content = content.split(" ");
						String id = split_content[0];
						String url = split_content[1];
						System.out.println("    ajax: " + id + " " + url);
						int uid = Integer.parseInt(id);
						if (!not_ajax.contains(uid)) {
							ps.setInt(1, AJAX_VERSION);
							ps.setInt(2, uid);
							count++;
							ps.execute();
						}
					}
				}
				System.out.println("total:" + count);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	public static void main(String[] args) {
		// init();
		run();
	}
}
