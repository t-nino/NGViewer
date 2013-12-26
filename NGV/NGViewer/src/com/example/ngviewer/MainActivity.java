package com.example.ngviewer;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ngviewer.R;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 * メイン
 * AsyncLoader利用のため、Activityではなく、FragmentActivityを利用しています。
 * また、コールバックの検知のためLoaderCallbackをインプリメントしています。
 * @author t-nino
 */
public class MainActivity extends FragmentActivity implements LoaderCallbacks<ArrayList<ImageContainer>>{


	//大元になるRSS記事(Naverまとめ画像)
	private final String KIJI＿RSS = "http://matome.naver.jp/feed/topic/1Luxr";

	//表示のために先読みしておく数
	private final int buffer = 10;

	//記事一覧を表示するためのリストビュー
	ListView listView;
	//RSSの管理＋表示をあわせた機能を持つListView用のアダプター
	RSSAdaputer rssAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		listView = (ListView)findViewById(R.id.listView1);
		listView.setOnItemClickListener(new OnItemClickListener() {

		/*
		 * Listのアイテムをクリックすることで、その記事のURLを次のActivityに引き渡して起動
		 */
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO 自動生成されたメソッド・スタブ
				String url = null;
				if(rssAdapter!=null){
					url = rssAdapter.getURL(arg2);
				}
				if(url!=null){
					Intent nextIntent = new Intent(MainActivity.this,ImageViewActivity.class);
					nextIntent.putExtra("url",url);
					startActivity(nextIntent);
				}
			}
		});

		//asyncLoaderを初期化。initだとinitLoaderだと、最初の一回しか起動しないのでRestartを利用
		getSupportLoaderManager().restartLoader(0,null,this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	/*
	 * ここでは、Loader呼び出し時の初期化と、Loader作って返す処理をする。
	 * 初期化は、たとえばダイアログの作成などが当てはまるが、今回は行っていない
	 */
	@Override
	public Loader<ArrayList<ImageContainer>> onCreateLoader(int arg0, Bundle arg1) {
		// TODO 自動生成されたメソッド・スタブ


		//独自のasyncLoaderをつくって、読み込むURLをセットして返す
		AsyncRSSLoader asyncRSSLoader = new AsyncRSSLoader(this);
		asyncRSSLoader.setURL(KIJI＿RSS);
		return asyncRSSLoader;
	}

	/**
	 * RSSの読み込みが終了した際に呼び出される
	 * @param arg0 Loader
	 * @param arg1 RSSに含まれる記事を管理するコンテナの配列
	 */

	@Override
	public void onLoadFinished(Loader<ArrayList<ImageContainer>> arg0,ArrayList<ImageContainer> arg1) {
		// TODO 自動生成されたメソッド・スタブ
		if(arg1!=null){
			rssAdapter = new RSSAdaputer(this,0,arg1);
			listView.setAdapter(rssAdapter);
		}
	}

	/**
	 * ロードがリセットされた際の処理。未実装
	 * @param arg0
	 */
	@Override
	public void onLoaderReset(Loader<ArrayList<ImageContainer>> arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	/**
	 * RSS表示・管理用のアダプタ。
	 * 今回はMainActivityに含まれるListViewの更新をこちらからおこないたかったので、インナークラスにしている
	 * @author t-nino
	 *
	 */
	public class RSSAdaputer extends BaseAdapter{

		private LayoutInflater layoutInflater_;
	    View holder;
	    Context context;
	    //内部保存用のコンテナ
	    ArrayList<ImageContainer> rssItems;


		 public RSSAdaputer(Context context, int id, List<ImageContainer> rss) {
			 super();
			 layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 this.context = context;
			 this.rssItems = (ArrayList<ImageContainer>) rss;
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

				 ImageContainer item = (ImageContainer)rssItems.get(position);


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
						 final ImageContainer bufItem = getItem(count);

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
						            rssAdapter.notifyDataSetChanged();
						            //リストを再描画（行わなくても更新されているので、不要のようである
						            listView.invalidateViews();
						        }
							 });
							 //ロード中にしておく
							 bufItem.setLoadStatus(ImageContainer.LOADING);
						 }
 System.out.println("LOAD START");
						 //再格納？
						 rssItems.set(count,bufItem);
						 count++;
					 }
				 }

				 return convertView;
			 }

		 public String getURL(int position){
			 //エラー防止のため、数チェック。これでいいか後でテスト
			 if(position < getCount()){
				 ImageContainer rss = getItem(position);
				 return rss.getLink();
			 }else{
				 return null;
			 }
		 }

		@Override
		public int getCount() {
			// TODO 自動生成されたメソッド・スタブ
			return rssItems.size();
		}

		@Override
		public ImageContainer getItem(int position) {
			// TODO 自動生成されたメソッド・スタブ
			return rssItems.get(position);
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
