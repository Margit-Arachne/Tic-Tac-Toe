package com.example.tic_tac_toe;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class VictoryLineView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private float progress;
    private boolean drawLine;
    private ValueAnimator animator;

    public VictoryLineView(Context context) {
        super(context);
        init();
    }

    public VictoryLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VictoryLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        float strokeWidth = getResources().getDisplayMetrics().density * 6f;
        paint.setColor(Color.RED);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        setVisibility(GONE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!drawLine) {
            return;
        }
        float currentX = startX + (endX - startX) * progress;
        float currentY = startY + (endY - startY) * progress;
        canvas.drawLine(startX, startY, currentX, currentY, paint);
    }

    public void showLine(int startRow, int startCol, int endRow, int endCol) {
        if (getWidth() == 0 || getHeight() == 0) {
            post(() -> showLine(startRow, startCol, endRow, endCol));
            return;
        }

        float cellWidth = getWidth() / 3f;
        float cellHeight = getHeight() / 3f;

        startX = (startCol + 0.5f) * cellWidth;
        startY = (startRow + 0.5f) * cellHeight;
        endX = (endCol + 0.5f) * cellWidth;
        endY = (endRow + 0.5f) * cellHeight;

        beginAnimation();
    }

    public void clear() {
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
        drawLine = false;
        progress = 0f;
        setVisibility(GONE);
        invalidate();
    }

    private void beginAnimation() {
        if (animator != null) {
            animator.cancel();
        }
        progress = 0f;
        drawLine = true;
        setVisibility(VISIBLE);
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(400);
        animator.addUpdateListener(valueAnimator -> {
            progress = (float) valueAnimator.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }
}
