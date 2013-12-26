package com.example.ngviewer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.util.Xml;


/**
 * 指定されたRSSをダウンロードし、特定のタグを抽出・管理する
 * @author t-nino
 *
 */
public class AsyncRSSLoader extends AsyncTaskLoader<ArrayList<ImageContainer>> {

	private String urlStr = null;

	public AsyncRSSLoader(Context context) {
		super(context);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public void setURL(String urlStr){
		this.urlStr = urlStr;
	}
	public void setURL(URL url){
		this.urlStr = url.getPath();
	}


	@Override
	public ArrayList<ImageContainer> loadInBackground() {
		// TODO 自動生成されたメソッド・スタブ
		return getNeverRSS();
	}


	@Override
	protected void onStartLoading() {
		// TODO 自動生成されたメソッド・スタブ
		super.onStartLoading();
		forceLoad();
	}

	private ArrayList<ImageContainer> getNeverRSS(){

		URL url;
		ArrayList<ImageContainer> retArray = new ArrayList<ImageContainer>();

		try {
			url = new URL(urlStr);


			URLConnection con = url.openConnection();
			InputStreamReader is = new InputStreamReader(con.getInputStream());
			XmlPullParser xmlPullParser = Xml.newPullParser();
			xmlPullParser.setInput(is);

			    int eventType;
			    eventType = xmlPullParser.getEventType();
			    ImageContainer rss = new ImageContainer();
			    while ((eventType = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT) {

			    	if (eventType == XmlPullParser.START_TAG){
			    		String name = xmlPullParser.getName();
			    		Log.d("XmlPullParserSample",name);

			    		if(name.equals("item")){
			    			rss = new ImageContainer();
			    		}else if(name.equals("title")){
			    			String title = xmlPullParser.nextText();
			    			Log.d("XmlPullParserSample",title);
			    			rss.setTitle(title);
			    		}else if(name.equals("link")){
			    			String link = xmlPullParser.nextText();
			    			Log.d("XmlPullParserSample",link);
			    			rss.setLink(link);
			    		}else if(name.equals("thumbnail")){
			    			String thumbnail = xmlPullParser.getAttributeValue(null,"url");
			    			Log.d("XmlPullParserSample",thumbnail);
			    			rss.setImage(thumbnail);
			    		}
			    	}else if(eventType == XmlPullParser.END_TAG){
			    		String name = xmlPullParser.getName();
			    		if(name.equals("item")){
				    		retArray.add(rss);
			    		}

			    	}
			    	/*　デバグ用
			        if(eventType == XmlPullParser.START_DOCUMENT) {
			            Log.d("XmlPullParserSample", "Start document");
			        } else if(eventType == XmlPullParser.END_DOCUMENT) {
			            Log.d("XmlPullParserSample", "End document");
			        } else if(eventType == XmlPullParser.START_TAG) {
			            Log.d("XmlPullParserSample", "Start tag "+xmlPullParser.getName());
			        } else if(eventType == XmlPullParser.END_TAG) {
			            Log.d("XmlPullParserSample", "End tag "+xmlPullParser.getName());
			        } else if(eventType == XmlPullParser.TEXT) {
			            Log.d("XmlPullParserSample", "Text "+xmlPullParser.getText());
			        }
			        */
			        eventType = xmlPullParser.next();
			    }
			} catch (MalformedURLException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			return retArray;
		}
}
