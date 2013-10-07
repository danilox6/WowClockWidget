package com.devxperiments.wowclockwidget;

/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RemoteViews.RemoteView;

import java.util.TimeZone;

import com.devxperiments.wowclockwidget.R;


/**
 * This widget display an analogic clock with two hands for hours and minutes.
 * 
 * @attr ref android.R.styleable#AnalogClock_dial
 * @attr ref android.R.styleable#AnalogClock_hand_hour
 * @attr ref android.R.styleable#AnalogClock_hand_minute
 */
@RemoteView
public class CustomAnalogClock extends View {
	private Time mCalendar;
	
	private Context mContext;

	private Drawable mHourHand;
	private Drawable mMinuteHand;
	private Drawable mDial;

	private int mDialWidth;
	private int mDialHeight;

	private boolean mAttached;

	private final Handler mHandler = new Handler();
	private float mMinutes;
	private float mHour;
	private boolean mChanged;

	public CustomAnalogClock(Context context) {
		this(context, null);
	}

	public CustomAnalogClock(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomAnalogClock(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		
		mContext = getContext();
		Resources r = mContext.getResources();
//		TypedArray a = context.obtainStyledAttributes(attrs,
//				com.android.R.styleable.AnalogClock, defStyle, 0);

		mDial = r.getDrawable(R.drawable.appwidget_clock_dial);
//		if (mDial == null) {
//			mDial = r.getDrawable(com.android.internal.R.drawable.clock_dial);
//		}

		mHourHand = r.getDrawable(R.drawable.appwidget_clock_hour);
//		if (mHourHand == null) {
//			mHourHand = r
//					.getDrawable(com.android.internal.R.drawable.clock_hand_hour);
//		}

		mMinuteHand = r.getDrawable(R.drawable.appwidget_clock_minute);
//		if (mMinuteHand == null) {
//			mMinuteHand = r
//					.getDrawable(com.android.internal.R.drawable.clock_hand_minute);
//		}

		mCalendar = new Time();

		mDialWidth = mDial.getIntrinsicWidth();
		mDialHeight = mDial.getIntrinsicHeight();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		if (!mAttached) {
			mAttached = true;
			IntentFilter filter = new IntentFilter();

			filter.addAction(Intent.ACTION_TIME_TICK);
			filter.addAction(Intent.ACTION_TIME_CHANGED);
			filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

			getContext().registerReceiver(mIntentReceiver, filter, null,
					mHandler);
		}

		// NOTE: It's safe to do these after registering the receiver since the
		// receiver always runs
		// in the main thread, therefore the receiver can't run before this
		// method returns.

		// The time zone may have changed while the receiver wasn't registered,
		// so update the Time
		mCalendar = new Time();

		// Make sure we update to the current time
		onTimeChanged();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mAttached) {
			getContext().unregisterReceiver(mIntentReceiver);
			mAttached = false;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		float hScale = 1.0f;
		float vScale = 1.0f;

		if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
			hScale = (float) widthSize / (float) mDialWidth;
		}

		if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
			vScale = (float) heightSize / (float) mDialHeight;
		}

		float scale = Math.min(hScale, vScale);

		setMeasuredDimension(
				resolveStateAndSize((int) (mDialWidth * scale),
						widthMeasureSpec, 0),
				resolveStateAndSize((int) (mDialHeight * scale),
						heightMeasureSpec, 0));
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mChanged = true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		boolean changed = mChanged;
		if (changed) {
			mChanged = false;
		}

		int availableWidth = getWidth();
		int availableHeight = getHeight();

		int x = availableWidth / 2;
		int y = availableHeight / 2;

		final Drawable dial = mDial;
		int w = dial.getIntrinsicWidth();
		int h = dial.getIntrinsicHeight();

		boolean scaled = false;

		if (availableWidth < w || availableHeight < h) {
			scaled = true;
			float scale = Math.min((float) availableWidth / (float) w,
					(float) availableHeight / (float) h);
			canvas.save();
			canvas.scale(scale, scale, x, y);
		}

		if (changed) {
			dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
		}
		dial.draw(canvas);

		canvas.save();
		canvas.rotate(mHour / 12.0f * 360.0f, x, y);
		final Drawable hourHand = mHourHand;
		if (changed) {
			w = hourHand.getIntrinsicWidth();
			h = hourHand.getIntrinsicHeight();
			hourHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
					+ (h / 2));
		}
		hourHand.draw(canvas);
		canvas.restore();

		canvas.save();
		canvas.rotate(mMinutes / 60.0f * 360.0f, x, y);

		final Drawable minuteHand = mMinuteHand;
		if (changed) {
			w = minuteHand.getIntrinsicWidth();
			h = minuteHand.getIntrinsicHeight();
			minuteHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
					+ (h / 2));
		}
		minuteHand.draw(canvas);
		canvas.restore();

		if (scaled) {
			canvas.restore();
		}
	}

	private void onTimeChanged() {
		mCalendar.setToNow();

		int hour = mCalendar.hour;
		int minute = mCalendar.minute;
		int second = mCalendar.second;

		mMinutes = minute + second / 60.0f;
		mHour = hour + mMinutes / 60.0f;
		mChanged = true;

		updateContentDescription(mCalendar);
	}

	private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
				String tz = intent.getStringExtra("time-zone");
				mCalendar = new Time(TimeZone.getTimeZone(tz).getID());
			}

			onTimeChanged();

			invalidate();
		}
	};
	
	
	public void setDialDrawableId(int id){
		mDial = mContext.getResources().getDrawable(id);
		invalidate();
	}
	
	public void setHourHandDrawableId(int id){
		mHourHand = mContext.getResources().getDrawable(id);
		invalidate();
	}
	
	public void setMinuteHandDrawableId(int id){
		mMinuteHand = mContext.getResources().getDrawable(id);
		invalidate();
	}

	@SuppressWarnings("deprecation")
	private void updateContentDescription(Time time) {
		final int flags = DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_24HOUR;
		String contentDescription = DateUtils.formatDateTime(mContext,
				time.toMillis(false), flags);
		setContentDescription(contentDescription);
	}
	
	   /**
     * Utility to reconcile a desired size and state, with constraints imposed
     * by a MeasureSpec. Will take the desired size, unless a different size
     * is imposed by the constraints. The returned value is a compound integer,
     * with the resolved size in the {@link #MEASURED_SIZE_MASK} bits and
     * optionally the bit {@link #MEASURED_STATE_TOO_SMALL} set if the resulting
     * size is smaller than the size the view wants to be.
     *
     * @param size How big the view wants to be
     * @param measureSpec Constraints imposed by the parent
     * @return Size information bit mask as defined by
     * {@link #MEASURED_SIZE_MASK} and {@link #MEASURED_STATE_TOO_SMALL}.
     */
    private int resolveStateAndSize(int size, int measureSpec, int childMeasuredState) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
        case MeasureSpec.UNSPECIFIED:
            result = size;
            break;
        case MeasureSpec.AT_MOST:
            if (specSize < size) {
                result = specSize | MEASURED_STATE_TOO_SMALL;
            } else {
                result = size;
            }
            break;
        case MeasureSpec.EXACTLY:
            result = specSize;
            break;
        }
        return result | (childMeasuredState&MEASURED_STATE_MASK);
    }
}