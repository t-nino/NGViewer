package com.example.ngviewer;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.ngviewer.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 *画像リストを非同期でダウンロード・表示するアダプタ
 * @author t-nino
 *
 */
public class ImageAdaputer extends ArrayAdapter<String>{

	private LayoutInflater layoutInflater_;
    ImageLoader loader;

	 public ImageAdaputer(Context context, int id, List<String> url) {
		 super(context, id, url);
		 layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 loader = ImageLoader.getInstance();
	 }

	 @Override
		 public View getView(int position, View convertView, ViewGroup parent) {
			 // 特定の行(position)のデータを得る
			 String url = (String)getItem(position);

			 // convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
			 if (null == convertView) {
			 convertView = layoutInflater_.inflate(R.layout.image, null);
			 }

			 // データをViewの各Widgetにセットする
			 //ImageView imageView = (ImageView)convertView.findViewById(R.id.image);
			final ImageView imageView = (ImageView)convertView;


			 // loadImageを使う場合
			 loader.loadImage(url, new SimpleImageLoadingListener() {
			        @Override
			        public void onLoadingComplete(String imageUri,View view, Bitmap loadedImage) {
			            imageView.setImageBitmap(loadedImage);
			        }
			    });

			    // displayImageを使う場合
			//loader.displayImage(imageUrl, imageView);

/*
			 TextView textView;
			 textView = (TextView)convertView.findViewById(R.id.text);
			 textView.setText(rss.getTitle());
*/
			 return convertView;
		 }


}
