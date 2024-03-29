package sword.view

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.util.AttributeSet
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import sword.logger.SwordLog

class DragListenerGridView(context: Context, attrs: AttributeSet? = null) :
  ViewGroup(context, attrs) {
  private val tag = "DragListenerGridView"
  private val column = 2
  private val row = 3
  private val views = mutableListOf<View>()

  //这里返回 false，View.onDragEvent 就会被调用
  private val dragListener = OnDragListener { v, event ->
    when (event.action) {
      DragEvent.ACTION_DRAG_STARTED -> {
        //某个 View 开始被拖拽
        SwordLog.debug(
          tag, "view#${views.indexOf(v)} 开始被拖拽，" +
              "localState：${views.indexOf((event.localState as View))}，" +
              "v==localState: ${v == event.localState}"
        )
        if (v == event.localState && v.visibility != View.INVISIBLE) {
          v.visibility = View.INVISIBLE
        }
      }
      DragEvent.ACTION_DRAG_ENTERED -> {
        SwordLog.debug(
          tag, "进入 view#${views.indexOf(v)}，" +
              "localState：${views.indexOf((event.localState as View))}，" +
              "v==localState: ${v == event.localState}" +
              ", v===localState: ${v === event.localState}"
        )
        //手指进入某个 View 的区域内
        if (v == event.localState) {
          return@OnDragListener true
        }

        val originIndex = views.indexOf(event.localState as View)
        val aimIndex = views.indexOf(v)
        SwordLog.debug(tag, "被拖拽的 View 索引：$originIndex, 目标 View 索引：$aimIndex")
        /*if (aimIndex > originIndex) {
          for (i in originIndex until aimIndex) {
            views[i] = views[i + 1]
          }
        } else {
          for (i in originIndex - 1 downTo aimIndex) {
            views[i + 1] = views[i]
          }
        }*/
        val dragView = event.localState as View
        views.removeAt(originIndex)
        views.add(aimIndex, dragView)

        val columnWidth = width / column
        val rowHeight = height / row
        SwordLog.debug(tag, "列宽：$columnWidth，行高：${rowHeight}")
        views.forEachIndexed { index, child ->
          val childPoint = calculateLocation(index, columnWidth, rowHeight)
          SwordLog.debug(tag, "原始左：${child.left}，目标左：${childPoint.x}，原始上：${child.top}，目标上：${childPoint.y}， index：$index")
          child.animate()
            .translationX(childPoint.x.toFloat() - child.left)
            .translationY(childPoint.y.toFloat() - child.top)
            .setDuration(150)
            .start()
        }
      }
      DragEvent.ACTION_DRAG_LOCATION -> {
        SwordLog.debug(
          tag, "当前所在 view#${views.indexOf(v)}，x: ${event.x}, y: ${event.y}" +
              "localState：${views.indexOf(event.localState as View)}，" +
              "v==localState: ${v == event.localState}"
        )
      }
      DragEvent.ACTION_DRAG_EXITED -> {
        SwordLog.debug(
          tag, "退出 view#${views.indexOf(v)}，" +
              "localState：${views.indexOf((event.localState as View))}，" +
              "v==localState: ${v == event.localState}"
        )
      }
      DragEvent.ACTION_DRAG_ENDED -> {
        SwordLog.debug(
          tag, "拖拽结束 view#${views.indexOf(v)}，" +
              "localState：${views.indexOf((event.localState as View))}，" +
              "v==localState: ${v == event.localState}，getResult: ${event.result}"
        )
        if (v.visibility != View.VISIBLE) {
          v.visibility = View.VISIBLE
        }
      }
      DragEvent.ACTION_DROP -> {
        SwordLog.debug(
          tag, "拖拽取消 view#${views.indexOf(v)}，" +
              "localState：${views.indexOf(event.localState as View)}，" +
              "v==localState: ${v == event.localState}"
        )
        if (v.visibility != View.VISIBLE) {
          v.visibility = View.VISIBLE
        }
      }
    }
    true
  }

  override fun onFinishInflate() {
    super.onFinishInflate()
    SwordLog.debug(tag, "onFinishInflate, 子 View 数量：$childCount")
    children.forEach { child ->
      views.add(child)
      child.setOnDragListener(dragListener)
      //长按拖拽
      child.setOnLongClickListener {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
          child.startDragAndDrop(null, DragShadowBuilder(child), child, 0)
        } else {
          child.startDrag(null, DragShadowBuilder(child), child, 0)
        }
      }
    }
    SwordLog.debug(tag, "onFinishInflate, views 数量：${views.size}")
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = MeasureSpec.getSize(widthMeasureSpec)
    val widthSpec = MeasureSpec.makeMeasureSpec(
      width / column,
      MeasureSpec.EXACTLY
    )

    val height = MeasureSpec.getSize(heightMeasureSpec)
    val heightSpec = MeasureSpec.makeMeasureSpec(
      height / row,
      MeasureSpec.EXACTLY
    )

    children.forEach { child ->
      child.measure(widthSpec, heightSpec)
    }
    setMeasuredDimension(width, height)
  }

  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    views.forEachIndexed { index, child ->
      val childWidth = measuredWidth / column
      val childHeight = measuredHeight / row
      val childPoint = calculateLocation(index, childWidth, childHeight)
      child.layout(childPoint.x, childPoint.y, childPoint.x + child.measuredWidth, childPoint.y + child.measuredHeight)
    }
  }

  private fun calculateLocation(
    index: Int,
    columnWidth: Int, 
    rowHeight: Int
  ): Point {
    val rowIndex = index / column
    val columnIndex = index % column
    
    val left = columnIndex * columnWidth
    val top = rowIndex * rowHeight
    SwordLog.debug(tag, "$rowIndex 行 $columnIndex 列，左：$left，上：$top， index：$index")
    return Point(left, top)
  }
}