package avwave.androidaspectcrop.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class CropImageBorderView extends View {

    private float aspectRatio = 20.0f/30.0f;
    private int mHorizontalPadding = 20;
    private int mBorderColor = Color.parseColor("#FFFFFF");
    private int mBorderWidth = 1;
    private Paint mPaint;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mEraserPaint;

    private Rect rect = new Rect();

    public CropImageBorderView(Context context) {
        this(context, null);
    }

    public CropImageBorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropImageBorderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mHorizontalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mHorizontalPadding, getResources().getDisplayMetrics());
        mBorderWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mBorderWidth, getResources().getDisplayMetrics());
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mEraserPaint = new Paint();
        mEraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mEraserPaint.setAntiAlias(true);
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(Color.parseColor("#AA000000"));
        mPaint.setStyle(Paint.Style.FILL);

        mCanvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
        mPaint.setColor(mBorderColor);
        mPaint.setStrokeWidth(mBorderWidth);
        mPaint.setStyle(Paint.Style.STROKE);

        getBoundingBoxRect().roundOut(rect);

        mBitmap.eraseColor(Color.TRANSPARENT);
        mCanvas.drawColor(Color.parseColor("#66ffffff"));
        mCanvas.drawRect(rect, mEraserPaint);
        canvas.drawBitmap(mBitmap, 0, 0, null);

        canvas.drawRect(rect, mPaint);
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        if (xNew != xOld || yNew != yOld){
            mBitmap = Bitmap.createBitmap(xNew, yNew, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }
        super.onSizeChanged(xNew, yNew, xOld, yOld);
    }

    private RectF getBoundingBoxRect() {
        int width = getWidth() - (mHorizontalPadding * 2);
        int heightAspect = (int) Math.floor(width * aspectRatio);

        RectF checkRect = new RectF();
        checkRect.left = mHorizontalPadding;
        checkRect.right = getWidth() - mHorizontalPadding;
        checkRect.top = ((getHeight() - heightAspect) / 2);
        checkRect.bottom = ((getHeight() + heightAspect) / 2);
        return checkRect;
    }
}