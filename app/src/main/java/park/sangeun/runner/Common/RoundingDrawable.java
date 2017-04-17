package park.sangeun.runner.Common;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 * Created by user on 2017-04-07.
 */

public class RoundingDrawable extends Drawable {
    private Bitmap bitmap;
    private Paint paint;
    private RectF rectF;
    private int bitmapWidth;
    private int bitmapHeight;

    public RoundingDrawable(Bitmap bitmap){
        this.bitmap = bitmap;
        this.rectF = new RectF();
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setDither(true);

        final BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        this.paint.setShader(shader);

        this.bitmapWidth = this.bitmap.getWidth();
        this.bitmapHeight = this.bitmap.getHeight();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        rectF.set(bounds);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawOval(rectF, paint);
    }

    @Override
    public int getIntrinsicWidth() {
        return bitmapWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return bitmapHeight;
    }

    public void setAntiAlias(boolean aa) {
        paint.setAntiAlias(aa);
        invalidateSelf();
    }

    @Override
    public void setFilterBitmap(boolean filter) {
        paint.setFilterBitmap(filter);
        invalidateSelf();
    }

    @Override
    public void setAlpha(int i) {
        if (paint.getAlpha() != i) {
            paint.setAlpha(i);
            invalidateSelf();
        }
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setDither(boolean dither) {
        paint.setDither(dither);
        invalidateSelf();
    }

    public Bitmap getBitmap(){
        return bitmap;
    }
}
