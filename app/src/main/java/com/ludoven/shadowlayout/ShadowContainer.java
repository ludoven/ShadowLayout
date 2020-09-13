package com.ludoven.shadowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;


public class ShadowContainer extends ViewGroup {
    private final float deltaLength;
    private final float cornerRadius;
    private final Paint mShadowPaint;
    private final Paint mShadowPaint1;
    private boolean drawShadow;

    public ShadowContainer(Context context) {
        this(context, null);
    }

    public ShadowContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShadowContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setWillNotDraw(false);
        setBackgroundColor(Color.parseColor("#ffffff"));
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShadowContainer);
        int shadowColor = a.getColor(R.styleable.ShadowContainer_containerShadowColor, Color.RED);
        float shadowRadius = a.getDimension(R.styleable.ShadowContainer_containerShadowRadius, 0);
        deltaLength = a.getDimension(R.styleable.ShadowContainer_containerDeltaLength, 0);
        cornerRadius = a.getDimension(R.styleable.ShadowContainer_containerCornerRadius, 0);
        float dx = a.getDimension(R.styleable.ShadowContainer_deltaX, 0);
        float dy = a.getDimension(R.styleable.ShadowContainer_deltaY, 0);
        drawShadow = a.getBoolean(R.styleable.ShadowContainer_enable, true);
        a.recycle();
        mShadowPaint = new Paint();
        mShadowPaint.setStyle(Paint.Style.FILL);
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setColor(shadowColor);

       mShadowPaint.setShadowLayer(shadowRadius, dx, dy, Color.BLUE);

        mShadowPaint1 = new Paint();
        mShadowPaint1.setStyle(Paint.Style.FILL);
        mShadowPaint1.setAntiAlias(true);
        mShadowPaint1.setColor(shadowColor);
        mShadowPaint1.setShadowLayer(shadowRadius, dx, dy, shadowColor);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        if (drawShadow) {
            if (getLayerType() != LAYER_TYPE_SOFTWARE) {
                setLayerType(LAYER_TYPE_SOFTWARE, null);
            }
            View child = getChildAt(0);
            int left = child.getLeft();
            int top = child.getTop();
            int right = child.getRight();
            int bottom = child.getBottom();

            Path drawablePath = new Path();
            drawablePath.arcTo(new RectF(left, top, left + 2 * cornerRadius, top + 2 * cornerRadius), -90, -90, false);
            drawablePath.lineTo(left, bottom-cornerRadius );
            drawablePath.lineTo(right-cornerRadius, top );
            drawablePath.close();
            canvas.drawPath(drawablePath, mShadowPaint);


            Path drawablePath2 = new Path();
            drawablePath2.arcTo(new RectF(left+ cornerRadius , bottom , left+ 2 * cornerRadius , bottom), 180, -90, false);
            drawablePath2.lineTo(right - cornerRadius, bottom);
            drawablePath2.arcTo(new RectF(right - 2 * cornerRadius, bottom - 2 * cornerRadius, right, bottom), 90, -90, false);
            drawablePath2.lineTo(right, top  + cornerRadius);
            drawablePath2.close();
            canvas.drawPath(drawablePath2, mShadowPaint1);
        }
        super.dispatchDraw(canvas);
    }

    public void setDrawShadow(boolean drawShadow){
        if (this.drawShadow == drawShadow){
            return;
        }
        this.drawShadow = drawShadow;
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() != 1) {
            throw new IllegalStateException("子View只能有一个");
        }
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        View child = getChildAt(0);
        LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
        int childBottomMargin = (int) (Math.max(deltaLength, layoutParams.bottomMargin) + 1);
        int childLeftMargin = (int) (Math.max(deltaLength, layoutParams.leftMargin) + 1);
        int childRightMargin = (int) (Math.max(deltaLength, layoutParams.rightMargin) + 1);
        int childTopMargin = (int) (Math.max(deltaLength, layoutParams.topMargin) + 1);
        int widthMeasureSpecMode;
        int widthMeasureSpecSize;
        int heightMeasureSpecMode;
        int heightMeasureSpecSize;
        if (widthMode == MeasureSpec.UNSPECIFIED){
            widthMeasureSpecMode = MeasureSpec.UNSPECIFIED;
            widthMeasureSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        }else {
            if (layoutParams.width == LayoutParams.MATCH_PARENT) {
                widthMeasureSpecMode = MeasureSpec.EXACTLY;
                widthMeasureSpecSize = measuredWidth - childLeftMargin - childRightMargin;
            } else if (LayoutParams.WRAP_CONTENT == layoutParams.width) {
                widthMeasureSpecMode = MeasureSpec.AT_MOST;
                widthMeasureSpecSize = measuredWidth - childLeftMargin - childRightMargin;
            } else {
                widthMeasureSpecMode = MeasureSpec.EXACTLY;
                widthMeasureSpecSize = layoutParams.width;
            }
        }
        if (heightMode == MeasureSpec.UNSPECIFIED){
            heightMeasureSpecMode = MeasureSpec.UNSPECIFIED;
            heightMeasureSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        }else {
            if (layoutParams.height == LayoutParams.MATCH_PARENT) {
                heightMeasureSpecMode = MeasureSpec.EXACTLY;
                heightMeasureSpecSize = measuredHeight - childBottomMargin - childTopMargin;
            } else if (LayoutParams.WRAP_CONTENT == layoutParams.height) {
                heightMeasureSpecMode = MeasureSpec.AT_MOST;
                heightMeasureSpecSize = measuredHeight - childBottomMargin - childTopMargin;
            } else {
                heightMeasureSpecMode = MeasureSpec.EXACTLY;
                heightMeasureSpecSize = layoutParams.height;
            }
        }
        measureChild(child, MeasureSpec.makeMeasureSpec(widthMeasureSpecSize, widthMeasureSpecMode), MeasureSpec.makeMeasureSpec(heightMeasureSpecSize, heightMeasureSpecMode));
        int parentWidthMeasureSpec = MeasureSpec.getMode(widthMeasureSpec);
        int parentHeightMeasureSpec = MeasureSpec.getMode(heightMeasureSpec);
        int height = measuredHeight;
        int width = measuredWidth;
        int childHeight = child.getMeasuredHeight();
        int childWidth = child.getMeasuredWidth();
        if (parentHeightMeasureSpec == MeasureSpec.AT_MOST){
            height = childHeight + childTopMargin + childBottomMargin;
        }
        if (parentWidthMeasureSpec == MeasureSpec.AT_MOST){
            width = childWidth + childRightMargin + childLeftMargin;
        }
        if (width < childWidth + 2 * deltaLength){
            width = (int) (childWidth + 2 * deltaLength);
        }
        if (height < childHeight + 2 * deltaLength){
            height = (int) (childHeight + 2 * deltaLength);
        }

        if (height != measuredHeight || width != measuredWidth){
            setMeasuredDimension(width, height);
        }

    }

    static class LayoutParams extends MarginLayoutParams{

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View child = getChildAt(0);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int childMeasureWidth = child.getMeasuredWidth();
        int childMeasureHeight = child.getMeasuredHeight();
        child.layout((measuredWidth - childMeasureWidth) / 2, (measuredHeight - childMeasureHeight) / 2, (measuredWidth + childMeasureWidth) / 2, (measuredHeight + childMeasureHeight) / 2);
    }
}