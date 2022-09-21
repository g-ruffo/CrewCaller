package ca.veltus.crewcaller.utils

import android.content.res.ColorStateList
import android.graphics.Color
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.*
import androidx.databinding.adapters.ListenerUtil
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.base.BaseRecyclerViewAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


object BindingAdapters {


    // Use binding adapter to set the recycler view data using livedata object.
    @Suppress("UNCHECKED_CAST")
    @BindingAdapter("android:liveData")
    @JvmStatic
    fun <T> setRecyclerViewData(recyclerView: RecyclerView, items: LiveData<List<T>>?) {
        items?.value?.let { itemList ->
            (recyclerView.adapter as? BaseRecyclerViewAdapter<T>)?.apply {
                clear()
                addData(itemList)
            }
        }
    }

    // Use this binding adapter to show and hide the views using boolean variables.
    @BindingAdapter("android:fadeVisible")
    @JvmStatic
    fun setFadeVisible(view: View, visible: Boolean? = true) {
        if (view.tag == null) {
            view.tag = true
            view.visibility = if (visible == true) View.VISIBLE else View.GONE
        } else {
            view.animate().cancel()
            if (visible == true) {
                if (view.visibility == View.GONE)
                    view.fadeIn()
            } else {
                if (view.visibility == View.VISIBLE)
                    view.fadeOut()
            }
        }
    }

    @BindingAdapter("setEnabled")
    @JvmStatic
    fun setFabButtonEnabled(fab: FloatingActionButton, text: String?) {
        if (text.isNullOrEmpty() || text.isNullOrBlank()) {
            fab.isEnabled = false
            fab.alpha = 0.5f
        } else {
            fab.isEnabled = true
            fab.alpha = 1f
        }
    }

    @BindingAdapter(value = ["setEnabledProduction", "setEnabledDate"], requireAll = true)
    @JvmStatic
    fun setFabButtonEnabledWorkDay(fab: FloatingActionButton, production: String?, date: String?) {
        if (production.isNullOrEmpty() || date.isNullOrBlank()) {
            fab.isEnabled = false
            fab.alpha = 0.5f
        } else {
            fab.isEnabled = true
            fab.alpha = 1f
        }
    }

    @BindingAdapter(
        value = ["setEnabledTier", "setEnabledPosition", "setEnabledRate"],
        requireAll = true
    )
    @JvmStatic
    fun setFabButtonEnabledPayRate(
        fab: FloatingActionButton,
        textTier: String?,
        textPosition: String?,
        textRate: String?
    ) {
        if (textTier.isNullOrEmpty() || textTier.isNullOrBlank() ||
            textPosition.isNullOrEmpty() || textPosition.isNullOrBlank() ||
            textRate.isNullOrEmpty() || textRate.isNullOrBlank()
        ) {
            fab.isEnabled = false
            fab.alpha = 0.5f
        } else {
            fab.isEnabled = true
            fab.alpha = 1f
        }
    }

    @BindingAdapter(
        value = ["setContactDetailFirstName", "setContactDetailLastName"],
        requireAll = true
    )
    @JvmStatic
    fun setContactDetailName(view: TextView, firstName: String?, lastName: String?) {
        if (!firstName.isNullOrEmpty() && !lastName.isNullOrEmpty()) {
            val firstLastName = "$firstName $lastName"
            view.text = firstLastName
        } else {
            view.text = "$firstName"
        }
    }

