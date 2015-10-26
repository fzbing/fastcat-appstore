package com.fastspider.fastcat.bean;

public class Article {
	public String id;
	public String title;// ����
	public String content;// �б�����
	public String pcount;// ��ϸ����
	public String created;// ����
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPcount() {
		return pcount;
	}

	public void setPcount(String pcount) {
		this.pcount = pcount;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

}
