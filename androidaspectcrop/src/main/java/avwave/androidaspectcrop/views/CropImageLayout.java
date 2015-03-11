package avwave.androidaspectcrop.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class CropImageLayout extends RelativeLayout {

    private CropZoomableImageView mZoomImageView;
    private CropImageBorderView mClipImageView;

    public final static int MAX_WIDTH = 2048;

    private float aspectRatio = 1.0f;

    private int mHorizontalPadding = 20;

    public CropImageLayout(Context context) {
        this(context, null);
    }

    public CropImageLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropImageLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mZoomImageView = new CropZoomableImageView(context);
        mClipImageView = new CropImageBorderView(context);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mZoomImageView, params);
        addView(mClipImageView, params);
    }

    public Bitmap clip() {
        return mZoomImageView.clip();
    }

    public void setImageBitmap(Bitmap bitmap) {
        mZoomImageView.setImageBitmap(bitmap);
        mZoomImageView.reLayout();
        mZoomImageView.invalidate();
    }

    public void setCropDimensions(float x, float y) {
        aspectRatio = x/y;
        mClipImageView.setAspectRatio(aspectRatio);
        mClipImageView.invalidate();
        mZoomImageView.setAspectRatio(aspectRatio);
        mZoomImageView.reLayout();
        mZoomImageView.invalidate();
    }

    public void setImagePath(String filePath) {
        Bitmap b = BitmapFactory.decodeFile(filePath);
        setImageBitmap(b);
    }
}