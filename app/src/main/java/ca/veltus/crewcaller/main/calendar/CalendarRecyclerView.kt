package ca.veltus.crewcaller.main.calendar

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.GridLayoutAnimationController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CalendarRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {

    override fun setLayoutManager(layout: LayoutManager?) {
        if (layout is GridLayoutManager) {
            super.setLayoutManager(layout)
        } else {
            throw Exception("GridlayoutManager is required")
        }
    }

    override fun attachLayoutAnimationParameters(
        child: View?, params: ViewGroup.LayoutParams?, index: Int, count: Int
    ) {
        if (adapter != null && layoutManager is GridLayoutManager) {
            var animationParams =
                params!!.layoutAnimationParameters as GridLayoutAnimationController.AnimationParameters?
            if (animationParams == null) {
                animationParams = GridLayoutAnimationController.AnimationParameters()
                params.layoutAnimationParameters = animationParams
            }

            val column = (layoutManager as GridLayoutManager).spanCount

            animationParams.count = count
            animationParams.index = index
            animationParams.columnsCount = column
            animationParams.rowsCount = count / column

            val index = count - 1 - index
            animationParams.column = column - 1 - (index % column)
            animationParams.row = animationParams.rowsCount - 1 - index / column
        } else {
            super.attachLayoutAnimationParameters(child, params, index, count)
        }
    }
}