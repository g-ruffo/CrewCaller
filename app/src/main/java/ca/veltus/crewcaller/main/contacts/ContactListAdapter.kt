package ca.veltus.crewcaller.main.contacts

import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.base.BaseRecyclerViewAdapter
import ca.veltus.crewcaller.main.data.dataitem.ContactDataItem

class ContactListAdapter(callBack: (selectedContact: ContactDataItem) -> Unit) :
    BaseRecyclerViewAdapter<ContactDataItem>(callBack) {
    override fun getLayoutRes(viewType: Int) = R.layout.contact_list_item
}