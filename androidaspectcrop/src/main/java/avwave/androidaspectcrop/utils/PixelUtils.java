package avwave.androidaspectcrop.utils;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by rayarvin on 3/10/15.
 */
public class PixelUtils {
    public static float dpToRenderedPixels(float dp) {
        Resources r = Resources.getSystem();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}