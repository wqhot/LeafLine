package com.lf.leafline.utils;

import java.util.ArrayList;
import java.util.List;

import com.lf.leafline.CommentsMessage;
import com.lf.leafline.LeafMessage;

public class DataJiexi {
	public DataJiexi() {
		// TODO 自动生成的构造函数存根
	}

	public List<LeafMessage> SquareJiexi(String str) {
		List<LeafMessage> returnlist = new ArrayList<LeafMessage>();
		int p = -1;
		int q;
		String element;
		int i = 0;

		while (p != str.length() - 1) {
			p = str.indexOf("{", p + 1);
			q = str.indexOf("}", p + 1);
			element = str.substring(p + 1, q);
			LeafMessage lf = new LeafMessage();

			int p1 = -1;
			while (p1 != element.length() - 1) {
				p1 = element.indexOf("@", p1 + 1);
				lf.setid(Integer.parseInt(element.substring(0, p1)));
				element = element.substring(p1 + 1, element.length());
				break;
			}

			p1 = -1;
			while (p1 != element.length() - 1) {
				p1 = element.indexOf("@", p1 + 1);
				lf.setusername(element.substring(0, p1));
				element = element.substring(p1 + 1, element.length());
				break;
			}
			p1 = -1;
			while (p1 != element.length() - 1) {
				p1 = element.indexOf("@", p1 + 1);
				lf.setusertime(element.substring(0, p1));
				element = element.substring(p1 + 1, element.length());
				break;
			}

			lf.setcontext(element);
			returnlist.add(i, lf);
			i++;
			if (q == str.length() - 1) {
				break;
			}
		}

		return returnlist;
	}

	public List<CommentsMessage> MessageJiexi(String str) {
		List<CommentsMessage> returnlist = new ArrayList<CommentsMessage>();
		int p = -1;
		int q;
		String element;
		int i = 0;
		while (p != str.length() - 1) {
			p = str.indexOf("{", p + 1);
			q = str.indexOf("}", p + 1);
			element = str.substring(p + 1, q);
			CommentsMessage com = new CommentsMessage();
			int p1 = -1;
			while (p1 != element.length() - 1) {
				p1 = element.indexOf("@", p1 + 1);
				com.set(element.substring(0, p1), "", "");
				element = element.substring(p1 + 1, element.length());
				break;
			}
			com.setcomment(element);
			returnlist.add(i, com);
			i++;
			if (q == str.length() - 1) {
				break;
			}
		}
		return returnlist;
	}
}
