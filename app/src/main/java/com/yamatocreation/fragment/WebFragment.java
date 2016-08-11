package com.yamatocreation.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewDatabase;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yamatocreation.MainActivity;
import com.yamatocreation.R;
import com.yamatocreation.view.WebSiteView;

import org.apache.http.client.CookieStore;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class WebFragment extends Fragment
{
	//////////////////////////////////////////////////////////////
	// 定数
	public static final String ARG_SECTION_NUMBER = "section_number";
	public static final String ARG_INIT_URL = "init_url";
	public static final String URL_INIT = "http://yamatocreation.jimdo.com/";
	public static final String URL_ABOUT = "http://yamatocreation.cocotte.jp/yamatocreation/index";

	//////////////////////////////////////////////////////////////
	// データ
	protected String m_strURL;
	public String GetURL(){ return m_strURL; }

	//////////////////////////////////////////////////////////////
	// View
	protected MainActivity mTop;
	protected WebSiteView mWebView;
	public String GetTitle(){ return mWebView.getTitle(); }
	public void WebViewReload(){ mWebView.reload(); }

	protected RelativeLayout mWebUnderLayout;
	//protected Animation inWebMenuLayoutAnimation;
	//protected Animation outWebMenuLayoutAnimation;
	//protected Animation inWebUnderLayoutAnimation;
	//protected Animation outWebUnderLayoutAnimation;
	protected ImageView mBackBtn;
	protected ImageView mNextBtn;
	protected TextView mGoWebBtn;

	//////////////////////////////////////////////////////////////
	// メソッド

	/****************************************************
	 * WebMailFragment newInstance()<br>
	 * WebMailFragment の生成処理<br>
	 *
	 * @author k0j1
	 * @param	sectionNumber	選択されたメニューの番号（1相対）
	 * @return	生成したフラグメントクラス
	 ***************************************************/
	public static WebFragment newInstance(int sectionNumber, String strURL)
	{
		WebFragment csFragment = new WebFragment();

		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		args.putString(ARG_INIT_URL, strURL);
		csFragment.setArguments(args);
		csFragment.m_strURL = strURL;

		return csFragment;
	}

	// コンストラクタ
	public WebFragment()
	{
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.frag_web, container, false);

		mWebUnderLayout = (RelativeLayout)rootView.findViewById(R.id.WebUnderLayout);
		mWebView = (WebSiteView)rootView.findViewById(R.id.web);

		mBackBtn = (ImageView)rootView.findViewById(R.id.web_back_btn);
		mNextBtn = (ImageView)rootView.findViewById(R.id.web_next_btn);
		mGoWebBtn = (TextView)rootView.findViewById(R.id.web_go_site_text);

		//m_strURL = URL_INIT;
		InitWebView();
		InitButton();

		return rootView;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		mTop = (MainActivity) activity;
		mTop.onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
		m_strURL = getArguments().getString(ARG_INIT_URL);

		//inWebMenuLayoutAnimation = (Animation) AnimationUtils.loadAnimation(activity, R.anim.in_animation_l);
		//outWebMenuLayoutAnimation = (Animation) AnimationUtils.loadAnimation(activity, R.anim.out_animation_l);
		//inWebUnderLayoutAnimation = (Animation) AnimationUtils.loadAnimation(activity, R.anim.in_animation);
		//outWebUnderLayoutAnimation = (Animation) AnimationUtils.loadAnimation(activity, R.anim.out_animation);
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
	}

	@Override
	public void onStart()
	{
		super.onStart();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		try {
			WebView.class.getMethod("onResume").invoke(mWebView);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPause()
	{
		super.onPause();
		try {
			WebView.class.getMethod("onPause").invoke(mWebView);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSaveInstanceState(final Bundle outState)
	{
		super.onSaveInstanceState(outState);
	}

	/******************************************************************************************
	 * void InitButton()<br>
	 * 各ボタンの初期化<br>
	 *
	 * @author k0j1
	 * @return	none
	 *****************************************************************************************/
	public void InitButton()
	{
		// 戻るボタン
		mBackBtn.setClickable(true);
		mBackBtn.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				BackWeb();
			}
		});
		// 進むボタン
		mNextBtn.setClickable(true);
		mNextBtn.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				ForwardWeb();
			}
		});
		mGoWebBtn.setClickable(true);
		mGoWebBtn.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				IntentShare(mWebView.getUrl());
			}
		});
	}

	/*******************************************
	 * 一つ前のページに（ページがなければ終了）
	 *******************************************/
	private boolean BackWeb()
	{
		boolean bRet = false;
		if(mWebView.canGoBack()){
			// 履歴があれば表示
			mWebView.goBack();
			bRet = true;
		}
		return bRet;
	}

	/*******************************************
	 * 一つ先のページに
	 *******************************************/
	private boolean ForwardWeb()
	{
		boolean bRet = false;
		if (mWebView.canGoForward()){
			mWebView.goForward();
			bRet = true;
		}
		return bRet;
	}

	/*************************************
	 * WebViewの初期設定
	 **************************************/
	@SuppressLint("SetJavaScriptEnabled")
	public void InitWebView()
	{
		// Wide ViewPortでは、ページのサイズが画面サイズよりも大きい場合に画面に収めるように縮小できます。また、Overviewモードはページが画面に収まるように自動で縮小します。
		// Wide ViewPortとOverviewモードを組み合わせることで、ロードしたページ全体が画面に表示されるようになります。
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		//ズーム機能を有効にする
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setDisplayZoomControls(false);
		mWebView.getSettings().setSupportZoom(true);

		// WebViewで使うcookieの準備
		try{
			CookieSyncManager.createInstance(mTop);
			CookieSyncManager.getInstance().startSync();
			CookieManager.getInstance().setAcceptCookie(true);
			CookieManager.getInstance().removeExpiredCookie();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// javascriptの有効化
		try {
			mWebView.setWebViewClient(new WebViewClient());
			mWebView.getSettings().setJavaScriptEnabled(true);
			//m_csWeb.getSettings().setBuiltInZoomControls(true);
			mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
			// プラグインの設定
			mWebView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
		}catch(NoClassDefFoundError e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		// HTML5のための設定
		try{
			mWebView.getSettings().setDomStorageEnabled(true);
			String databasePath = mTop.getDir("localstorage", Context.MODE_PRIVATE).getPath();
			mWebView.getSettings().setDatabasePath(databasePath);
			mWebView.getSettings().setDatabaseEnabled(true);
			File databaseDir = new File(mTop.getCacheDir(), databasePath);
			databaseDir.mkdirs();
			mWebView.getSettings().setDatabasePath(databaseDir.toString());
		}catch(NoClassDefFoundError e){
			e.printStackTrace();
		}catch(IllegalStateException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}

		try{
			WebViewDatabase.getInstance(mTop).clearHttpAuthUsernamePassword();
			LoadUrlWithCookie(m_strURL);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// タッチ処理
		try {
			mWebView.setOnTouchListener(new OnTouchListener() {
				@Override public boolean onTouch(View v, MotionEvent event) {
					try {
						//get the URL of the touched anchor tag
						WebView.HitTestResult hr = ((WebView) v).getHitTestResult();
						String str = null;
						if (null != hr)
							str = hr.getExtra();
						if (str != null && str.startsWith("http://i.ytimg.com/vi/")) {
							String videoId = str.split("\\/")[4];
							//Everything is in place, now launch the activity
							Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoId));
							v.getContext().startActivity(i);
							return true;
						}
					}
					catch (NoClassDefFoundError e) {
						e.printStackTrace();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					return false;
				}
			});
		}catch(NoClassDefFoundError e){
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// ウェブクライアント
		try {
			mWebView.setWebViewClient(new WebViewClient() {
				private String loginCookie;

				@Override public void onLoadResource(WebView view, String url) {
					CookieManager cookieManager = CookieManager.getInstance();
					loginCookie = cookieManager.getCookie(url);
				}

				@Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
					super.onPageStarted(view, url, favicon);
				}

				@Override public void onPageFinished(WebView view, String url) {
					CookieManager cookieManager = CookieManager.getInstance();
					cookieManager.setCookie(url, loginCookie);
					super.onPageFinished(view, url);
				}

				@Override public void onReceivedHttpAuthRequest(WebView web, HttpAuthHandler handler, String host, String realm) {

					String[] up = web.getHttpAuthUsernamePassword(host, realm);
					if (up != null && up.length == 2) {
						handler.proceed(up[0], up[1]);
					}
				}

				@Override public void onReceivedError(WebView view, int error, String desc, String failUrl) {
					Log.e("Log", "errorCode:" + String.valueOf(error));
					Log.e("Description", "Description:" + desc);
					Log.e("failUrl", "failUrl:" + failUrl);
				}

				@Override public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
					handler.proceed();
				}

				@Override public boolean shouldOverrideUrlLoading(WebView view, String strURL) {
					boolean bRet = false;
					if (strURL != null && IsURLofGooglePlay(strURL)) {
						String strDetailsKeyword = strURL.substring(strURL.lastIndexOf("details?id="), strURL.length() - 1);
						IntentGooglePlay(mTop, strDetailsKeyword);
						bRet = true;
					}
					else if (strURL != null && IsURLofYoutube(strURL)) {
						String videoId = strURL.split("\\/")[4];
						Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoId));
						view.getContext().startActivity(i);
						bRet = true;
					}

					return bRet;
				}
			});
		}catch(NoClassDefFoundError e){
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		try {
			mWebView.setWebChromeClient(new WebChromeClient() {
				@Override public void onProgressChanged(WebView view, int progress) {
				}
			});
		}catch(NoClassDefFoundError e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}

		try{
			mWebView.setVerticalScrollbarOverlay(true);
		}catch(NoClassDefFoundError e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}

		try{
			mWebView.requestFocus();
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	public static boolean isHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}
	public static boolean isHoneycombTablet(Context context) {
		return isHoneycomb() && (context.getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK)
				== Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	/*******************************************
	 * クッキーのセット
	 *******************************************/
	public void LoadUrlWithCookie(String strUrl)
	{
		try{
			// HttpClientの準備
			DefaultHttpClient httpClient;
			httpClient = new DefaultHttpClient();
			httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
			httpClient.getParams().setParameter("http.connection.timeout", 5000);
			httpClient.getParams().setParameter("http.socket.timeout", 3000);

			// ログイン
			HttpURLConnection urlConnection = null;
			try {
				URL url = new URL(strUrl);
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod("POST");
				urlConnection.connect();
			} catch(Exception e) {
			} finally {
				try {
					if(urlConnection != null){
						urlConnection.disconnect();
					}
				} catch (Exception e) {}
			}

			// HttpClientで得たCookieの情報をWebViewでも利用できるようにする
			CookieStore cookieStr = httpClient.getCookieStore();
			Cookie cookie = null;
			if ( cookieStr != null ) {
				List<Cookie> cookies = cookieStr.getCookies();
				if (!cookies.isEmpty()) {
					for (int i = 0; i < cookies.size(); i++) {
						cookie = cookies.get(i);
					}
				}
				if (cookie != null) {
					String cookieString = cookie.getName() + "=" + cookie.getValue() + "; domain=" + cookie.getDomain();
					CookieManager.getInstance().setCookie( strUrl, cookieString);
					CookieSyncManager.getInstance().sync();
				}
			}

			mWebView.loadUrl(strUrl);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/******************************************************
	 * Youtubeへのリンクかを判定
	 *
	 * @param	strURL		URL
	 * @return	none
	 ******************************************************/
	protected boolean IsURLofYoutube(String strURL){	return strURL.startsWith("http://i.ytimg.com/vi/"); }

	/******************************************************
	 * GooglePlayへのリンクかを判定
	 *
	 * @param	strURL		URL
	 * @return	none
	 ******************************************************/
	protected boolean IsURLofGooglePlay(String strURL){	return strURL.startsWith("https://play.google.com/"); }

	/******************************************************
	 * GooglePlayアプリ起動
	 *
	 * @param	ac				Activity
	 * @param	strKeyword		GooglePlayで表示するキーワード
	 * @return	none
	 ******************************************************/
	static public void IntentGooglePlay(Activity ac, String strKeyword)
	{
		Uri uri = Uri.parse("market://" + strKeyword);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		ac.startActivity(intent);
	}
	/*******************************************
	 * void IntentShare()<br>
	 * 他のアプリで開く（ブラウザ起動）<br>
	 *
	 * @param	strURL		URL
	 * @return	none
	 *******************************************/
	public void IntentShare(String strURL)
	{
		Uri uri = Uri.parse(strURL);
		Intent i = new Intent(Intent.ACTION_VIEW,uri);
		startActivity(i);
	}

}