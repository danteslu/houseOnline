package com.houseOnline.common.query;

public class Page {
	private int pageNum;
	private int nPerPage;
	
	public Page(int pageNum, int nPerPage) {
		this.pageNum = pageNum;
		this.nPerPage = nPerPage;
	}
	
	public int getSkip() {
		return pageNum * nPerPage;
	}

	public int getnPerPage() {
		return nPerPage;
	}
}
