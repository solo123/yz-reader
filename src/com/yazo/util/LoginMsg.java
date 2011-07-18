package com.yazo.util;

public class LoginMsg {
	private String url;
	private String agent;
	private String password;
	private boolean debug;
	private String logServer;
	private String channel;
	private String powerSwitch;
	private String number;
	private String type;
	private String message;
	private String register;
	private String authenticate;
	private String getClientWelcomeInfo;
	private String getCatalogInfo;
	private String getContentInfo;
	private String getChapterInfo;
	private String getCatalogProductInfo;
	private String subscribeCatalog;
	private String getContentProductInfo;
	private String subscribeContent;
	private String getUserId;
	
	
	public LoginMsg(String url, String agent, String password, boolean debug,
			String logServer, String channel, String type, String message,
			String register, String authenticate, String getClientWelcomeInfo,
			String getCatalogInfo, String getContentInfo,
			String getChapterInfo, String getCatalogProductInfo,
			String subscribeCatalog, String getContentProductInfo,
			String subscribeContent, String getUserId) {
		this.url = url;
		this.agent = agent;
		this.password = password;
		this.debug = debug;
		this.logServer = logServer;
		this.channel = channel;
		this.type = type;
		this.message = message;
		this.register = register;
		this.authenticate = authenticate;
		this.getClientWelcomeInfo = getClientWelcomeInfo;
		this.getCatalogInfo = getCatalogInfo;
		this.getContentInfo = getContentInfo;
		this.getChapterInfo = getChapterInfo;
		this.getCatalogProductInfo = getCatalogProductInfo;
		this.subscribeCatalog = subscribeCatalog;
		this.getContentProductInfo = getContentProductInfo;
		this.subscribeContent = subscribeContent;
		this.getUserId = getUserId;
	}
	public LoginMsg() {
		super();
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getAgent() {
		return agent;
	}
	public void setAgent(String agent) {
		this.agent = agent;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isDebug() {
		return debug;
	}
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	public String getLogServer() {
		return logServer;
	}
	public void setLogServer(String logServer) {
		this.logServer = logServer;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getRegister() {
		return register;
	}
	public void setRegister(String register) {
		this.register = register;
	}
	public String getAuthenticate() {
		return authenticate;
	}
	public void setAuthenticate(String authenticate) {
		this.authenticate = authenticate;
	}
	public String getGetClientWelcomeInfo() {
		return getClientWelcomeInfo;
	}
	public void setGetClientWelcomeInfo(String getClientWelcomeInfo) {
		this.getClientWelcomeInfo = getClientWelcomeInfo;
	}
	public String getGetCatalogInfo() {
		return getCatalogInfo;
	}
	public void setGetCatalogInfo(String getCatalogInfo) {
		this.getCatalogInfo = getCatalogInfo;
	}
	public String getGetContentInfo() {
		return getContentInfo;
	}
	public void setGetContentInfo(String getContentInfo) {
		this.getContentInfo = getContentInfo;
	}
	public String getGetChapterInfo() {
		return getChapterInfo;
	}
	public void setGetChapterInfo(String getChapterInfo) {
		this.getChapterInfo = getChapterInfo;
	}
	public String getGetCatalogProductInfo() {
		return getCatalogProductInfo;
	}
	public void setGetCatalogProductInfo(String getCatalogProductInfo) {
		this.getCatalogProductInfo = getCatalogProductInfo;
	}
	public String getSubscribeCatalog() {
		return subscribeCatalog;
	}
	public void setSubscribeCatalog(String subscribeCatalog) {
		this.subscribeCatalog = subscribeCatalog;
	}
	public String getGetContentProductInfo() {
		return getContentProductInfo;
	}
	public void setGetContentProductInfo(String getContentProductInfo) {
		this.getContentProductInfo = getContentProductInfo;
	}
	public String getSubscribeContent() {
		return subscribeContent;
	}
	public void setSubscribeContent(String subscribeContent) {
		this.subscribeContent = subscribeContent;
	}
	public String getGetUserId() {
		return getUserId;
	}
	public void setGetUserId(String getUserId) {
		this.getUserId = getUserId;
	}
	
	
	public String getPowerSwitch() {
		return powerSwitch;
	}
	public void setPowerSwitch(String powerSwitch) {
		this.powerSwitch = powerSwitch;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String toString() {
		return "LoginMsg [url=" + url + ", agent=" + agent + ", password="
				+ password + ", debug=" + debug + ", logServer=" + logServer
				+ ", channel=" + channel + ", powerSwitch=" + powerSwitch
				+ ", number=" + number + ", type=" + type + ", message="
				+ message + ", register=" + register + ", authenticate="
				+ authenticate + ", getClientWelcomeInfo="
				+ getClientWelcomeInfo + ", getCatalogInfo=" + getCatalogInfo
				+ ", getContentInfo=" + getContentInfo + ", getChapterInfo="
				+ getChapterInfo + ", getCatalogProductInfo="
				+ getCatalogProductInfo + ", subscribeCatalog="
				+ subscribeCatalog + ", getContentProductInfo="
				+ getContentProductInfo + ", subscribeContent="
				+ subscribeContent + ", getUserId=" + getUserId + "]";
	}
	
	
}
