package com.lf.leafline;

import java.util.ArrayList;
import java.util.List;

public class CommentsMessage {

	private String userlist = "name";
	private String usercomment = "Good";
	private String usertime = "2015-04-29 15:54";
	private String usertouxiang = "ic_launcher";

	public void setImage(String str) {
		usertouxiang = str;
	}

	public String getImage() {
		return usertouxiang;
	}

	public void set(String str1, String str2, String str3) {
		userlist = str1;
		usercomment = str2;
		usertime = str3;
	}

	public void setusername(String str) {
		userlist = str;
	}

	public void setcomment(String str) {
		usercomment = str;
	}

	public List<String> get() {
		List<String> r = new ArrayList<String>();
		r.add(userlist);
		r.add(usertime);
		r.add(usercomment);
		return r;
	}
}
