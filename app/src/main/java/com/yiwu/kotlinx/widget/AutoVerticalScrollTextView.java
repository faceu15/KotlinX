package com.yiwu.kotlinx.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatTextView;

import com.yiwu.kotlinx.R;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.lang.ref.WeakReference;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Function:
 * Created by BBTree Team
 * Author: EngrZhou
 * Create Date: 2016/08/11
 * Create Time: 上午10:00
 */
public class AutoVerticalScrollTextView extends AppCompatTextView {

    private static final String TAG = AutoVerticalScrollTextView.class.getSimpleName();

    private static final int LINE_EXTRA_PADDING = 5;
    private static final int DEFAULT_DELAY_START_SECOND = 5;
    /**
     * scroll delay time
     */
    private int mDelayStart = DEFAULT_DELAY_START_SECOND;
    /**
     * is reset after scroll finish
     */
    private boolean mIsResetOnFinish = true;
    private int mStep = 5;
    private int mSpeed = 1;
    private ScrollStatusListener mScrollStatusListener;
    private volatile boolean mIsScrolling;
    private boolean isScrolled = false;
    private ScheduledExecutorService mMarquee = null;
    private MarqueeRunnable mMarqueeRunnable;
    private StaticLayout mTextLayout;
    private int mNowPoint;
    private int mMyHeight;

    public AutoVerticalScrollTextView(Context context) {
        this(context, null);
    }

    public AutoVerticalScrollTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoVerticalScrollTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        mNowPoint = 0;
        requestLayout();
        resetTextParams();
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AutoVerticalScrollTextView, 0, 0);
            mDelayStart = array.getInt(R.styleable.AutoVerticalScrollTextView_delayStart, DEFAULT_DELAY_START_SECOND);
            mIsResetOnFinish = array.getBoolean(R.styleable.AutoVerticalScrollTextView_resetOnFinish, true);
            mStep = array.getInt(R.styleable.AutoVerticalScrollTextView_step, 1);
            mSpeed = array.getInt(R.styleable.AutoVerticalScrollTextView_speed, 2);
            array.recycle();
        }
        setSingleLine(false);
    }

    public int getDelayStart() {
        return mDelayStart;
    }

    public void setDelayStart(int delayStart) {
        this.mDelayStart = delayStart;
    }

    public boolean isResetOnFinish() {
        return mIsResetOnFinish;
    }

    public void setResetOnFinish(boolean resetOnFinish) {
        this.mIsResetOnFinish = resetOnFinish;
    }

    public int getStep() {
        return mStep;
    }

    public void setStep(int step) {
        this.mStep = step;
    }

    public int getSpeed() {
        return mSpeed;
    }

    public void setSpeed(int speed) {
        this.mSpeed = speed;
    }

    public ScrollStatusListener getScrollStatusListener() {
        return mScrollStatusListener;
    }

    public void setScrollStatusListener(ScrollStatusListener scrollStatusListener) {
        mScrollStatusListener = scrollStatusListener;
    }

    public boolean isScrolling() {
        return mIsScrolling;
    }

    public void reset() {
        mNowPoint = 0;
        resetStatus();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        resetStatus();
    }

    private synchronized void resetStatus() {
        resetTextParams();
        resetThread();
    }

    private synchronized void resetTextParams() {
        int currentTextColor = getCurrentTextColor();
        TextPaint textPaint = getPaint();
        textPaint.setColor(currentTextColor);
        mTextLayout = new StaticLayout(getText(), getPaint(),
                getWidth(), Layout.Alignment.ALIGN_NORMAL,
                getLineSpacingMultiplier(), getLineSpacingExtra(), false);
    }

    private synchronized void resetThread() {
        mMyHeight = getLineHeight() * getLineCount();
        if (mMarqueeRunnable != null && !mMarqueeRunnable.mFinished) {
            mMarqueeRunnable.stop();
            mMarqueeRunnable = null;
        }
        int visibleLineCount = getHeight() / (getLineHeight() - LINE_EXTRA_PADDING);
        Log.i("DDDD", "->: height=" + getHeight() + "  lineHeight=" + getLineHeight() + "  lineCount=" + getLineCount() + "  visible=" + visibleLineCount);
        Log.i("DDDD",  "getTotalPaddingBottom=" + getTotalPaddingBottom() + "  getPaddingBottom=" + getPaddingBottom());
        isScrolled = getTotalPaddingBottom() - getPaddingBottom() <= 0;
//        isScrolled = getLineCount() > visibleLineCount;
        if (isScrolled) {
            mMarqueeRunnable = new MarqueeRunnable(this);
            startMarquee();
        }
    }

    public void stopMarquee() {
        if (mMarqueeRunnable != null && !mMarqueeRunnable.mFinished) {
            mMarqueeRunnable.stop();
            mMarqueeRunnable = null;
        }
    }

    private void startMarquee() {
        if (mMarquee != null) {
            mMarquee.shutdownNow();
        }
        mMarquee = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern(TAG).daemon(true).build());
        mMarquee.schedule(mMarqueeRunnable, 0, TimeUnit.SECONDS);
    }

    public void resetText() {
        mNowPoint = 0;
        postInvalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopMarquee();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isScrolled) {
            canvas.save();
            float textX = 0;
            float textY = mNowPoint;
            canvas.translate(textX, textY);
            if (mTextLayout != null) {
                mTextLayout.draw(canvas);
            }
            canvas.restore();
        } else {
            super.onDraw(canvas);
        }
    }

    public interface ScrollStatusListener {
        void onScrollPrepare();

        void onScrollStart();

        void onScrollStop();

    }

    private static class MarqueeRunnable implements Runnable {
        private volatile boolean mFinished = false;

        private WeakReference<AutoVerticalScrollTextView> mOwner;

        public MarqueeRunnable(AutoVerticalScrollTextView owner) {
            mOwner = new WeakReference<>(owner);
        }

        public void stop() {
            mFinished = true;
        }

        public AutoVerticalScrollTextView getOwner() {
            return mOwner.get();
        }

        @Override
        public void run() {
            AutoVerticalScrollTextView owner = getOwner();
            if (owner == null) {
                return;
            }
            owner.mIsScrolling = true;
            if (owner.mScrollStatusListener != null) {
                owner.mScrollStatusListener.onScrollPrepare();
            }
            try {
                TimeUnit.SECONDS.sleep(owner.mDelayStart);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (owner.mScrollStatusListener != null) {
                owner.mScrollStatusListener.onScrollStart();
            }
            while (!mFinished) {
                if (owner == null) {
                    return;
                }
                if (owner.mStep == 0) {
                    break;
                }
                owner.mNowPoint -= owner.mStep;
                if (owner.mMyHeight != 0 && owner.mNowPoint < -owner.mMyHeight) {
                    if (owner.mIsResetOnFinish) {
                        owner.mNowPoint = 0;
                        owner.postInvalidate();
                    }
                    owner.mIsScrolling = false;
                    if (owner.mScrollStatusListener != null) {
                        owner.mScrollStatusListener.onScrollStop();
                    }
                    owner.reset();
                    break;
                }
                owner.postInvalidate();
                try {
                    Thread.sleep(owner.mSpeed * 50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
