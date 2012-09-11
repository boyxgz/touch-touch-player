package com.surelution;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

/**
 * 
 * @author <a href="mailto:guangzong.syu@gmail.com">Guangzong Hsu</a>
 * 
 */
public class Fragment {

	private Fragment parent;
	public Fragment getParent() {
		return parent;
	}

	private List<Fragment> children;
	public List<Fragment> getChildren() {
		return children;
	}

	public final String title;
	public final String path;
	public final String description;
	public final Integer start;
	public final Integer end;
	
	public Fragment(String title, String path, String description, Integer start, Integer end) {
		this.title = title;
		this.path = path;
		this.description = description;
		this.start = start;
		this.end = end;
	}
	
	public void addChild(Fragment fragment) {
		fragment.parent = this;
		if(children == null) {
			children = new ArrayList<Fragment>();
		}
		children.add(fragment);
	}
	
	public String getMediaFile() {
		if(path != null) {
			return path;
		} else {
			return parent.getMediaFile();
		}
	}

	public static List<Fragment> load(String xmlPath) throws Exception {
		List<Fragment> result = new ArrayList<Fragment>();
		Stack<Fragment> stack = new Stack<Fragment>();
//		String xmlPath = "/sdcard/letaotao/fragment.xml";
		InputStream in = new FileInputStream(xmlPath);
		XmlPullParser parser = Xml.newPullParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		parser.setInput(in, null);
		int event = parser.next();
		while (event != XmlPullParser.END_DOCUMENT) {
			if (event == XmlPullParser.START_TAG) {
//				String name = parser.getName();
				String path = parser.getAttributeValue(null, "path");
				String description = 
						parser.getAttributeValue(null, "description");
				String title = parser.getAttributeValue(null, "title");
				String sStart = parser.getAttributeValue(null, "start");
				String sEnd = parser.getAttributeValue(null, "end");
				Integer start = null;
				Integer end = null;
				if (sStart != null) {
					start = Integer.parseInt(sStart);
				}
				if (sEnd != null) {
					end = Integer.parseInt(sEnd);
				}
				Fragment fragment = 
						new Fragment(title, path, description, start, end);
				stack.push(fragment);
			} else if (event == XmlPullParser.END_TAG) {
				Fragment fragment = stack.pop();
				if (!stack.isEmpty()) {
					Fragment top = stack.peek();
					top.addChild(fragment);
				} else {
					result.add(fragment);
				}
			}
			event = parser.next();
		}

		return result;
	}
}
