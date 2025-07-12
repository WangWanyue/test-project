package com.example.drawingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class DrawingView extends View {

    // Stores completed paths mainly for testing (e.g. path count)
    private final List<Path> paths = new ArrayList<>();

    // Currently active stroke (not yet committed to the buffer)
    private Path currentPath;

    // Off-screen bitmap & canvas to avoid redrawing every historical path each frame
    private Bitmap bitmapBuffer;
    private Canvas bufferCanvas;

    private Paint drawPaint;

    // Touch throttling
    private static final float TOUCH_TOLERANCE = 2f; // px
    private float lastX;
    private float lastY;

    public DrawingView(Context context) {
        super(context);
        setupDrawing();
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing() {
        drawPaint = new Paint();
        drawPaint.setColor(Color.BLACK);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(5f);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            // Recreate the buffer when the view size changes
            bitmapBuffer = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bufferCanvas = new Canvas(bitmapBuffer);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw the cached pixels first
        if (bitmapBuffer != null) {
            canvas.drawBitmap(bitmapBuffer, 0, 0, null);
        }

        // Draw the active in-progress stroke so it feels responsive
        if (currentPath != null) {
            canvas.drawPath(currentPath, drawPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentPath = new Path();
                paths.add(currentPath); // keep reference mostly for tests
                currentPath.moveTo(touchX, touchY);
                lastX = touchX;
                lastY = touchY;
                break;

            case MotionEvent.ACTION_MOVE:
                if (currentPath != null) {
                    float dx = Math.abs(touchX - lastX);
                    float dy = Math.abs(touchY - lastY);
                    if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                        currentPath.lineTo(touchX, touchY);
                        lastX = touchX;
                        lastY = touchY;
                    } else {
                        // Movement is too small – skip to throttle noisy events
                        return true;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (currentPath != null && bufferCanvas != null) {
                    // Commit the finished stroke to the off-screen buffer
                    bufferCanvas.drawPath(currentPath, drawPaint);
                    currentPath = null;
                }
                break;
            default:
                return false;
        }

        // Schedule a new frame – coalesced with display vsync
        postInvalidateOnAnimation();
        return true;
    }

    // Getter for testing
    public int getPathsCount() {
        return paths.size();
    }
}
