package sword.view.floatball;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.swordlibrary.R;

import sword.ScreenSize;

/**
 * 悬浮菜单封装类
 */
public class MenuItemView extends LinearLayout {
    private final ImageView iconView;
    private final TextView titleView;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    int verticalPadding = ScreenSize.dp(3);

    public MenuItemView(Context context) {
        super(context);
        setOrientation(VERTICAL);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.RED);

        iconView = new ImageView(context);
        iconView.setPadding(ScreenSize.dp(3), ScreenSize.dp(3), ScreenSize.dp(3), ScreenSize.dp(3));
        iconView.setBackgroundResource(R.drawable.background_circle_corner_gray);
        iconView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ScreenSize.dp(28), ScreenSize.dp(28));
        params.gravity = Gravity.CENTER;
        addView(iconView, params);

        titleView =  new TextView(context);
        titleView.setTextColor(Color.WHITE);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30);
        titleView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = verticalPadding;
        addView(titleView, lp);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        unRead(canvas, 0);
    }

    public void setItemViewData(int resId, String title) {
        iconView.setImageResource(resId);
        titleView.setText(title);
    }
    
    public void setIconView(int resId) {
        iconView.setImageResource(resId);
    }

    void unRead(Canvas canvas, int num) {
        float cx = (float) ((getWidth() >> 1) + ScreenSize.dp(17) * Math.sin(Math.PI / 6));
        float cy = (float) (iconView.getTop() + ScreenSize.dp(17) - ScreenSize.dp(17) * Math.cos(Math.PI / 6));
        canvas.drawCircle(cx, cy, ScreenSize.dp(3), paint);
    }
}