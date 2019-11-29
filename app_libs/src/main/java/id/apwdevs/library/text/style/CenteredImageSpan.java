package id.apwdevs.library.text.style;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

public class CenteredImageSpan extends ImageSpan {
    private int initialDescent = 0;
    private int extraSpace = 0;
    private WeakReference<Drawable> mDrawableRef;

    public CenteredImageSpan(final Drawable drawable) {
        this(drawable, DynamicDrawableSpan.ALIGN_BOTTOM);
    }

    public CenteredImageSpan(final Drawable drawable, final int verticalAlignment) {
        super(drawable, verticalAlignment);
    }

    public CenteredImageSpan(final Context ctx, final int resId) {
        //final Drawable dw = ctx.getResources().getDrawable(resId);
        super(ctx, resId);
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        // TODO: Implement this method
        //super.draw(canvas, text, start, end, x, top, y, bottom, paint);
        Drawable b = getCachedDrawable();
        canvas.save();
        int drawableHeight = b.getIntrinsicHeight();
        int fontAscent = paint.getFontMetricsInt().ascent;
        int fontDescent = paint.getFontMetricsInt().descent;
        int transY = bottom - b.getBounds().bottom + (drawableHeight - fontDescent + fontAscent);
        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, FontMetricsInt fm) {
        // TODO: Implement this method
        Drawable d = getCachedDrawable();
        Rect rect = d.getBounds();
        if (fm != null) {
			/*if(rect.bottom - (fm.descent - fm.ascent) >= 0){
				initialDescent = fm.descent;
				extraSpace = rect.bottom - (fm.descent - fm.ascent);
			}
			fm.descent = extraSpace / 2 + initialDescent;
			fm.bottom  = fm.descent;
			fm.ascent  = -rect.bottom + fm.descent;
			fm.top = fm.ascent;*/
            FontMetricsInt pfm = paint.getFontMetricsInt();
            fm.ascent = pfm.ascent;
            fm.descent = pfm.descent;
            fm.top = pfm.top;
            fm.bottom = pfm.bottom;
        }
        return rect.right;
        //return super.getSize(paint, text, start, end, fm);
    }

    private Drawable getCachedDrawable() {
        WeakReference<Drawable> wr = mDrawableRef;
        Drawable d = null;
        if (wr != null) d = wr.get();
        if (d == null) {
            d = getDrawable();
            mDrawableRef = new WeakReference<>(d);
        }
        return d;
    }
}