package com.nutrivda.app.custom.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.nutrivda.app.R;

public class SemicircularGaugeView extends View {

    private int maxValue = 100;
    private int value = 0;
    private int colorMorado;

    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint textPaint;
    private String text = "";

    public SemicircularGaugeView(Context context) {
        super(context);
        init();
    }

    public SemicircularGaugeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.LTGRAY);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(40);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(40);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(64);
        textPaint.setTextAlign(Paint.Align.CENTER);

        colorMorado = ContextCompat.getColor(getContext(), R.color.lila_medio);
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    public void setValue(int value) {
        this.value = value;
        invalidate();
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2 - 50;

        int centerX = width / 2;
        int centerY = height;

        // Dibuja fondo semicircular
        canvas.drawArc(
                centerX - radius,
                centerY - 2 * radius,
                centerX + radius,
                centerY,
                180,
                180,
                false,
                backgroundPaint
        );

        if (value > maxValue) {
            progressPaint.setColor(Color.RED);
        } else {
            progressPaint.setColor(colorMorado);
        }

        // Dibuja progreso
        float sweepAngle = 180f * Math.min(value, maxValue) / maxValue;
        canvas.drawArc(
                centerX - radius,
                centerY - 2 * radius,
                centerX + radius,
                centerY,
                180,
                sweepAngle,
                false,
                progressPaint
        );

        Paint valorPaint = new Paint(textPaint);
        valorPaint.setTextSize(64f);

        Paint labelPaint = new Paint(textPaint);
        labelPaint.setTextSize(34f);

        canvas.drawText(value + " / " + maxValue, centerX, centerY - radius / 2f, valorPaint);
        canvas.drawText(text, centerX, centerY - radius / 2f + 60, labelPaint);
    }
}
