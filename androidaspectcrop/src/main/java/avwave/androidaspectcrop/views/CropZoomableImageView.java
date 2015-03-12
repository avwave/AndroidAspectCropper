package avwave.androidaspectcrop.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import avwave.androidaspectcrop.utils.DecodeUtils;
import avwave.androidaspectcrop.utils.PrintUtils;

public class CropZoomableImageView extends ImageView implements ScaleGestureDetector.OnScaleGestureListener,
        View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener {

    private float SCALE_MAX = 4.0f;

    private float initScale = 1.0f;

    private final float[] matrixValues = new float[9];

    private boolean once = true;

    private ScaleGestureDetector mScaleGestureDetector = null;

    private final Matrix mScaleMatrix = new Matrix();

    private GestureDetector mGestureDetector;

    private int mHorizontalPadding = 20;

    private float aspectRatio = 20.0f / 30.0f;

    private int initBitmapWidth, initBitmapHeight;

    private Uri srcBitmapUri;
    private PrintUtils printUtil;


    public void setInitBitmapWidth(int initBitmapWidth) {
        this.initBitmapWidth = initBitmapWidth;
    }

    public void setInitBitmapHeight(int initBitmapHeight) {
        this.initBitmapHeight = initBitmapHeight;
    }

    public CropZoomableImageView(Context context) {
        this(context, null);
    }

    public CropZoomableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropZoomableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setScaleType(ScaleType.MATRIX);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        setOnTouchListener(this);

        mHorizontalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mHorizontalPadding, getResources().getDisplayMetrics());

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener());
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public void setSrcBitmap(Uri srcBitmapUri, int dpi, int targetWidth, int targetHeight) {

        this.srcBitmapUri = srcBitmapUri;
        printUtil = new PrintUtils(getContext(), dpi, targetWidth, targetHeight, srcBitmapUri);
    }

    public void reLayout() {
        once = true;
    }

    @Override
    public void onGlobalLayout() {
        if (once) {
            Drawable d = getDrawable();
            if (d == null) {
                return;
            }
            int width = getWidth();
            int height = getHeight();

            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();

            float scale = 1.0f;

            scale = Math.max(width * 1.0f / dw, width * 1.0f / dh);

            initScale = scale;
            SCALE_MAX = initScale * 4;

            mScaleMatrix.postTranslate((width - dw) / 2, (height - dh) / 2);
            mScaleMatrix.postScale(scale, scale, getWidth() / 2, getHeight() / 2);
            setImageMatrix(mScaleMatrix);
            once = false;

        }
    }

    public void setSCALE_MAX(float SCALE_MAX) {
        this.SCALE_MAX = SCALE_MAX;
    }

    public Bitmap clip() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);

        Rect checkRect = new Rect();
        getBoundingBoxRect().round(checkRect);

        return Bitmap.createBitmap(bitmap, checkRect.left, checkRect.top, checkRect.width(), checkRect.height());

    }

    public Bitmap clipOriginalImageAtURI(Uri photoURI) {
        Bitmap largeBitmap = DecodeUtils.decode(getContext(), photoURI, -1, -1);

        float scale = (float) largeBitmap.getHeight() / (float) initBitmapHeight;
        Rect cropRect = getFinalCropRect(scale);

        Log.i("LTRB:", cropRect.flattenToString() + " : " + cropRect.width() + "x" + cropRect.height());

        return Bitmap.createBitmap(largeBitmap, cropRect.left, cropRect.top, cropRect.width(), cropRect.height());

    }

    private Rect getFinalCropRect(float scale) {
        RectF checkRect = getBoundingBoxRect();
        RectF scaledRect = new RectF();

        Matrix imageScaleMatrix = new Matrix(mScaleMatrix);

        imageScaleMatrix.invert(imageScaleMatrix);
        imageScaleMatrix.postScale(scale, scale);

        imageScaleMatrix.mapRect(scaledRect, checkRect);

        Rect cropRect = new Rect();
        scaledRect.round(cropRect);
        return cropRect;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float bitmapScale = (float) printUtil.getSrcBitmapWidth() / (float) initBitmapWidth;
        Rect cropRect = getFinalCropRect(bitmapScale);

        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();

        if (getDrawable() == null) {
            return true;
        }

        SCALE_MAX = printUtil.getMaxScale();

        if (scaleFactor * scale < initScale) {
            scaleFactor = initScale / scale;
        }

//        if (scaleFactor * scale > SCALE_MAX) {
//            scaleFactor = SCALE_MAX / scale;
//        }

        if (scaleFactor > 1 && !printUtil.isRectBigEnough(cropRect)) {
            Log.i("SCALEFINAL", String.valueOf(scale));
            scaleFactor = 1.0f;
        } else {
            mScaleMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            checkBorderAndCenterWhenScale();
        }


        Log.i("IMSCL", "source(" + initBitmapWidth + ", " + initBitmapHeight + ")");



        setImageMatrix(mScaleMatrix);

        invalidate();
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        onScale(detector);
    }

    private float mLastX, mLastY;
    private int mLastPointerCount;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        float x = 0, y = 0;

        final int pointerCount = event.getPointerCount();

        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);

        }

        x = x / pointerCount;
        y = y / pointerCount;

        if (pointerCount != mLastPointerCount) {
            mLastX = x;
            mLastY = y;
        }
        mLastPointerCount = pointerCount;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - mLastX;
                float dy = y - mLastY;

                if (getDrawable() != null) {
                    mScaleMatrix.postTranslate(dx, dy);
                    checkMatrixBounds();
                    setImageMatrix(mScaleMatrix);
                }

                mLastY = y;
                mLastX = x;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastPointerCount = 0;
                break;

        }

        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    public final float getScale() {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    private void checkBorderAndCenterWhenScale() {
        RectF rect = getMatrixRectF();
        RectF checkRect = getBoundingBoxRect();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        if (rect.width() >= checkRect.width()) {
            if (rect.left > checkRect.left) {
                deltaX = -rect.left + checkRect.left;
            }
            if (rect.right < checkRect.right) {
                deltaX = rect.right - checkRect.right;
            }
        }

        if (rect.height() >= checkRect.height()) {
            if (rect.top > 0) {
                deltaY = -rect.top + checkRect.top;
            }
            if (rect.bottom < checkRect.bottom) {
                deltaY = rect.bottom - checkRect.bottom;
            }
        }

        if (rect.width() < width) {
            deltaX = width * 0.5f - rect.right + 0.5f * rect.width();
        }

        if (rect.height() < height) {
            deltaY = height * 0.5f - rect.bottom + 0.5f * rect.height();
        }

        mScaleMatrix.postTranslate(deltaX, deltaY);


    }

    private RectF getMatrixRectF() {
        Matrix matrix = mScaleMatrix;
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (d != null) {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    private void checkMatrixBounds() {
        RectF rect = getMatrixRectF();
        float deltaX = 0, deltaY = 0;
        final float viewWidth = getWidth();
        final float viewHeight = getHeight();

        RectF checkRect = getBoundingBoxRect();

        if (rect.top > checkRect.top) {
            deltaY = -rect.top + checkRect.top;
        }
        if (rect.bottom < checkRect.bottom) {
            deltaY = viewHeight - rect.bottom - checkRect.top;

        }
        if (rect.left > checkRect.left) {
            deltaX = -rect.left + checkRect.left;
        }
        if (rect.right < checkRect.right) {
            deltaX = viewWidth - rect.right - checkRect.left;
        }
        mScaleMatrix.postTranslate(deltaX, deltaY);
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