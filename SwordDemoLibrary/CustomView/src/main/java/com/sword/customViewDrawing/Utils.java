package com.sword.customViewDrawing;

import android.content.res.Resources;
import android.util.TypedValue;

public class Utils {
	public static float dp2px(float value) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, Resources.getSystem().getDisplayMetrics());
	}
}
