package org.masonapps.materialize3d.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ColorPickerView extends View
{
    private final Paint selectionPaint;
    private final Paint outlinePaint;
    private final PointF hueSelectionPoint;
    private final PointF colorSelectionPoint;
    private final RectF squareRect;
    private Rect contentRect;

	private float radius;
	
	private float MARGIN;

	private final Paint hueCirclePaint;

	private final Paint squarePaint;
	
	private int[] hueGradColors = new int[360];
    private float[] vertices = new float[8];
    private int[] colors  = {Color.WHITE, Color.BLACK, Color.BLACK, Color.CYAN, 0, 0, 0, 0};
    private final short[] indices = { 0, 2, 3, 0, 1, 2};
    private float[] currentHSVcolor = new float[] {0f, 1f, 1f};
    private float currentAngle = 0;
    private float squareRadius;
    private boolean squareTouched = false;
    private boolean hueCircleTouched = false;
    private float centerX;
    private float centerY;
    private LinearGradient valShader;
    private float[] tempHSV = new float[] {0f, 1f, 1f};
    private OnColorChangeListener mListener = null;
    private int currentColor;
    private boolean colorSet = false;

    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        MARGIN = getResources().getDisplayMetrics().density * 6f;
		contentRect = new Rect();
        squareRect = new RectF();
        hueSelectionPoint = new PointF();
        colorSelectionPoint = new PointF();

        selectionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectionPaint.setStyle(Paint.Style.FILL);

        outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setColor(Color.DKGRAY);

		hueCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		hueCirclePaint.setStyle(Paint.Style.STROKE);
        hueCirclePaint.setDither(true);
		
		squarePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        squarePaint.setStyle(Paint.Style.FILL);
        squarePaint.setDither(true);

		for(int i = 0; i < hueGradColors.length; i++){
			currentHSVcolor[0] = i;
			hueGradColors[i] = Color.HSVToColor(currentHSVcolor);
		}
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
        squareTouched = false;
        hueCircleTouched = false;
		contentRect.set(getPaddingLeft(), getPaddingTop(), w - getPaddingRight(), h - getPaddingBottom());
		radius = (contentRect.width() > contentRect.height() ? contentRect.height() : contentRect.width()) * 0.5f - 2 * MARGIN;
        centerX = contentRect.centerX();
        centerY = contentRect.centerY();
        hueCirclePaint.setShader(new SweepGradient(centerX, centerY, hueGradColors, null));
		hueCirclePaint.setStrokeWidth(radius * 0.15f);
        outlinePaint.setStrokeWidth(MARGIN * 0.2f);
        squareRadius = (radius - hueCirclePaint.getStrokeWidth() - MARGIN * 3f) / (float) Math.sqrt(2d);
        vertices[0] = centerX - squareRadius;
        vertices[1] = centerY - squareRadius;

        vertices[2] = centerX - squareRadius;
        vertices[3] = centerY + squareRadius;

        vertices[4] = centerX + squareRadius;
        vertices[5] = centerY + squareRadius;

        vertices[6] = centerX + squareRadius;
        vertices[7] = centerY - squareRadius;
        squareRect.set(vertices[0], vertices[1], vertices[4], vertices[5]);
        valShader = new LinearGradient(squareRect.left, squareRect.top, squareRect.left, squareRect.bottom, Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
        if(colorSet) {
            Color.colorToHSV(currentColor, currentHSVcolor);
            currentAngle = currentHSVcolor[0];
            final float sx = currentHSVcolor[1] * (squareRect.right - squareRect.left) + squareRect.left;
            final float sy = (1f - currentHSVcolor[2]) * (squareRect.bottom - squareRect.top) + squareRect.top;
            colorSelectionPoint.set(sx, sy);
            colorSet = false;
        }

        tempHSV[0] = currentHSVcolor[0];
        tempHSV[1] = 1f;
        tempHSV[2] = 1f;
        final int hueColor = Color.HSVToColor(tempHSV);

        outlinePaint.setColor(Color.DKGRAY);
		canvas.drawCircle(contentRect.centerX(), contentRect.centerY(), radius - hueCirclePaint.getStrokeWidth() * 0.5f, hueCirclePaint);
		canvas.drawCircle(contentRect.centerX(), contentRect.centerY(), radius - hueCirclePaint.getStrokeWidth(), outlinePaint);
		canvas.drawCircle(contentRect.centerX(), contentRect.centerY(), radius, outlinePaint);

        final LinearGradient satShader = new LinearGradient(squareRect.left, squareRect.top, squareRect.right, squareRect.top, Color.WHITE, hueColor, Shader.TileMode.CLAMP);
        final ComposeShader shader = new ComposeShader(valShader, satShader, PorterDuff.Mode.MULTIPLY);
        squarePaint.setShader(shader);
        canvas.drawRect(squareRect, squarePaint);
        canvas.drawRect(squareRect, outlinePaint);

        hueSelectionPoint.x = (float)Math.cos(Math.toRadians(currentAngle)) * (radius - hueCirclePaint.getStrokeWidth() * 0.5f) + centerX;
        hueSelectionPoint.y = (float)Math.sin(Math.toRadians(currentAngle)) * (radius - hueCirclePaint.getStrokeWidth() * 0.5f) + centerY;
        selectionPaint.setColor(hueColor);
        canvas.drawCircle(hueSelectionPoint.x, hueSelectionPoint.y , hueCirclePaint.getStrokeWidth() * 0.5f + MARGIN, selectionPaint);
        canvas.drawCircle(hueSelectionPoint.x, hueSelectionPoint.y , hueCirclePaint.getStrokeWidth() * 0.5f - outlinePaint.getStrokeWidth() * 0.5f + MARGIN, outlinePaint);

        outlinePaint.setColor(Color.GRAY);
        selectionPaint.setColor(Color.HSVToColor(currentHSVcolor));
        canvas.drawCircle(colorSelectionPoint.x, colorSelectionPoint.y , 2 * MARGIN, selectionPaint);
        canvas.drawCircle(colorSelectionPoint.x, colorSelectionPoint.y , 2 * MARGIN - outlinePaint.getStrokeWidth() * 0.5f, outlinePaint);

	}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                final float subx = x - contentRect.centerX();
                final float suby = y - contentRect.centerY();
                final float d = (float)Math.sqrt(subx * subx + suby * suby);
                if(d <=  radius - hueCirclePaint.getStrokeWidth() - MARGIN * 2f){
                    squareTouched = true;
                }else if(d <= radius + MARGIN){
                    hueCircleTouched = true;
                }
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                if(squareTouched){
                    if(x < squareRect.left) x = squareRect.left;
                    if(x > squareRect.right) x = squareRect.right;
                    if(y < squareRect.top) y = squareRect.top;
                    if(y > squareRect.bottom) y = squareRect.bottom;
                    colorSelectionPoint.set(x, y);
                    currentHSVcolor[1] = (x - squareRect.left) / (squareRect.right - squareRect.left);
                    currentHSVcolor[1] = Math.min(1f, Math.max(0f, currentHSVcolor[1]));

                    currentHSVcolor[2] = 1f - ((y - squareRect.top) / (squareRect.bottom - squareRect.top));
                    currentHSVcolor[2] = Math.min(1f, Math.max(0f, currentHSVcolor[2]));
                    if(mListener != null)mListener.onColorChanged(Color.HSVToColor(currentHSVcolor));
                }else if(hueCircleTouched){
                    currentAngle = ((float) Math.toDegrees(Math.atan2(y - contentRect.centerY(), x - contentRect.centerX())) + 360) % 360f;
                    currentHSVcolor[0] = currentAngle;
                    if(mListener != null)mListener.onColorChanged(Color.HSVToColor(currentHSVcolor));

                }
                invalidate();
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if(squareTouched || hueCircleTouched){
                    hueCircleTouched = false;
                    squareTouched = false;
                }
                invalidate();
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    public static int[] colorToRGB(int c){
        return new int[]{c >> 16 & 0xFF, c >> 8 & 0xFF, c & 0xFF};
    }

    public static interface OnColorChangeListener{
        public void onColorChanged(int color);
    }

    public void setCurrentColor(int color) {
        currentColor = color;
        colorSet = true;
        invalidate();
    }

    public void setListener(OnColorChangeListener mListener) {
        this.mListener = mListener;
    }
}
