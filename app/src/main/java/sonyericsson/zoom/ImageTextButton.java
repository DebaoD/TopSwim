package sonyericsson.zoom;

import debaod.topswim.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.Button;

public class ImageTextButton extends Button {
	private final String namespace = "http://schemas.android.com/apk/res/android";
	private int resourceId = 0;
	private Bitmap bitmap;

	public ImageTextButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setClickable(true);
		resourceId = attrs.getAttributeResourceValue(namespace, "icon",
				R.drawable.fav);
		bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		int x = (this.getMeasuredWidth() - bitmap.getWidth()) >> 1;
		int y =	0;
		canvas.drawBitmap(bitmap, x, y, null);

		canvas.translate(0,
				(this.getMeasuredHeight() >> 1) - (int) this.getTextSize()+2);

		super.onDraw(canvas);
	}

	public void setIcon(Bitmap bitmap) {
		this.bitmap = bitmap;
		invalidate();
	}

	public void setIcon(int resourceId) {
		this.bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
		invalidate();
	}
}
