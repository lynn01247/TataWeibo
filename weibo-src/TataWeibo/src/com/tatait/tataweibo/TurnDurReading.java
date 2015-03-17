package com.tatait.tataweibo;

import java.io.IOException;

import com.tatait.tataweibo.util.AccessTokenKeeper;
import com.tatait.tataweibo.util.file.BookPageFactory;
import com.tatait.tataweibo.util.file.PageWidget;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class TurnDurReading extends Activity {
	/** Called when the activity is first created. */
	private PageWidget mPageWidget;
	Bitmap mCurPageBitmap, mNextPageBitmap;
	Canvas mCurPageCanvas, mNextPageCanvas;
	BookPageFactory pagefactory;
	private String filenameString;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        mPageWidget = new PageWidget(this,screenWidth,screenHeight);
		setContentView(mPageWidget);
		Bundle bunde = this.getIntent().getExtras();
		filenameString = bunde.getString("FILE_PATH");

		mCurPageBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
		mNextPageBitmap = Bitmap
				.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);

		mCurPageCanvas = new Canvas(mCurPageBitmap);
		mNextPageCanvas = new Canvas(mNextPageBitmap);
		boolean isGBK = AccessTokenKeeper.readCharGBK(TurnDurReading.this);
		pagefactory = new BookPageFactory(screenWidth, screenHeight,isGBK);

		pagefactory.setBgBitmap(BitmapFactory.decodeResource(
				this.getResources(), R.color.niupizhi));

		
			if(filenameString==null){
				Toast.makeText(this, "电子书不存在。",
						Toast.LENGTH_SHORT).show();
				return;
			}else{
				try {
					pagefactory.openbook(filenameString);
					pagefactory.my_onDraw(mCurPageCanvas);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					
				}
			}
			

		mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);

		mPageWidget.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				// TODO Auto-generated method stub
				
				boolean ret=false;
				if (v == mPageWidget) {
					if (e.getAction() == MotionEvent.ACTION_DOWN) {
						mPageWidget.abortAnimation();
						mPageWidget.calcCornerXY(e.getX(), e.getY());

						pagefactory.my_onDraw(mCurPageCanvas);
						if (mPageWidget.DragToRight()) {
							try {
								pagefactory.prePage();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}						
							if(pagefactory.isfirstPage())return false;
							pagefactory.my_onDraw(mNextPageCanvas);
						} else {
							try {
								pagefactory.nextPage();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							if(pagefactory.islastPage())return false;
							pagefactory.my_onDraw(mNextPageCanvas);
						}
						mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
					}
                 
					 ret = mPageWidget.doTouchEvent(e);
					return ret;
				}
				return false;
			}

		});
	}
}