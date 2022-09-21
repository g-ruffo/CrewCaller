package ca.veltus.crewcaller.main.payrates

import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.base.BaseRecyclerViewAdapter
import ca.veltus.crewcaller.main.data.dataitem.PayRateDataItem

class PayRateListAdapter (callBack: (selectedPayRate: PayRateDataItem) -> Unit) :
    BaseRecyclerViewAdapter<PayRateDataItem>(callBack) {
    override fun getLayoutRes(viewType: Int) = R.layout.pay_rate_list_item
}