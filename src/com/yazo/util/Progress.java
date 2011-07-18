package com.yazo.util;
/**
 * 
 * @author Administrator
 *
 */
public class Progress {
	
	/** 专区ID*/
	private String catalogId;
	/**内容ID*/
	private String contentId;
	/**章节ID*/
	private String chapterId;
	/**产品ID*/
	private String productId;
	public String getCatalogId() {
		return catalogId;
	}
	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}
	public String getContentId() {
		return contentId;
	}
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	public String getChapterId() {
		return chapterId;
	}
	public void setChapterId(String chapterId) {
		this.chapterId = chapterId;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String toString() {
		return "Progress [catalogId=" + catalogId + ", contentId=" + contentId
				+ ", chapterId=" + chapterId + ", productId=" + productId + "]";
	}

}
