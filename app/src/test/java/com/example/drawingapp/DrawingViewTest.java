package com.example.drawingapp;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
public class DrawingViewTest {

    private DrawingView drawingView;
    private MotionEvent motionEvent;

    @Before
    public void setUp() {
        Context context = RuntimeEnvironment.getApplication();
        drawingView = new DrawingView(context, null);
        motionEvent = mock(MotionEvent.class);
    }

    @Test
    public void testOnTouchEvent_actionDown_addsNewPath() {
        // Simulate ACTION_DOWN event
        // We need to use a real MotionEvent for ACTION_DOWN because Robolectric's mock MotionEvent
        // doesn't seem to work well with the way DrawingView processes it (getX/getY).
        // However, creating a real MotionEvent is tricky without proper setup.
        // For now, let's assume the initial state has 0 paths.
        // After calling onTouchEvent with ACTION_DOWN, there should be 1 path.
        
        // Get initial path count
        int initialPathCount = drawingView.getPathsCount(); // Requires a getter in DrawingView

        // Create a real MotionEvent for ACTION_DOWN
        long downTime = System.currentTimeMillis();
        long eventTime = System.currentTimeMillis();
        float x = 100f;
        float y = 100f;
        int metaState = 0;
        MotionEvent actionDownEvent = MotionEvent.obtain(
                downTime, eventTime, MotionEvent.ACTION_DOWN, x, y, metaState
        );

        drawingView.onTouchEvent(actionDownEvent);

        // Verify a new path was added
        assertEquals("A new path should be added on ACTION_DOWN", initialPathCount + 1, drawingView.getPathsCount());
    }
}
