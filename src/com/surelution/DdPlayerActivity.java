package com.surelution;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Stack;

import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class DdPlayerActivity extends Activity implements OnClickListener {
//    private MediaPlayer player;
    private Fragment fragment;
	
	private Stack<Fragment> stack = new Stack<Fragment>();
	private Fragment currFragment;
	
	private String currPlayerPath;
	private MediaPlayer currPlayer;

	private CountDownTimer timer;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        try {
			parseXml();
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        currFragment = fragment;
        redraw();
    }

	@Override
	public void onClick(View view) {
//		if(view.getId() == R.id.button1) {
//			play(21000, 40000);
//		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.menuItemCopyright) {
			Intent i = new Intent(this, CopyrightActivity.class);
			startActivity(i);
		} else if(item.getItemId() == R.id.menuItemExit) {
			finish();
			System.exit(0);
		}
		return true;
	}
	
	private void redraw() {
		Stack<Fragment> stack = new Stack<Fragment>();
		stack.push(currFragment);
		Fragment parent = currFragment.getParent();
		while(parent != null) {
			stack.push(parent);
			parent = parent.getParent();
		}
        LinearLayout breadCrumb = (LinearLayout)findViewById(R.id.tableRow1);
        LinearLayout op = (LinearLayout)findViewById(R.id.tableRow2);
        
        breadCrumb.removeAllViews();
        op.removeAllViews();
        
        class l implements OnClickListener {
        	
        	private Fragment fragment;

        	private int start, end;
        	l(Fragment fragment) {
        		this.fragment = fragment;
        		if(this.fragment.start == null) {
        			start = 0;
        		} else {
        			start = this.fragment.start;
        		}
        		if(this.fragment.end == null) {
        			end = getPlayer(fragment.getMediaFile()).getDuration();
        		} else {
        			end = this.fragment.end;
        		}
        	}
			@Override
			public void onClick(View v) {
				if(fragment.getChildren() == null) {
					play(fragment.start, fragment.end);
				} else {
					currFragment = fragment;
					redraw();
					play(start, end);
				}
			}
        	
        }
        
        while(!stack.empty()) {
        	Fragment f = stack.pop();
        	Button b = new Button(this);
        	b.setText(f.title);
        	b.setOnClickListener(new l(f));
        	breadCrumb.addView(b);
        }
        
		for(Fragment f : currFragment.getChildren()) {
        	Button b = new Button(this);
        	b.setText(f.title);
        	b.setOnClickListener(new l(f));
        	op.addView(b);
        }
	}
	
	private void play(int start, int end) {
//		if(getPlayer(fragment.getMediaFile()).isPlaying()) {
//			player.pause();
//		}
		final MediaPlayer player = getPlayer(fragment.getMediaFile());
		player.seekTo(start);
		player.start();
		if(timer != null) {
			timer.cancel();
		}
		timer = new CountDownTimer(end - start, 10) {
			
			@Override
			public void onTick(long millisUntilFinished) {
				
			}
			
			@Override
			public void onFinish() {
				player.pause();
			}
		};
		timer.start();
	}

	private void parseXml() throws Exception {
		String xmlPath = "/sdcard/letaotao/fragment.xml";
		InputStream in = new FileInputStream(xmlPath);
		XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(in, null);
        int event = parser.next();
        while(event != XmlPullParser.END_DOCUMENT) {
        	if(event == XmlPullParser.START_TAG) {
                String name = parser.getName();
                String path = parser.getAttributeValue(null, "path");
                String description = parser.getAttributeValue(null, "description");
                String title = parser.getAttributeValue(null, "title");
                String sStart = parser.getAttributeValue(null, "start");
                String sEnd = parser.getAttributeValue(null, "end");
                Integer start = null;
                Integer end = null;
                if(sStart != null) {
                	start = Integer.parseInt(sStart);
                }
                if(sEnd != null) {
                	end = Integer.parseInt(sEnd);
                }
                Log.i("shit", name + "," + "," + path + "," + description + "," + title + "," + start + "," + end);
                Fragment fragment = new Fragment(title, path, description, start, end);
                stack.push(fragment);
        	} else if(event == XmlPullParser.END_TAG) {
        		fragment = stack.pop();
        		if(!stack.isEmpty()) {
            		Fragment top = stack.peek();
            		if(top != null) {
            			top.addChild(fragment);
            		}
        		}
        	}
        	event = parser.next();
        }
        
	}
	
	private MediaPlayer getPlayer(String path) {
		if(!path.equals(currPlayerPath)) {
			currPlayerPath = path;
	        Uri uri = Uri.parse("file:///sdcard/letaotao/" + path);
	        currPlayer = MediaPlayer.create(this, uri);
		}
		return currPlayer;
	}

}