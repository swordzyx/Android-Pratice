package sword

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.view.MotionEvent
import android.view.View


/**
 * 自定义 parent 下面某个子 View 的点击事件。
 *
 * @Param left: 点击区域的左边界
 * @Param right：点击区域的右边界
 * @Param top：点击区域的上边界
 * @Param bottom：点击区域的下边界
 */
@SuppressLint("ClickableViewAccessibility")
private fun expendCheckBoxClickable(parent: View, left: Int, right: Int, top: Int, bottom: Int) {
	parent.setOnTouchListener(View.OnTouchListener { _, event ->
		when (event.action) {
			MotionEvent.ACTION_DOWN -> {
				val downX = event.x
				val downY = event.y
				if (downX >= left && downX <= right && downY >= top && downY <= bottom) {
					return@OnTouchListener true
				}
			}
			MotionEvent.ACTION_UP -> {
				val upY = event.y
				val upX = event.x
				if (upX >= left && upX <= right && upY >= top && upY <= bottom) {
					//todo：响应点击事件 performClick();
					return@OnTouchListener true
				}
			}
		}
		false
	})
}