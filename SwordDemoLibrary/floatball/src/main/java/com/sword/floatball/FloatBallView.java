package com.sword.floatball;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.utilclass.LogUtil;
import com.example.utilclass.ScreenSize;

/**
 * {@hide}
 */
public class FloatBallView extends View {
	private static final int WAKE_UP_TIME = 3000;

	private final Paint backPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	private final GestureDetector gestureDetector;

	private final Runnable sleepRunnable = new SleepRunnable();

	private final int border = ScreenSize.dpToPx(3);
	private final int screenWidth;
	private int borderAlpha = (int) (0.3 * 0xff);
	private int innerCircleAlpha = (int) (0.6 * 0xff);
	private float offsetX = 0;
	private float offsetY = 0;
	private boolean sleep = false;

	public FloatBallView(@NonNull Context context) {
		super(context);

		backPaint.setColor(Color.BLACK);

		FloatBallGestureListener listener = new FloatBallGestureListener();
		gestureDetector = new GestureDetector(context, listener);

		screenWidth = ScreenSize.getWindowSizeExcludeSystem(context).x;
		
		setBackgroundColor(Color.WHITE);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		float centerX = (getLeft() + getRight()) / 2f + offsetX;
		float centerY = (getTop() + getBottom()) / 2f + offsetY;
		backPaint.setStyle(Paint.Style.FILL);
		backPaint.setAlpha(innerCircleAlpha);
		canvas.drawCircle(centerX, centerY, getWidth()/2f - border - 3, backPaint);
		
		//canvas.drawBitmap();
		backPaint.setAlpha(borderAlpha);
		backPaint.setStyle(Paint.Style.STROKE);
		backPaint.setStrokeWidth(border);
		canvas.drawCircle(centerX, centerY, getWidth()/2f - border, backPaint);
	}

	public void setInnerCircleAlpha(int alpha) {
		innerCircleAlpha = alpha;
	}

	public void setBorderAlpha(int alpha) {
		borderAlpha = alpha;
	}

	public void setOffsetX(float offsetX) {
		this.offsetX = offsetX;
		invalidate();
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector != null) {
			gestureDetector.onTouchEvent(event);
		}
		return true;
	}

	private boolean scrolled = false;
	class FloatBallGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		//单击
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if (sleep) {
				wakeUp();
				postSleepRunnable();
			} else {
				performClick();
			}
			return true;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			LogUtil.debug("onSingleUp, scrolled: " + scrolled);
			return false;
		}

		//滚动
		@SuppressLint("ObjectAnimatorBinding")
		@Override
		public boolean onScroll(MotionEvent downEvent, MotionEvent currentEvent, float distanceX, float distanceY) {
			if (!scrolled) {
				removeCallbacks(sleepRunnable);
				scrolled = true;
			}
			
			switch (currentEvent.getAction()) {
				case MotionEvent.ACTION_UP:
					ObjectAnimator moveEdge;
					if (currentEvent.getX() > screenWidth / 2f) {
						moveEdge = ObjectAnimator.ofFloat(this, "offsetX", offsetX, screenWidth - getLeft());
					} else {
						moveEdge = ObjectAnimator.ofFloat(this, "offsetX", offsetX, 0);
					}
					moveEdge.addListener(animatorListener);
					moveEdge.start();
					break;
				case MotionEvent.ACTION_MOVE:
					offsetX += distanceX;
					offsetY += distanceY;
					invalidate();
					break;
				default:
					break;
			}
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			LogUtil.debug("onFling, scrolled" + scrolled);
			return false;
		}
	}

	private void postSleepRunnable() {
		postDelayed(sleepRunnable, WAKE_UP_TIME);
	}

	private void removeSleepRunnable() {
		removeCallbacks(sleepRunnable);
	}

	private class SleepRunnable implements Runnable {
		@Override
		public void run() {
			sleep();
		}
	}

	//休眠状态
	private void sleep() {
		if (sleep) return;
		sleep = true;

		if (getLeft() == 0) {
			offsetX -= getWidth() / 2f;
		} else {
			offsetX += getWidth() / 2f;
		}
		invalidate();
	}

	private void wakeUp() {
		if (!sleep) return;
		sleep = false;

		if (getLeft() == 0) {
			offsetX += getWidth() / 2f;
		} else {
			offsetX -= getWidth() / 2f;
		}
		invalidate();
	}

	private final AnimatorListenerAdapter animatorListener = new AnimatorListenerAdapter() {
		@Override
		public void onAnimationEnd(Animator animation) {
			scrolled = false;
			postSleepRunnable();
		}
	};


}