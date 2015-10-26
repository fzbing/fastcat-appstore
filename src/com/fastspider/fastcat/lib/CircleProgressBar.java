package com.fastspider.fastcat.lib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CircleProgressBar extends View {
	private int maxProgress = 100;
	private int progress = 0;
	private int progressStrokeWidth = 4;
	//��Բ���ڵľ�������
	RectF oval;
	Paint paint;
	public CircleProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO �Զ���ɵĹ��캯����
		oval = new RectF();
		paint = new Paint();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO �Զ���ɵķ������
		super.onDraw(canvas);
		int width = this.getWidth();
		int height = this.getHeight();
		
		if(width!=height)
		{
			int min=Math.min(width, height);
			width=min;
			height=min;
		}
		
		paint.setAntiAlias(true); // ���û���Ϊ�����
		paint.setColor(Color.WHITE); // ���û�����ɫ
		canvas.drawColor(Color.TRANSPARENT); // ��ɫ����
		paint.setStrokeWidth(progressStrokeWidth); //�߿�
		paint.setStyle(Style.STROKE);

		oval.left = progressStrokeWidth / 2; // ���Ͻ�x
		oval.top = progressStrokeWidth / 2; // ���Ͻ�y
		oval.right = width - progressStrokeWidth / 2; // ���½�x
		oval.bottom = height - progressStrokeWidth / 2; // ���½�y

		canvas.drawArc(oval, -90, 360, false, paint); // ���ư�ɫԲȦ�������������
		paint.setColor(Color.rgb(0x57, 0x87, 0xb6));
		canvas.drawArc(oval, -90, ((float) progress / maxProgress) * 360, false, paint); // ���ƽ��Բ������������ɫ
		
		paint.setStrokeWidth(1);
		String text = progress + "%";
		int textHeight = height / 4;
		paint.setTextSize(textHeight);
		int textWidth = (int) paint.measureText(text, 0, text.length());
		paint.setStyle(Style.FILL);
		canvas.drawText(text, width / 2 - textWidth / 2, height / 2 +textHeight/2, paint);

	}
	
	
	
	public int getMaxProgress() {
		return maxProgress;
	}

	public void setMaxProgress(int maxProgress) {
		this.maxProgress = maxProgress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
		this.invalidate();
	}

	/**
	 * �ǣգ��̵߳���
	 */
	public void setProgressNotInUiThread(int progress) {
		this.progress = progress;
		this.postInvalidate();
	}
}
