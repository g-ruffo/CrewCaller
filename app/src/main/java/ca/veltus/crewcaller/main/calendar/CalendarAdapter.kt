package ca.veltus.crewcaller.main.calendar

import android.view.ViewGroup
import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.base.BaseRecyclerViewAdapter
import ca.veltus.crewcaller.base.DataBindingViewHolder
import ca.veltus.crewcaller.main.data.dataitem.CalenderDayDataItem

class CalendarAdapter(callBack: (calenderDayDataItem: CalenderDayDataItem) -> Unit) :
    BaseRecyclerViewAdapter<CalenderDayDataItem>(callBack) {
    override fun getLayoutRes(viewType: Int) = R.layout.calendar_cell_item

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DataBindingViewHolder<CalenderDayDataItem> {
        val binding = super.onCreateViewHolder(parent, viewType)
        binding.itemView.tag = R.layout.calendar_cell_item
        binding.itemView.layoutParams.height = (parent.height * 0.166666666).toInt()
        return binding
    }
}