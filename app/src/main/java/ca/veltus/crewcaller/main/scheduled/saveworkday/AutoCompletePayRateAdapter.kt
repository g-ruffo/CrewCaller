package ca.veltus.crewcaller.main.scheduled.saveworkday

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.main.data.dataitem.PayRateDataItem
import java.util.*

class AutoCompletePayRateAdapter(context: Context, resource: Int, var items: List<PayRateDataItem>)
    : ArrayAdapter<PayRateDataItem>(context, resource, items) {

    val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemId(position: Int): Long {
        return UUID.fromString(items[position].id).mostSignificantBits and Long.MAX_VALUE
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view: View? = convertView
        if (view == null) {
            view = inflater.inflate(R.layout.drop_down_menu_item_two, parent, false)
        }
        (view?.findViewById(R.id.payRateTierTextView) as TextView).text = getItem(position)!!.tier
        (view.findViewById(R.id.payRatePositionTextView) as TextView).text = getItem(position)!!.position
        (view.findViewById(R.id.payRateRateTextView) as TextView).text = getItem(position)!!.rate

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view: View? = convertView
        if (view == null) {
            view = inflater.inflate(R.layout.drop_down_menu_item_two, parent, false)
        }
        (view?.findViewById(R.id.payRateTierTextView) as TextView).text = getItem(position)!!.tier
        (view.findViewById(R.id.payRatePositionTextView) as TextView).text = getItem(position)!!.position
        (view.findViewById(R.id.payRateRateTextView) as TextView).text = getItem(position)!!.rate
        return view
    }
}