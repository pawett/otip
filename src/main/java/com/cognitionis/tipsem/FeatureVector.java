package com.cognitionis.tipsem;

public class FeatureVector {

private String token;
private String lemma;
private String PoS;
private String dynWindow;
private String PPHeadPrep;
private String syntHierarchy;
private String top4Hypers;
private String role;
private String roleAndLemma;

public FeatureVector(String[] pipes_desc_arr)
{
	
}
public String getToken() {
	return token;
}
public void setToken(String token) {
	this.token = token;
}
public String getLemma() {
	return lemma;
}
public void setLemma(String lemma) {
	this.lemma = lemma;
}
public String getPoS() {
	return PoS;
}
public void setPoS(String poS) {
	PoS = poS;
}
public String getDynWindow() {
	return dynWindow;
}
public void setDynWindow(String dynWindow) {
	this.dynWindow = dynWindow;
}
public String getSyntHierarchy() {
	return syntHierarchy;
}
public void setSyntHierarchy(String syntHierarchy) {
	this.syntHierarchy = syntHierarchy;
}
public String getTop4Hypers() {
	return top4Hypers;
}
public void setTop4Hypers(String top4Hypers) {
	this.top4Hypers = top4Hypers;
}
public String getRole() {
	return role;
}
public void setRole(String role) {
	this.role = role;
}
public String getRoleAndLemma() {
	return roleAndLemma;
}
public void setRoleAndLemma(String roleAndLemma) {
	this.roleAndLemma = roleAndLemma;
}

}
