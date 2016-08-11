package com.yamatocreation.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

public class WebSiteView extends WebView
{
	//////////////////////////////////////////////////////////////
	// 郢�?郢ｧ�ｿ
    private OnTouchEventCallback mOnTouchEventCallback;

    public WebSiteView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public WebSiteView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public WebSiteView(Context context) {
        super(context);

    }

    public void setmOnTouchEventCallback(final OnTouchEventCallback onTouchEventCallback){
        mOnTouchEventCallback = onTouchEventCallback;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(mOnTouchEventCallback != null) mOnTouchEventCallback.onTouchStateChanged(event.getAction(), event);
        return super.onTouchEvent(event);
    }


    public static interface OnTouchEventCallback
    {
        public void onTouchStateChanged(int state, MotionEvent event);
    }

}