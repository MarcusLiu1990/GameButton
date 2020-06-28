package com.mgcoco.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GameButtonView extends View {

    private int mWidth, mHeight;
    private int mSize;
    private int mCenterX, mCenterY;
    private SweepGradient mSweepGradient;


    private Matrix mMatrix = new Matrix();

    private final static float DISTANCE_DIVIDED_SIZE = 100;
    private float mGapDistance = 0f;
    private long mFrequency = 1000;

    private int mStroke = 10;
    private Paint mGradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mClearPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private List<Float> mAngle = new ArrayList<>();
    private int mCurrentPointIndex = 0;

    private RectF mCenterRect;

    private float mRoundedRadius = 10f;

    private int mStartColor = Color.RED;

    private int mEndColor  = Color.WHITE;

    private Drawable mStartDrawable;

    private Path mCornerLeftTop = new Path();
    private Path mCornerLeftBottom = new Path();
    private Path mCornerRightTop = new Path();
    private Path mCornerRightBottom = new Path();

    public GameButtonView(Context context) {
        super(context);
        init(context, null);
    }

    public GameButtonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GameButtonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public GameButtonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs){
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.GameButton);

        if(ta != null) {
            mRoundedRadius = ta.getDimensionPixelSize(R.styleable.GameButton_cornerRadius, (int)convertDpToPixel(5, context));

            mStroke = ta.getDimensionPixelSize(R.styleable.GameButton_stroke, (int)convertDpToPixel(5, context));

            mStartDrawable = ta.getDrawable(R.styleable.GameButton_startDrawable);

            mStartColor = ta.getColor(R.styleable.GameButton_startColor, Color.parseColor("#B79771"));

            mEndColor = ta.getColor(R.styleable.GameButton_endColor, Color.WHITE);

            mFrequency = ta.getInteger(R.styleable.GameButton_frequency, 1000);
        }

        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mClearPaint.setAntiAlias(true);
        mClearPaint.setStyle(Paint.Style.FILL);
        mClearPaint.setColor(Color.TRANSPARENT);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private void initPath(){
        mSize = (int)Math.sqrt((mWidth * mWidth) + (mHeight * mHeight));
        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;
        //固定移動距離
        mGapDistance = ((mWidth * 2) + (mHeight * 2)) / DISTANCE_DIVIDED_SIZE;

        mCenterRect = new RectF(mStroke, mStroke, mWidth - mStroke, mHeight - mStroke);

        List<Point> pointList = new ArrayList<>();
        pointList.clear();

        for(int i = 0; i < (float)(mHeight  / 2) / mGapDistance; i ++) {
            pointList.add(new Point(mCenterX, (int)(i * mGapDistance)));
        }

        for(int i = (int)((float)(mWidth / 2) / mGapDistance); i >= 0; i--) {
            pointList.add(new Point((int)(i * mGapDistance), mCenterY));
        }

        for(int i = (int)((float)(mWidth / 2) / mGapDistance); i > 0; i--) {
            pointList.add(new Point((int)(-mCenterX + (i * mGapDistance)), mCenterY));
        }

        for(int i = (int)((float)(mHeight / 2) / mGapDistance); i >= 0; i --) {
            pointList.add(new Point(-mCenterX, (int)(i * mGapDistance)));
        }

        for(int i = (int)((float)(mHeight / 2) / mGapDistance); i > 0; i --) {
            pointList.add(new Point(-mCenterX, (int)(-mCenterY + (i * mGapDistance))));
        }

        for(int i = 0; i < (float)(mWidth / 2) / mGapDistance; i ++) {
            pointList.add(new Point((int)(-mCenterX + (i * mGapDistance)), -mCenterY));
        }

        for(int i = 1; i < (float)(mWidth / 2) / mGapDistance; i ++) {
            pointList.add(new Point((int)(i * mGapDistance), -mCenterY));
        }

        for(int i = 1; i < (float)(mHeight  / 2) / mGapDistance; i ++) {
            pointList.add(new Point(mCenterX, (int)(-mCenterY + (i * mGapDistance))));
        }

        float degree = 0;
        for(int i = 0; i < pointList.size(); i++) {
            Point currentPoint = pointList.get(i);
            Point nextPoint;
            if(i + 1 < pointList.size()) nextPoint = pointList.get(i + 1);
            else nextPoint = pointList.get(0);

            double c = mGapDistance;
            double a = Math.sqrt((currentPoint.x * currentPoint.x) + (currentPoint.y * currentPoint.y));
            double b = Math.sqrt((nextPoint.x * nextPoint.x) + (nextPoint.y * nextPoint.y));

            float angle = (float)Math.acos((a * a + b * b - c * c) / (2 * a * b));
            float angleDegree = (float) (angle * 180.0d / Math.PI);
            degree += angleDegree;
            mAngle.add(degree);
        }

        mSweepGradient = new SweepGradient(mCenterX, mCenterY, new int[]{ mStartColor, mEndColor }, new float[]{ 0.0f, 1.0f });
        mGradientPaint.setShader(mSweepGradient);
        initRoundedCornerPath();
    }

    private void initRoundedCornerPath(){
        mCornerLeftTop.moveTo(0, mRoundedRadius);
        mCornerLeftTop.quadTo(0,0, mRoundedRadius, 0);
        mCornerLeftTop.lineTo(0, 0);
        mCornerLeftTop.lineTo(0, mRoundedRadius);
        mCornerLeftTop.close();

        mCornerRightBottom.moveTo(mWidth - mRoundedRadius, 0);
        mCornerRightBottom.quadTo(mWidth,0, mWidth, mRoundedRadius);
        mCornerRightBottom.lineTo(mWidth, 0);
        mCornerRightBottom.lineTo(mWidth - mRoundedRadius, 0);
        mCornerRightBottom.close();

        mCornerRightTop.moveTo(mWidth, mHeight - mRoundedRadius);
        mCornerRightTop.quadTo(mWidth, mHeight, mWidth - mRoundedRadius, mHeight);
        mCornerRightTop.lineTo(mWidth, mHeight);
        mCornerRightTop.lineTo(mWidth, mHeight - mRoundedRadius);
        mCornerRightTop.close();

        mCornerLeftBottom.moveTo(0, mHeight - mRoundedRadius);
        mCornerLeftBottom.quadTo(0, mHeight, mRoundedRadius, mHeight);
        mCornerLeftBottom.lineTo(0, mHeight);
        mCornerLeftBottom.lineTo(0, mHeight - mRoundedRadius);
        mCornerLeftBottom.close();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        initPath();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if(visibility == VISIBLE) {
            mHandler.post(mRunnable);
        }
        else{
            mHandler.removeCallbacks(mRunnable);
        }
    }

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if(mCurrentPointIndex >= mAngle.size())  mCurrentPointIndex = 0;

            mMatrix.setRotate(mAngle.get(mCurrentPointIndex), mCenterX, mCenterY);
            mSweepGradient.setLocalMatrix(mMatrix);
            mHandler.postDelayed(this, (long) (mFrequency / DISTANCE_DIVIDED_SIZE));
            mCurrentPointIndex ++;
            invalidate();
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mGradientPaint != null && mCenterX > 0) {
            canvas.drawCircle(mCenterX, mCenterY, mSize, mGradientPaint);
            canvas.drawRoundRect(mCenterRect, mRoundedRadius, mRoundedRadius, mClearPaint);

            canvas.drawPath(mCornerLeftTop, mClearPaint);
            canvas.drawPath(mCornerLeftBottom, mClearPaint);
            canvas.drawPath(mCornerRightTop, mClearPaint);
            canvas.drawPath(mCornerRightBottom, mClearPaint);
        }
    }

    public static float convertDpToPixel(float dp, Context context){
        float px = dp * getDensity(context);
        return px;
    }

    public static float getDensity(Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.density;
    }
}
