package avwave.androidaspectcrop.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by rayarvin on 3/12/15.
 */
public class PrintUtils {
    private int dpi;
    private int targetWidth;
    private int targetHeight;

    private int srcBitmapWidth, srcBitmapHeight;

    public PrintUtils(Context context, int dpi, int tWidth, int tHeight, Uri photoUri) {
        this.dpi = dpi;
        this.targetWidth = tWidth * dpi;
        this.targetHeight = tHeight * dpi;
        InputStream input;

        try {
            input = context.getContentResolver().openInputStream(photoUri);

            int orientation = ExifUtils.getExifOrientation(context, photoUri);

            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bitmapOptions);

            if (orientation == 0 || orientation == 180) {
                srcBitmapWidth = bitmapOptions.outWidth;
                srcBitmapHeight = bitmapOptions.outHeight;
            } else {
                srcBitmapWidth = bitmapOptions.outHeight;
                srcBitmapHeight = bitmapOptions.outWidth;
            }
            input.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public int getSrcBitmapWidth() {
        return srcBitmapWidth;
    }

    public int getSrcBitmapHeight() {
        return srcBitmapHeight;
    }

    public boolean isRectBigEnough(Rect rect) {
        Log.i("ISRECTBIGENUF", "croprect(" + rect.width() + ", " + rect.height() + "), target(" + targetWidth + ", " + targetHeight + "), source(" + srcBitmapWidth + ", " +srcBitmapHeight + ")");
        if (rect.width() > targetWidth && rect.height() > targetHeight) {
            return true;
        }
        return false;
    }

    public float getMaxScale() {
        return Math.max((float)srcBitmapWidth / (float)targetWidth, (float)srcBitmapHeight/(float)targetHeight);
    }

}