    @BindingAdapter(
        value = ["emailHelperTextColorSwitcher", "emailTextInputEditTextStatus"], requireAll = true
    )
    @JvmStatic
    fun setEmailHelperTextColor(view: TextInputLayout, text: String?, editText: TextInputEditText) {
        val context = view.context
        editText.setOnFocusChangeListener { v, hasFocus ->
            if (!Patterns.EMAIL_ADDRESS.matcher(text.toString())
                    .matches() && !hasFocus && text != "null" && !text.isNullOrEmpty()
            ) {
                view.setHelperTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.warningRed)
                    )
                )
                view.helperText = context.getString(R.string.invalidEmailAddressHelper)

            } else {
                view.setHelperTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.optionalGrey)
                    )
                )
                view.helperText = null
            }
        }
    }

    @BindingAdapter("prodNameHelperTextColorSwitcher")
    @JvmStatic
    fun setProductionNameHelperTextColor(view: TextInputLayout, text: String?) {
        val context = view.context
        if (text.isNullOrBlank()) {
            view.setHelperTextColor(
                ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.warningRed)
                )
            )

        } else {
            view.setHelperTextColor(
                ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.optionalGrey)
                )
            )
        }
    }

    @BindingAdapter("convertDateToDetail")
    @JvmStatic
    fun convertDateToDetail(view: TextView, date: String?) {
        val context = view.context
        if (date.isNullOrEmpty()) {
            view.text = context.getString(R.string.noDateAvailible)
        } else {
            val parsedDate = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val dateFormatter = SimpleDateFormat("EEEE, d MMM yyyy", Locale.ENGLISH)
            val dateObject = parsedDate.parse(date)
            val convertedDate = dateFormatter.format(dateObject!!)
            view.text = convertedDate
        }
    }

    @BindingAdapter("convertEarnings")
    @JvmStatic
    fun convertEarnings(view: TextView, earnings: Double?) {
        val context = view.context
        if (earnings != null) {
            val round = DecimalFormat("$ #,##0.00").format(earnings)
            view.text = round.toString()
            view.setTextColor(ContextCompat.getColor(context, R.color.earningsGreen))
        } else {
            view.text = context.getString(R.string.zeroEarnings)
            view.setTextColor(ContextCompat.getColor(context, R.color.calendarGrey))
        }
    }

    @BindingAdapter(
        value = ["setTimeWorkedStart", "setTimeWorkedEnd", "setTotalHoursWorked"],
        requireAll = true
    )
    @JvmStatic
    fun setTimeWorked(
        view: TextView,
        startTime: String?,
        endTime: String?,
        totalHoursWorked: String?
    ) {
        val context = view.context
        if (startTime.isNullOrEmpty() || endTime.isNullOrEmpty()) {
            view.maxLines = 1
            if (totalHoursWorked.isNullOrEmpty()) {
                view.text = context.getString(R.string.zeroTime)
            } else {
                view.text = totalHoursWorked
            }
        } else {
            view.text =
                convertMillisecondsToHours(calculateTimeDifferences(startTime, endTime).toInt())
            view.maxLines = 2
            view.gravity = Gravity.END
        }
    }

    @BindingAdapter("weatherUnixTimeConverter")
    @JvmStatic
    fun setTimeFromUnix(view: TextView, time: Int?) {
        if (time != null) {
            val dateFormatter = SimpleDateFormat("d MMM yyyy HH:mm:ss", Locale.ENGLISH)
            view.text = dateFormatter.format(time.toLong() * 1000)
        }
    }

    @BindingAdapter("localDateTimeConverter")
    @JvmStatic
    fun setTimeFromLocalDateTime(view: TextView, time: String?) {
        val context = view.context
        if (time != null) {
            val localDateTime = LocalDateTime.parse(time)
            val dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy HH:mm:ss")
            val formattedDateTime = localDateTime.format(dateFormatter)
            view.text = context.getString(R.string.lastBackUp, formattedDateTime)
        }
    }

    @BindingAdapter("checkForEmptyContacts")
    @JvmStatic
    fun checkForEmptyContacts(view: CardView, list: Int?) {
        if (list == null || list < 1) {
            view.elevation = 8f
            view.alpha = 0.8f
        } else {
            view.elevation = 20f
            view.alpha = 1f
        }
    }

    @BindingAdapter("checkForEmptyTextField")
    @JvmStatic
    fun checkForEmptyTextField(view: CardView, text: String?) {
        val context = view.context
        if (text.isNullOrEmpty()) {
            view.elevation = 4f
            view.setCardBackgroundColor(ContextCompat.getColor(context, R.color.greyBackground))
        } else {
            view.elevation = 20f
            view.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))

        }
    }

    @BindingAdapter("setPreviousCompletedWork")
    @JvmStatic
    fun setPreviousCompletedWork(view: CardView, endTime: String?) {
        if (endTime.isNullOrEmpty()) {
            view.alpha = 1f
        } else {
            view.alpha = 0.7f
        }
    }

    @BindingAdapter("setCalendarCellBackgroundColor")
    @JvmStatic
    fun setCalendarCellBackgroundColor(view: ConstraintLayout, worked: Boolean) {
        val context = view.context
        if (worked) {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.timerRunningGreen))
        } else {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
    }

    @BindingAdapter(
        value = ["checkEditingEnabled", "checkEmptyArray", "checkEmptyList"],
        requireAll = false
    )
    @JvmStatic
    fun setArraySpinnerEnabled(
        view: View,
        isEditing: Boolean,
        array: Array<Any>?,
        list: List<Any>?
    ) {
        view.isEnabled = isEditing && !array.isNullOrEmpty() || isEditing && !list.isNullOrEmpty()
    }

    @BindingAdapter(value = ["disableIfLoading", "disableIfNull"], requireAll = true)
    @JvmStatic
    fun disableIfLoadingAndNull(view: View, loading: Boolean, data: Any?) {
        val context = view.context
        if (loading || data == null) {
            view.isEnabled = false
            view.setBackgroundColor(Color.GRAY)


        } else {
            view.isEnabled = true
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.purpleLogoLight))
        }
    }

    @BindingAdapter("disableWhenLoading")
    @JvmStatic
    fun disableWhenLoading(view: View, loading: Boolean) {
        val context = view.context
        if (loading) {
            view.isEnabled = false
            view.setBackgroundColor(Color.GRAY)

        } else {
            view.isEnabled = true
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.purpleLogoLight))
        }
    }


    @InverseBindingAdapter(attribute = "setCurrencyTextWatcher", event = "custom:currencyListener")
    @JvmStatic
    fun getCurrencyText(view: TextInputEditText): String {
        return view.text.toString()
    }

    @BindingAdapter(
        value = ["setCurrencyTextWatcher", "custom:currencyListener"],
        requireAll = false
    )
    @JvmStatic
    fun updateCurrencyText(
        view: TextInputEditText,
        text: String?,
        currencyListener: InverseBindingListener
    ) {
        val newValue = object : TextWatcher {

            var editing = false

            override fun afterTextChanged(s: Editable?) {
                if (!editing) {
                    editing = true
                    val digits = s.toString().replace("\\D".toRegex(), "")
                    val numberFormat = NumberFormat.getCurrencyInstance()
                    try {
                        val formatted = numberFormat.format(digits.toDouble() / 100)
                        s!!.replace(0, s.length, formatted)
                    } catch (nfe: NumberFormatException) {
                        s!!.clear()
                    }
                    editing = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                currencyListener.onChange()
            }
        }
        val oldValue = ListenerUtil.trackListener(view, newValue, view.id)
        if (oldValue != null) {
            view.removeTextChangedListener(oldValue)
        }
        view.addTextChangedListener(newValue)
    }


    @InverseBindingAdapter(
        attribute = "setPhoneNumberTextWatcher",
        event = "custom:phoneNumberListener"
    )
    @JvmStatic
    fun getPhoneNumberText(view: TextInputEditText): String {
        return view.text.toString()
    }


    @BindingAdapter(
        value = ["setPhoneNumberTextWatcher", "custom:phoneNumberListener"],
        requireAll = false
    )
    @JvmStatic
    fun updatePhoneNumberText(
        view: TextInputEditText,
        text: String?,
        phoneNumberListener: InverseBindingListener
    ) {
        Log.v("BINDING", "view = ${view.text} text = $text")
        val newValue = object : PhoneNumberFormattingTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                Log.v("BINDING", "onTextChanged")

                phoneNumberListener.onChange()
            }
        }

        val oldValue = ListenerUtil.trackListener(view, newValue, view.id)
        if (oldValue != null) {
            view.removeTextChangedListener(oldValue)
        }
        view.addTextChangedListener(newValue)
    }
}

