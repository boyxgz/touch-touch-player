package com.surelution;

import java.util.ArrayList;
import java.util.List;

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
}
