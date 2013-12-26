package com.example.ngviewer;

import android.app.Application;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * 外部モジュールImageLoaderのオプション指定のためのクラス
 * @author t-nino
 *
 */
public class NGVApplication extends Application{

    @Override
    public void onCreate() {

        super.onCreate();

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
        	.cacheInMemory(true)
        	.cacheOnDisc(true)
        	.build();

        // グローバル設定の生成と初期化を行う
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
        /*
        .memoryCache(new LruMemoryCache(2 * 1024 * 1024))

            .memoryCacheSize(2 * 1024 * 1024)
            .threadPoolSize(10)

            //... // ImageLoaderConfigurationの設定をメソッドチェインで繋いでいく
         */
             .defaultDisplayImageOptions(defaultOptions)
            .build();
        ImageLoader.getInstance().init(config);
    }
}
