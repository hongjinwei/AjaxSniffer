package com.peony.ajax_sniffer;

public class InfoSource {

	private int id;
	private String url;
	private String website;

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public InfoSource(int id, String url, String webiste) {
		super();
		this.id = id;
		this.url = url;
		this.website = webiste;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
