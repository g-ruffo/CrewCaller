package ca.veltus.crewcaller.main.scheduled

import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.base.BaseRecyclerViewAdapter
import ca.veltus.crewcaller.main.data.dataitem.ProductionDataItem
import ca.veltus.crewcaller.main.data.dataitem.WorkDayDataItem

class ScheduledWorkListAdapter (callBack: (workDayDataItem: WorkDayDataItem) -> Unit) :
    BaseRecyclerViewAdapter<WorkDayDataItem>(callBack) {
    override fun getLayoutRes(viewType: Int) = R.layout.scheduled_work_list_item
}