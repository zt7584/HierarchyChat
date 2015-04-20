package zt.dev.hierarchychat.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import zt.dev.hierarchychat.data.FirebaseHandler;

public class PaintBoardCustomView extends View {
    private static final float TOUCH_TOLERANCE = 4;
    private static final int PENCIL_STROKE_WIDTH = 12;
    private static final int ERASER_STROKE_WIDTH = 50;

    private Paint paint = new Paint();
    private Paint bitmapPaint = new Paint(Paint.DITHER_FLAG);
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;

    private int curColor;
    private int curStrokeWidth;
    private float preX, preY;

    private Firebase paintBoardRef;
    private Query paintBoardQuery;
    private ChildEventListener colorPointChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
            Map<Object, Object> colorPoint = (Map<Object, Object>) snapshot.getValue();
            int x = ((Long) colorPoint.get(FirebaseHandler.X_POS_KEY)).intValue();
            int y = ((Long) colorPoint.get(FirebaseHandler.Y_POS_KEY)).intValue();
            int color = ((Long) colorPoint.get(FirebaseHandler.COLOR_KEY)).intValue();
            int strokeWidth = ((Long) colorPoint.get(FirebaseHandler.STROKE_WIDTH_KEY)).intValue();
            if (paint != null) {
                paint.setColor(color);
                paint.setStrokeWidth(strokeWidth);
            }
            switch (((Long) colorPoint.get(FirebaseHandler.STATE_KEY)).intValue()) {
                case ColorPoint.START_POINT: {
                    onTouchStart(x, y);
                    invalidate();
                    break;
                }
                case ColorPoint.MID_POINT: {
                    onTouchMove(x, y);
                    invalidate();
                    break;
                }
                case ColorPoint.FINAL_POINT: {
                    onTouchUp();
                    invalidate();
                    break;
                }
            }
        }

        @Override
        public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
        }

        @Override
        public void onChildRemoved(DataSnapshot snapshot) {
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
        }

        @Override
        public void onChildMoved(DataSnapshot snapshot, String key) {
        }
    };


    public PaintBoardCustomView(final Context context) {
        super(context);
    }

    public PaintBoardCustomView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public PaintBoardCustomView(final Context context, final AttributeSet attrs,
                                   final int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);

        float x = event.getX();
        float y = event.getY();

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                addColorPoint(x, y, ColorPoint.START_POINT);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                addColorPoint(x, y, ColorPoint.MID_POINT);
                break;
            }
            case MotionEvent.ACTION_UP: {
                addColorPoint(x, y, ColorPoint.FINAL_POINT);
                break;
            }
        }
        return true;
    }

    /*
     * Process painting point when getting point from Firebase
     */

    private void onTouchStart(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        preX = x;
        preY = y;
    }
    private void onTouchMove(float x, float y) {
        float dx = Math.abs(x - preX);
        float dy = Math.abs(y - preY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(preX, preY, (x + preX)/2, (y + preY)/2);
            preX = x;
            preY = y;
        }
    }

    private void onTouchUp() {
        mCanvas.drawPath(mPath,  paint);
        mPath.reset();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        this.setBackgroundColor(Color.WHITE);
        if (mPath != null && mBitmap != null) {
            canvas.drawBitmap(mBitmap, 0, 0, bitmapPaint);
            canvas.drawPath(mPath, paint);
        }
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec,
                             final int heightMeasureSpec) {
        final int width = getDefaultSize(getSuggestedMinimumWidth(),
                widthMeasureSpec);
        final int height = getDefaultSize(getSuggestedMinimumHeight(),
                heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw,
                                 final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }
    }

    /*
     * Initialize the view with a firebase reference
     */

    public void setPaintRef(Firebase paintBoardRef) {
        mPath = new Path();
        this.paintBoardRef = paintBoardRef;
        this.paintBoardQuery = paintBoardRef.orderByChild(FirebaseHandler.TIMESTAMP_KEY);
        this.paintBoardQuery.addChildEventListener(colorPointChildEventListener);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(PENCIL_STROKE_WIDTH);
        this.curColor = Color.BLACK;
        this.curStrokeWidth = PENCIL_STROKE_WIDTH;
        invalidate();
    }

    /*
     * Switch between Pencil and Eraser
     */
    public void switchTool() {
        if (curColor == Color.BLACK) {
            curColor = Color.WHITE;
            curStrokeWidth = ERASER_STROKE_WIDTH;
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(ERASER_STROKE_WIDTH);
        } else {
            curColor = Color.BLACK;
            curStrokeWidth = PENCIL_STROKE_WIDTH;
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(PENCIL_STROKE_WIDTH);
        }
    }

    /*
     * Add a ColorPoint to firebase
     */

    public void addColorPoint(float x, float y, int state) {
        Map<String, Object> newColorPoint = new HashMap<String, Object>();
        newColorPoint.put(FirebaseHandler.X_POS_KEY, (int)x);
        newColorPoint.put(FirebaseHandler.Y_POS_KEY, (int)y);
        newColorPoint.put(FirebaseHandler.COLOR_KEY, curColor);
        newColorPoint.put(FirebaseHandler.TIMESTAMP_KEY, new Date().getTime());
        newColorPoint.put(FirebaseHandler.STATE_KEY, state);
        newColorPoint.put(FirebaseHandler.STROKE_WIDTH_KEY, curStrokeWidth);
        paintBoardRef.push().setValue(newColorPoint);
    }

    /*
     * ColorPoint object is used to hold all information related to a specific point
     */

    class ColorPoint {
        public static final int START_POINT = 0;
        public static final int MID_POINT = 1;
        public static final int FINAL_POINT = 2;

        public Point point;
        public int color;
        public long timeStamp;
        public int state;
        public int strokeWidth;

        public ColorPoint(int x, int y, int color, long timpeStamp, int state, int strokeWidth) {
            this.point = new Point(x, y);
            this.color = color;
            this.timeStamp = timpeStamp;
            this.state = state;
            this.strokeWidth = strokeWidth;
        }
    }
}
