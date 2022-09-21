package ca.veltus.crewcaller.main.productions

import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.base.BaseRecyclerViewAdapter
import ca.veltus.crewcaller.main.data.dataitem.ProductionDataItem

class ProductionListAdapter (callBack: (selectedProduction: ProductionDataItem) -> Unit) :
    BaseRecyclerViewAdapter<ProductionDataItem>(callBack) {
    override fun getLayoutRes(viewType: Int) = R.layout.production_list_item
}