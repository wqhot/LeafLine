package com.lf.leafline;

public class LeafMessage {
	private String username = "Leafline";
	private int msg_id;
	private String context = "Hello";
	private String usertime = "2015-04-28-11:26";
	private String usertouxiang = "ic_launcher";
	private String main_image = "http://www.sucaitianxia.com/sheji/pic/200710/20071017003358654.jpg";
	private int say_no = 0;
	private int say_yes = 0;
	private int com_num = 0;

	public void setcom_num(int id) {
		com_num = id;
	}

	public int getcom_num() {
		return com_num;
	}

	public void setsay_no(int id) {
		say_no = id;
	}

	public int getsay_no() {
		return say_no;
	}

	public void setsay_yes(int id) {
		say_yes = id;
	}

	public int getsay_yes() {
		return say_yes;
	}

	public void setid(int id) {
		msg_id = id;
	}

	public int getid() {
		return msg_id;
	}

	public String getusername() {
		return username;
	}

	public String getimage() {
		return main_image;
	}

	public String getcontext() {
		return context;
	}

	public String getusertime() {
		return usertime;
	}

	public String getusertouxiang() {
		return usertouxiang;
	}

	public void setusername(String str) {
		username = str;
	}

	public void setcontext(String str) {
		context = str;
	}

	public void setusertime(String str) {
		usertime = str;
	}

	public void setimage(String str) {
		main_image = str;
	}

	public void setusertouxiang(String str) {
		usertouxiang = str;
	}

}
