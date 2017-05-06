package com.linedraw.drawline.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.linedraw.drawline.GameManager;
import com.linedraw.drawline.utils.Fraction;

/**
 * Created by byron.
 */
public class GraphView extends View {

    private static final String TAG = GraphView.class.getSimpleName();

    private static final float TOUCH_RADIUS = 50;

    // Stores graph and line information
    private int gridDimension;
    private float lineStartX;
    private float lineStartY;
    private float lineEndX;
    private float lineEndY;
    private float finalLineStartX;
    private float finalLineStartY;
    private float finalLineEndX;
    private float finalLineEndY;
    private int touchStartIndex = -1;

    // Appearance fields
    private Paint gridPaint;
    private Paint axisPaint;
    private Paint linePaint;
    private Paint textPaint;
    private Paint[] pointsPaint = new Paint[2];

    private boolean canTouchThis = true;
    private boolean dragStarted;

    private GameManager gameManager;

    private Listener listener;

    public GraphView(Context context) {
        super(context);
        Init();
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init();
    }

    public GraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init();
    }

    public interface Listener {
        void onPointClicked(int index);
        void onLineDrawn();
    }

    // Initialize
    public void Init() {
        // Set initial grid dimension
        setGridDimension(10);

        // Grid line paint
        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(1);
        gridPaint.setColor(Color.GRAY);

        // Axis paint
        axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        axisPaint.setStyle(Paint.Style.STROKE);
        axisPaint.setStrokeWidth(3);
        axisPaint.setColor(Color.BLACK);

        // Line paint
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(5);
        linePaint.setColor(Color.BLUE);

        // Text paint
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(20);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setStrokeWidth(1);
        textPaint.setColor(Color.BLACK);

        // Circle paint
        pointsPaint = new Paint[2];
        pointsPaint[0] = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointsPaint[0].setStyle(Paint.Style.FILL);
        pointsPaint[0].setColor(Color.BLUE);

        pointsPaint[1] = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointsPaint[1].setStyle(Paint.Style.FILL);
        pointsPaint[1].setColor(Color.YELLOW);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GraphView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Background color
        canvas.drawColor(Color.WHITE);

        // Draw grid lines in x dimension (vertical lines)
        for (int x = -this.getGridDimension(); x <= this.getGridDimension(); ++x)
            canvas.drawLine(interpX(x), interpY(this.getGridDimension()),
                    interpX(x), interpY(-this.getGridDimension()),
                    (x == 0) ? axisPaint : gridPaint);

        // Draw grid lines in y dimension (horizontal lines)
        for (int y = -this.getGridDimension(); y <= this.getGridDimension(); ++y)
            canvas.drawLine(interpX(-this.getGridDimension()), interpY(y),
                    interpX(this.getGridDimension()), interpY(y),
                    (y == 0) ? axisPaint : gridPaint);

        // Draw coordinate text
        int step = this.getGridDimension() / 4;
        textPaint.setTextAlign(Paint.Align.CENTER);
        for (int x = -this.getGridDimension() + step; x < this
                .getGridDimension(); x += step) {
            if (x != 0) {
                canvas.drawText(Double.toString(x), interpX(x), interpY(0),
                        textPaint);
            }
        }
        textPaint.setTextAlign(Paint.Align.LEFT);
        for (int y = -this.getGridDimension() + step; y < this
                .getGridDimension(); y += step) {
            canvas.drawText(Double.toString(y), interpX(0), interpY(y),
                    textPaint);
        }

        drawPoints(canvas);
    }

    private void drawPoints(Canvas canvas) {
        if (lineStartX > 0 && lineStartY > 0 &&
                lineEndX > 0 && lineEndY > 0) {
            canvas.drawLine(interpX(gameManager.getPoints()[touchStartIndex].x), interpY(gameManager.getPoints()[touchStartIndex].y),
                    lineEndX, lineEndY, linePaint);
        }

        if (finalLineStartX > 0 && finalLineStartY > 0 &&
                finalLineEndX > 0 && finalLineEndY > 0) {
            double x0 = -this.getGridDimension();
            double y0 = solveLineEq(x0);
            double x1 = this.getGridDimension();
            double y1 = solveLineEq(x1);

            // Draw line
            canvas.drawLine(interpX(x0), interpY(y0), interpX(x1), interpY(y1),
                    linePaint);
        }

        // Draw y-intercept point
        if (gameManager == null) {
            return;
        }

        for (int i = 0 ; i < gameManager.getPoints().length ; i++) {
            if (gameManager.getPoints()[i] != null && gameManager.isPointVisible(i)) {
                double xp = gameManager.getPoints()[i].x;
                double yp = gameManager.getPoints()[i].y;
                canvas.drawCircle(interpX(xp), interpY(yp), this.getWidth()
                        * (float) 0.02, pointsPaint[i]);
            }
        }
    }

    // Interpolate from graph to Canvas coordinates
    private float interpX(double x) {
        double width = (double) this.getWidth();
        return (float) ((x + this.getGridDimension())
                / (this.getGridDimension() * 2) * width);
    }

    private float interpY(double y) {
        double height = (double) this.getHeight();
        return (float) ((y + this.getGridDimension())
                / (this.getGridDimension() * 2) * -height + height);
    }

    private double solveLineEq(double x) {
        Fraction slope = gameManager.getSlope();
        double yIntercept = gameManager.getyIntercept().toDouble();
        return (slope.toDouble() * x) + yIntercept;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec) -
                this.getPaddingLeft() - this.getPaddingRight();
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (!canTouchThis) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!gameManager.areAllPointsVisible()) {
                    int pointIndex = gameManager.checkPoint(event.getX(), event.getY(), TOUCH_RADIUS,
                            this.getWidth(), this.getHeight(), this.getGridDimension());
                    if (pointIndex != -1 && listener != null) {
                        listener.onPointClicked(pointIndex);
                    }
                    invalidate();
                    return false;
                }
                if (isTouchStartValid(gameManager.getPoints(), event.getX(), event.getY())) {
                    lineStartX = event.getX();
                    lineStartY = event.getY();
                    dragStarted = true;
                } else {
                    lineStartX = 0;
                    lineStartY = 0;
                    touchStartIndex = -1;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                lineEndX = event.getX();
                lineEndY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (isTouchEndValid(gameManager.getPoints(), event.getX(), event.getY()) &&
                        dragStarted) {
                    finalLineStartX = lineStartX;
                    finalLineStartY = lineStartY;
                    finalLineEndX = lineEndX;
                    finalLineEndY = lineEndY;
                    if (listener != null) {
                        listener.onLineDrawn();
                    }
                }
                touchStartIndex = -1;
                lineStartX = 0;
                lineStartY = 0;
                lineEndX = 0;
                lineEndY = 0;
                dragStarted = false;
                invalidate();
                break;
        }
        return true;
    }

    private boolean isTouchStartValid(Point[] points, float xTouch, float yTouch) {
        if (points == null ||
                points.length != 2) {
            return false;
        }

        for (int i = 0 ; i < points.length ; i++) {
            float x = interpX(points[i].x);
            float y = interpY(points[i].y);
            float touchRadius = (float) Math.sqrt(Math.pow(xTouch - x, 2) + Math.pow(yTouch - y, 2));

            if (touchRadius < TOUCH_RADIUS) {
                touchStartIndex = i;
                return true;
            }
        }
        return false;
    }

    private boolean isTouchEndValid(Point[] points, float xTouch, float yTouch) {
        if (points == null ||
                points.length != 2) {
            return false;
        }

        for (int i = 0 ; i < points.length ; i++) {
            if (i == touchStartIndex) {
                continue;
            }
            float x = interpX(points[i].x);
            float y = interpY(points[i].y);
            float touchRadius = (float) Math.sqrt(Math.pow(xTouch - x, 2) + Math.pow(yTouch - y, 2));

            if (touchRadius < TOUCH_RADIUS) {
                return true;
            }
        }
        return false;
    }

    public void setCanTouchThis(boolean canTouchThis) {
        this.canTouchThis = canTouchThis;
    }

    public int getGridDimension() {
        return gridDimension;
    }

    public void setGridDimension(int gridDimension) {
        this.gridDimension = gridDimension;
    }
}
