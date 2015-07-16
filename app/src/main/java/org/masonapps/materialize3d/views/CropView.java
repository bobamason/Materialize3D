package org.masonapps.materialize3d.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by ims_2 on 3/4/2015.
 */
public class CropView extends View {

    public static final int TINT_COLOR = 0x66FFFFFF;
    private int STROKE_COLOR = 0xFF3E3E3E;
    private static final int LINE_COLOR = 0x99FFFFFF;
    private static final float STROKE_WIDTH = 2;
    private boolean touched;
    private Paint boxPaint;
    private Bitmap bitmap = null;
    private float width;
    private float height;
    private Rect srcRect;
    private RectF destRect;
    private RectF boxRect;
    private float srcRatio;
    private PointF center;
    private PointF lastTouch;
    private Paint bitmapPaint;
    private Paint darkPaint;
    private boolean isLocked;

    public CropView(Context context) {
        super(context);
        init();
    }

    public CropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        touched = false;
        isLocked = false;
        srcRect = new Rect();
        destRect = new RectF();
        boxRect = new RectF();
        center = new PointF();
        lastTouch = new PointF();
        boxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(2 * STROKE_WIDTH);
        boxPaint.setColor(STROKE_COLOR);
        bitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        darkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        darkPaint.setStyle(Paint.Style.FILL);
        darkPaint.setColor(TINT_COLOR);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, srcRect, destRect, bitmapPaint);
            boxPaint.setStrokeWidth(STROKE_WIDTH);
            boxPaint.setColor(LINE_COLOR);

            canvas.drawLine(boxRect.left + (STROKE_WIDTH * 0.5f), Math.round(boxRect.top + boxRect.height() / 3f), boxRect.right - (STROKE_WIDTH * 0.5f), Math.round(boxRect.top + boxRect.height() / 3f), boxPaint);
            canvas.drawLine(boxRect.left + (STROKE_WIDTH * 0.5f), Math.round(boxRect.bottom - boxRect.height() / 3f), boxRect.right - (STROKE_WIDTH * 0.5f), Math.round(boxRect.bottom - boxRect.height() / 3f), boxPaint);

            canvas.drawLine(Math.round(boxRect.left + boxRect.width() / 3f), boxRect.top + (STROKE_WIDTH * 0.5f), Math.round(boxRect.left + boxRect.width() / 3f), boxRect.bottom - (STROKE_WIDTH * 0.5f), boxPaint);
            canvas.drawLine(Math.round(boxRect.right - boxRect.width() / 3f), boxRect.top + (STROKE_WIDTH * 0.5f), Math.round(boxRect.right - boxRect.width() / 3f), boxRect.bottom - (STROKE_WIDTH * 0.5f), boxPaint);

            if (useVerticalScroll()) {
                canvas.drawRect(destRect.left, destRect.top, boxRect.right, boxRect.top, darkPaint);
                canvas.drawRect(boxRect.left, boxRect.bottom, destRect.right, destRect.bottom, darkPaint);
            } else {
                canvas.drawRect(destRect.left, destRect.top, boxRect.left, boxRect.bottom, darkPaint);
                canvas.drawRect(boxRect.right, boxRect.top, destRect.right, destRect.bottom, darkPaint);
            }
            boxPaint.setStrokeWidth(2 * STROKE_WIDTH);
            boxPaint.setColor(STROKE_COLOR);
//            canvas.drawRect(boxRect.left + STROKE_WIDTH, boxRect.top + STROKE_WIDTH, boxRect.right - STROKE_WIDTH, boxRect.bottom - STROKE_WIDTH, boxPaint);
        }
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        srcRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
        width = getWidth();
        height = getHeight();
        center.set(width * 0.5f, height * 0.5f);
        float boxHalf = width > height ? height * 0.5f : width * 0.5f;
        boxRect.set(center.x - boxHalf + getPaddingLeft(), center.y - boxHalf + getPaddingTop(), center.x + boxHalf - getPaddingRight(), center.y + boxHalf - getPaddingBottom());
        center.set(width * 0.5f, height * 0.5f);
        srcRatio = (float) srcRect.width() / srcRect.height();
        if (useVerticalScroll()) {
            float wScale = boxRect.width() / srcRect.width();
            float hh = srcRect.height() * wScale * 0.5f;
            destRect.set(boxRect.left, center.y - hh, boxRect.right, center.y + hh);
        } else {
            float hScale = boxRect.height() / srcRect.height();
            float hw = srcRect.width() * hScale * 0.5f;
            destRect.set(center.x - hw, boxRect.top, center.x + hw, boxRect.bottom);
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isLocked && bitmap != null && destRect.contains(event.getX(), event.getY())) {
                    touched = true;
                    lastTouch.set(event.getX(), event.getY());
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (!isLocked && touched && bitmap != null) {
                    if (useVerticalScroll()) {
                        destRect.offset(0f, event.getY() - lastTouch.y);
                        if (destRect.top > boxRect.top) {
                            destRect.offset(0f, boxRect.top - destRect.top);
                        } else if (destRect.bottom < boxRect.bottom) {
                            destRect.offset(0f, boxRect.bottom - destRect.bottom);
                        }
                    } else {
                        destRect.offset(event.getX() - lastTouch.x, 0f);
                        if (destRect.left > boxRect.left) {
                            destRect.offset(boxRect.left - destRect.left, 0f);
                        } else if (destRect.right < boxRect.right) {
                            destRect.offset(boxRect.right - destRect.right, 0f);
                        }
                    }
                    lastTouch.set(event.getX(), event.getY());
                    invalidate();
                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touched = false;
                return false;
            default:
                return super.onTouchEvent(event);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    public void setLocked(boolean locked) {
        this.isLocked = locked;
    }

    private boolean useVerticalScroll() {
        if (bitmap != null) {
            return srcRect.height() > srcRect.width();
        }
        return false;
    }


    public RectF getCropDimens() {
        // RectF to draw bitmap relative to 1 x 1 Canvas... must be scaled
        if (bitmap != null) {
            final float size = boxRect.width();
            final float left = (destRect.left - boxRect.left) / size;
            final float top = (destRect.top - boxRect.top) / size;
            final float right = (destRect.right - boxRect.left) / size;
            final float bottom = (destRect.bottom - boxRect.top) / size;
            return new RectF(left, top, right, bottom);
        } else {
            return null;
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
