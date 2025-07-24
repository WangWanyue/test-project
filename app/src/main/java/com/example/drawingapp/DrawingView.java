package com.example.drawingapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class DrawingView extends View {

    private List<Path> paths = new ArrayList<>();
    private Path currentPath;
    private Paint drawPaint;
    
    // Bitmap caching fields
    private Bitmap cacheBitmap;
    private Canvas cacheCanvas;

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
        if (cacheBitmap != null) {
            cacheBitmap.recycle();
        }
        cacheBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        cacheCanvas = new Canvas(cacheBitmap);
        redrawCache();
    }

    // Redraw all completed paths to the cache bitmap
    private void redrawCache() {
        if (cacheCanvas != null) {
            cacheBitmap.eraseColor(Color.TRANSPARENT);
            for (Path path : paths) {
                cacheCanvas.drawPath(path, drawPaint);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw cached bitmap
        if (cacheBitmap != null) {
            canvas.drawBitmap(cacheBitmap, 0, 0, null);
        }
        // Draw current in-progress path
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
                currentPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                if (currentPath != null) {
                    currentPath.lineTo(touchX, touchY);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (currentPath != null) {
                    // Commit the finished path to the cache
                    cacheCanvas.drawPath(currentPath, drawPaint);
                    paths.add(currentPath);
                    currentPath = null;
                }
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    // Getter for testing
    public int getPathsCount() {
        return paths.size();
    }
}
