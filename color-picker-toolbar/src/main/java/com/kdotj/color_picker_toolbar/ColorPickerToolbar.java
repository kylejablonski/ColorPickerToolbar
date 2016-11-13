package com.kdotj.color_picker_toolbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Color Toolbar View
 * Created by kyle.jablonski on 11/8/16.
 */

public class ColorPickerToolbar extends View {

    private static final String TAG = ColorPickerToolbar.class.getSimpleName();

    /**
     * Default Indicator Color
     */
    private static final String DEFAULT_INDICATOR_COLOR = "#FFFFFFFF";

    /**
     * Indicator stroke width
     */
    private static final float INDICATOR_STROKE = 4f;

    /**
     * Default Matrix rotation for ColorToolbar
     */
    private static final int MATRIX_ROTATION = 90;

    /**
     * Default Color array for the Toolbar
     */
    private static final int[] COLORS = {
            Color.rgb(0, 0, 0), // black
            Color.rgb(255, 0, 0),  // red
            Color.rgb(255, 165, 0), // orange
            Color.rgb(255, 255, 0), // yellow
            Color.rgb(0, 255, 0), // lime
            Color.rgb(0, 255, 255), // cyan
            Color.rgb(0, 0, 255), // blue
            Color.rgb(75, 0, 130), // indigo
            Color.rgb(238, 130, 238), // violet
            Color.rgb(255, 255, 255) // white
    };

    private static final int INDICATOR_WIDTH = 8;
    private static final int MAX_HEIGHT = 56;
    private static final int MAX_COLOR_VAL = 255;
    private static final int MIN_ALPHA_VAL = 0;

    private static final int DEFAULT_PADDING = 8;

    private int mInternalPadding;

    private float mAlpha = 255f;
    private float mIndicatorX = 0f;
    private float mIndicatorY = 0f;

    private Rect mViewRect = new Rect();
    private Rect mToolbarRect = new Rect();

    private Matrix mMatrix;
    private Shader mShader;

    private Paint mBackgroundPaint;
    private Paint mIndicatorPaint;
    private Paint mToolbarPaint;

    private int mMinimumAlphaValue;
    private int mBackgroundColor;
    private int mIndicatorColor;
    private int[] mColors;
    private int mIndicatorType;
    private int mColorArrayResId;

    private int mSelectedColor;

    private ColorChangeListener mCallback;

    public interface ColorChangeListener {
        void onColorChanged(int color);
    }

    public enum IndicatorType {
        NONE,
        CIRCLE,
        BAR
    }

    public ColorPickerToolbar(Context context) {
        this(context, null);
    }

