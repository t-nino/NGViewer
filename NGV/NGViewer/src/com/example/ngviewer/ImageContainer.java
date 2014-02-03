package com.example.ngviewer;



import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.ImageLoader;



/**
 * 画像コンテンツの情報および画像そのものを管理するための情報コンテナクラス
 * @author t-nino
 *
 */
public class ImageContainer{

	//画像のダウンロード状況

	//まだ
	public static int NOT = 0;
	//ロード中
	public static int LOADING = 1;
	//ロード済み
	public static int LOADED = 2;

	public String title;
	public String link;
	public String image;
	public Bitmap bitmap;
	public int loadStatus;

	//外部モジュールでイメージの管理をおこなう
	//クラスを継承してこのクラスを作りたかったが、シングルトンであるため継承できない。仕方ないので、自分で持つ。
	public ImageLoader loader;

	public ImageContainer() {
		// TODO 自動生成されたコンストラクター・スタブ
		title  = null;
		link  = null;
		image = null;
		bitmap = null;
		loadStatus = NOT;
		loader = ImageLoader.getInstance();
	}



	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String thumnail) {
		this.image = thumnail;
	}
	public Bitmap getBitmap(){
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap){
		this.bitmap = bitmap;
	}
	public int getLoadStatus(){
		return loadStatus;
	}
	public void setLoadStatus(int loadStatus){
		this.loadStatus = loadStatus;
	}
	public ImageLoader getLoader(){
		return loader;
	}
	public void setLoader(ImageLoader loader){
		this.loader = loader;
	}

}
