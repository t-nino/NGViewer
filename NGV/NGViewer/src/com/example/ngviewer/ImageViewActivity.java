package com.example.ngviewer;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ngviewer.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;


/**
 * URLに含まれた画像を抽出し、グリッド表示するアクティビティ。とりあえず仮。
 * リストとグリッド、扱うデータが違うだけで、ほとんどMainActivitｙと同じ
 * @author t-nino
 *
 */

public class ImageViewActivity extends FragmentActivity implements LoaderCallbacks<ArrayList<ImageContainer>>{

	private String url;

	private GridView gridView;
	private GridAdaputer gridAdapter;
	ImageLoader loader;
	//表示のために先読みしておく数
	private final int buffer = 10;

	@Override
	public void onCreate(final Bundle savedInstanceState){
	    super.onCreate(savedInstanceState);

	    setContentView(R.layout.activity_gridviewer);
	    gridView = (GridView) findViewById(R.id.gridView1);

	    gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO 自動生成されたメソッド・スタブ
				// TODO 自動生成されたメソッド・スタブ
				String url = null;
				if(gridAdapter!=null){
					url = gridAdapter.getImageURL(arg2);
				}
				if(url!=null){
					Uri uri = Uri.parse(url);
					Intent nextIntent = new Intent(Intent.ACTION_VIEW,uri);
					startActivity(nextIntent);
				}
			}
		});


	    Intent intent = getIntent();
	    url = intent.getStringExtra("url");

		loader = ImageLoader.getInstance();

		//initLoaderだと、最初の一回しか起動しない
		getSupportLoaderManager().restartLoader(0,null,this);

	}

	@Override
	public Loader<ArrayList<ImageContainer>> onCreateLoader(int arg0, Bundle arg1) {
		// TODO 自動生成されたメソッド・スタブ

		AsyncHttpRequestLoader asyncHttpRequestLoader = new AsyncHttpRequestLoader(this);
		asyncHttpRequestLoader.setURL(url);
		return asyncHttpRequestLoader;
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<ImageContainer>> arg0,
			ArrayList<ImageContainer> arg1) {
		// TODO 自動生成されたメソッド・スタブ

		//
		if(arg1!=null){
			gridAdapter = new GridAdaputer(this,0,arg1);
			gridView.setAdapter(gridAdapter);
		}

	}

	@Override
	public void onLoaderReset(Loader<ArrayList<ImageContainer>> arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}



	//処理を呼び出し先で処理させるためにインナークラスにする

	public class GridAdaputer extends BaseAdapter{

		private LayoutInflater layoutInflater_;
	    View holder;
	    Context context;
	    ArrayList<ImageContainer> images;

		 public GridAdaputer(Context context, int id, List<ImageContainer> rss) {
			 super();
			 layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 this.context = context;
			 images = (ArrayList<ImageContainer>) rss;
		 }

		 @Override
			 public View getView(int position, View convertView, ViewGroup parent) {

		 	 ViewHolder holder;

			 // convertViewはnullの時だけ新しく作る
			 if (convertView == null) {
				 convertView = layoutInflater_.inflate(R.layout.rss, null);
				 holder = new ViewHolder();
				 holder.text = (TextView)convertView.findViewById(R.id.text);
				 holder.icon = (ImageView)convertView.findViewById(R.id.image);
				 convertView.setTag(holder);

			 }else{
				 holder = (ViewHolder)convertView.getTag();
			 }

			 ImageContainer item = (ImageContainer)images.get(position);


			 //Viewのテキストに記事のタイトルをセットする
			 holder.text.setText(item.title);

			 //読み込みが完了していればViewにサムネイル画像をセットする。
			 if(item.getLoadStatus()==ImageContainer.LOADED){
				 holder.icon.setImageBitmap(item.getBitmap());
			 //サムネイルの読み込みが完了していなければ、画像の読み込みを開始させる
			 }else{
				 //ここで、読み込み中のBMPを入れておかないと、使い回しがでてしまうので、対応
				 holder.icon = (ImageView)convertView.findViewById(R.id.image);
				 //初期画像をセットしておく
				 holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher));

				 //バッファ分だけ先読みでロード指示を出しておく
				 int max = Math.min(getCount(),position + buffer);
				 int count = position;
				while(count < max){
					 final ImageContainer bufItem = (ImageContainer)getItem(count);

					 //ロード完了時のためのリスナーを設定しておく
					 //繰り返し読み込み指示を出さないため、ロード中・ロード後はなにもしない
					 if(bufItem.getLoadStatus()==ImageContainer.NOT){
						 bufItem.getLoader().loadImage(bufItem.getImage(),new SimpleImageLoadingListener(){
					        /**
					         * サムネイルのロードが完了した際の処理
					         */
							 @Override
					        public void onLoadingComplete(String imageUri,View view, Bitmap loadedImage) {
System.out.println("LOAD COMP");
								//ロードした画像を管理クラスに保存しておく
					            bufItem.setBitmap(loadedImage);
					            //読み込みフラグを完了済みにしておく
					            bufItem.setLoadStatus(ImageContainer.LOADED);
					            //holder.icon.setImageBitmap(loadedImage);
					            //アダプタの状態が変わったことを通知
					            gridAdapter.notifyDataSetChanged();
					            //リストを再描画（行わなくても更新されているので、不要のようである
					            gridView.invalidateViews();
					        }
						 });
						 //ロード中にしておく
						 bufItem.setLoadStatus(ImageContainer.LOADING);
 System.out.println("LOAD START");
					 }
					 //再格納？
					 images.set(count,bufItem);
					 count++;
				 }
			 }

			 return convertView;
			 }

		 public String getImageURL(int position){
			 //エラー防止のため、数チェック。これでいいか後でテスト
			 if(position < getCount()){
				 ImageContainer rss = getItem(position);
				 return rss.getImage();
			 }else{
				 return null;
			 }
		 }

		@Override
		public int getCount() {
			// TODO 自動生成されたメソッド・スタブ
			return images.size();
		}

		@Override
		public ImageContainer getItem(int position) {
			// TODO 自動生成されたメソッド・スタブ
			return images.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO 自動生成されたメソッド・スタブ
			return position;
		}
	}
		 static class ViewHolder{
			 ImageView icon;
			 TextView text;
		 }

}