    public ColorPickerToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPickerToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initWith(context, attrs);
    }

    private void initWith(Context context, AttributeSet attrs) {
        setDrawingCacheEnabled(true);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorPickerToolbar);
        try {

            mMinimumAlphaValue = typedArray.getInt(R.styleable.ColorPickerToolbar_minimumAlphaValue, MIN_ALPHA_VAL);
            mBackgroundColor = typedArray.getColor(R.styleable.ColorPickerToolbar_backgroundColor, Color.TRANSPARENT);
            mIndicatorColor = typedArray.getColor(R.styleable.ColorPickerToolbar_indicatorColor, Color.parseColor(DEFAULT_INDICATOR_COLOR));
            mIndicatorType = typedArray.getInt(R.styleable.ColorPickerToolbar_indicatorType, 0);
            mColorArrayResId = typedArray.getResourceId(R.styleable.ColorPickerToolbar_colors, 0);
            mColors = mColorArrayResId != 0 ? getColorsById(mColorArrayResId) : COLORS;
        } finally {
            typedArray.recycle();
        }

        mInternalPadding = (int) DeviceDimensionHelper.convertDpToPixel(DEFAULT_PADDING, getContext());

        initPainters();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(mViewRect, mBackgroundPaint);

        canvas.drawRect(mToolbarRect, mToolbarPaint);

        switch (mIndicatorType) {
            case 0: // no op

                break;
            case 1:

                canvas.drawCircle(mIndicatorX , mIndicatorY, (int) DeviceDimensionHelper.convertDpToPixel(INDICATOR_WIDTH, getContext()), mIndicatorPaint);
                break;
            case 2:
                float right;
                float left;
                if (mIndicatorX + (int) DeviceDimensionHelper.convertDpToPixel(INDICATOR_WIDTH, getContext()) >= getWidth()) {
                    right = getWidth();
                    left = getWidth() - (int) DeviceDimensionHelper.convertDpToPixel(INDICATOR_WIDTH, getContext());
                } else {
                    left = mIndicatorX;
                    right = left + (int) DeviceDimensionHelper.convertDpToPixel(INDICATOR_WIDTH, getContext());
                }
//                canvas.drawRect(mInternalPadding, mInternalPadding, getWidth() - mInternalPadding, getHeight() - mInternalPadding, mToolbarPaint);
                canvas.drawRect(left, 0, right, getHeight(), mIndicatorPaint);
                break;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int desiredWidth = widthSize;
        int desiredHeight = (int) DeviceDimensionHelper.convertDpToPixel(MAX_HEIGHT, getContext());

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }
        
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mViewRect.left = 0;
        mViewRect.right = w;
        mViewRect.top = 0;
        mViewRect.bottom = h;

        if(mIndicatorType != 0){
            mToolbarRect.left = mInternalPadding;
            mToolbarRect.top = mInternalPadding;
            mToolbarRect.bottom = h - mInternalPadding;
            mToolbarRect.right = w - mInternalPadding;
        }else{
            mToolbarRect.left = mViewRect.left;
            mToolbarRect.right = mViewRect.right;
            mToolbarRect.top = mViewRect.top;
            mToolbarRect.bottom = mViewRect.bottom;
        }

        mIndicatorX = mToolbarRect.left;
        mIndicatorY = mToolbarRect.top;

        mShader = new LinearGradient(0, 0, 0, mToolbarRect.right, mColors, null, Shader.TileMode.MIRROR);
        mShader.setLocalMatrix(mMatrix);
        mToolbarPaint.setShader(mShader);
    }

    public void setColors(int[] colors) {
        mColors = colors;
        invalidate();
    }

    public int[] getColors() {
        return mColors;
    }

    public void setColorArrayResId(int colorArrayResId){
        mColorArrayResId = colorArrayResId;
        mColors = getColorsById(mColorArrayResId);
        invalidate();
    }

    public int getColorArrayResId(){
        return mColorArrayResId;
    }

    public void setIndicatorColor(int indicatorColor) {
        mIndicatorColor = indicatorColor;
        if (mIndicatorPaint != null) {
            mIndicatorPaint.setColor(mIndicatorColor);
        }
        invalidate();
    }

    public int getIndicatorColor() {
        return mIndicatorColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
        if (mBackgroundPaint != null) {
            mBackgroundPaint.setColor(mBackgroundColor);
        }
        invalidate();
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public IndicatorType getIndicatorType() {
        return IndicatorType.values()[mIndicatorType];
    }

    public void setIndicatorType(IndicatorType indicatorType) {
        switch (indicatorType) {
            case CIRCLE:
                mIndicatorType = 1;
                break;
            case BAR:
                mIndicatorType = 2;
                break;
            default:
                mIndicatorType = 0;
                break;
        }
        invalidate();
        requestLayout();
    }

    public void setIndicatorX(float x) {
        mIndicatorX = x;
        invalidate();
    }

    public float getIndicatorX() {
        return mIndicatorX;
    }

    public void setIndicatorY(float y) {
        mIndicatorY = y;
        invalidate();
    }

    public float getIndicatorY() {
        return mIndicatorY;
    }

    public void setSelectedColor(int color) {
        mSelectedColor = color;
        invalidate();
    }

    public int getSelectedColor() {
        return mSelectedColor;
    }

    public void setAlphaValue(float alphaValue) {
        mAlpha = alphaValue;
        invalidate();
    }

    public float getAlphaValue() {
        return mAlpha;
    }

    public void setMinimumAlphaValue(int minimumAlphaValue) {
        mMinimumAlphaValue = minimumAlphaValue;
        invalidate();
    }

    /**
     * Initializes Painter Objects
     */
    private void initPainters() {

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBackgroundPaint.setColor(mBackgroundColor);

        mIndicatorPaint = new Paint();
        if (mIndicatorType == 1) {
            mIndicatorPaint.setStyle(Paint.Style.STROKE);
            mIndicatorPaint.setStrokeWidth(DeviceDimensionHelper.convertDpToPixel(INDICATOR_STROKE, getContext()));
        } else {
            mIndicatorPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        }
        mIndicatorPaint.setColor(mIndicatorColor);

        mToolbarPaint = new Paint();
        mToolbarPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mMatrix = new Matrix();
        mMatrix.setRotate(MATRIX_ROTATION);

        mToolbarPaint.setShader(mShader);
    }

    /**
     * Adds a Listener to the Toolbar to listen for Color changes
     *
     * @param colorChangeListener {@see #ColorChangeListener}
     */
    public void addColorChangeListener(ColorChangeListener colorChangeListener) {
        mCallback = colorChangeListener;
    }

    private int[] getColorsById(int id) {
        if (isInEditMode()) {
            String[] s = getContext().getResources().getStringArray(id);
            int[] colors = new int[s.length];
            for (int j = 0; j < s.length; j++) {
                colors[j] = Color.parseColor(s[j]);
            }
            return colors;
        } else {
            TypedArray typedArray = getContext().getResources().obtainTypedArray(id);
            int[] colors = new int[typedArray.length()];
            for (int j = 0; j < typedArray.length(); j++) {
                colors[j] = typedArray.getColor(j, Color.BLACK);
            }
            typedArray.recycle();
            return colors;
        }
    }

    private void changeColor(float x, float y) {

        float pixelX;
        float pixelY;

        if (y <= mToolbarRect.top) {
            pixelY = mToolbarRect.top;
            mIndicatorY = pixelY ;
            mAlpha = mMinimumAlphaValue;
        } else if (y > mToolbarRect.top && y < mToolbarRect.bottom) {
            pixelY = y;
            mIndicatorY = pixelY;
            mAlpha = Math.max(y, mMinimumAlphaValue);
        }else {
            pixelY = mToolbarRect.bottom - 1;
            mIndicatorY = pixelY;
            mAlpha = MAX_COLOR_VAL;
        }

        if (x <= mToolbarRect.left) {
            pixelX = mToolbarRect.left;
            mIndicatorX = pixelX;
        } else if (x > mToolbarRect.left && x < mToolbarRect.right) {
            pixelX = x;
            mIndicatorX = pixelX;
        } else {
            pixelX = mToolbarRect.right - 1;
            mIndicatorX = pixelX;
        }

        // Set indicator position
//        mIndicatorX = pixelX;
//        mIndicatorY = pixelY;

        // Get the colors
        int pixel = getDrawingCache().getPixel((int) mIndicatorX, (int) mIndicatorY);
        mSelectedColor = Color.argb((int) mAlpha, Color.red(pixel), Color.green(pixel), Color.blue(pixel));
        if (mCallback != null) {
            mCallback.onColorChanged(mSelectedColor);
        }

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {

            case (MotionEvent.ACTION_DOWN):
                Log.d(TAG, "Action was DOWN");
                changeColor(x, y);
                return true;
            case (MotionEvent.ACTION_MOVE):
                Log.d(TAG, "Action was MOVE" + " x= " + x + " y = " + y);
                changeColor(x, y);
                return true;
            case (MotionEvent.ACTION_UP):
                Log.d(TAG, "Action was UP");
                changeColor(x, y);
                return true;
            case (MotionEvent.ACTION_CANCEL):
                Log.d(TAG, "Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE):
                Log.d(TAG, "Movement occurred outside bounds " +
                        "of current screen element");
                return true;
            default:
                return super.onTouchEvent(event);

        }
    }

}
