package ca.veltus.crewcaller.main.payrates

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.base.BaseViewModel
import ca.veltus.crewcaller.base.NavigationCommand
import ca.veltus.crewcaller.main.data.CrewCallerDataSource
import ca.veltus.crewcaller.main.data.dataitem.PayRateDataItem
import ca.veltus.crewcaller.main.data.dataitem.asDatabaseModel
import ca.veltus.crewcaller.main.data.dto.*
import ca.veltus.crewcaller.utils.trimSpace
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class PayRateListViewModel(val app: Application, val dataSource: CrewCallerDataSource) :
    BaseViewModel(app) {

    val payRateList = MutableLiveData<List<PayRateDataItem>>()

    private val _tierListArray = MutableLiveData<Array<String>>()
    val tierListArray: LiveData<Array<String>>
        get() = _tierListArray

    val payRateTier = MutableLiveData<String?>()
    val payRatePosition = MutableLiveData<String?>()
    val payRateRate = MutableLiveData<String?>()
    val payRateId = MutableLiveData<String?>()

    val enableEditing = MutableLiveData<Boolean>()

    init {
        enableEditing.value = true
        _tierListArray.value = arrayOf("Tier One", "Tier Two", "Feature Rate", "TV Rate")
    }

    fun clearValues() {
        payRateTier.value = null
        payRatePosition.value = null
        payRateRate.value = null
        payRateId.value = null
        enableEditing.value = true
    }

    // Load all saved PayRates.
    fun loadPayRates() {
        viewModelScope.launch {
            val result = dataSource.getPayRates()
            when (result) {
                is Result.Success<*> -> {

                    // If successful map retrieved database models to domain models to display in Ui.
                    val dataList = ArrayList<PayRateDataItem>()
                    dataList.addAll((result.data as List<PayRateDTO>).map { it.asDomainModel() })
                    payRateList.value = dataList
                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }

            //check if no data has to be shown
            invalidateShowNoData()
        }
    }

    // Create PayRateDataItem from field inputs and validate before saving.
    fun savePayRate() {
        val tier = payRateTier.value
        val position = payRatePosition.value
        val rate = payRateRate.value
        val id = payRateId.value ?: UUID.randomUUID().toString()

        validateAndSavePayRate(PayRateDataItem(id, tier!!, position!!, rate!!))
    }

    // Check that all required fields are valid and if so save PayRate and navigate back.
    private fun validateAndSavePayRate(payRate: PayRateDataItem) {
        payRate.position = payRate.position.trimSpace()
        payRate.rate = payRate.rate.trimSpace()

        if (payRateTier.value.isNullOrBlank() || payRateTier.value.isNullOrEmpty()) {
            showSnackBar.value = app.getString(R.string.selectTierToast)
            return
        }

        if (payRate.position.isNullOrBlank() || payRate.position.isNullOrEmpty()) {
            showSnackBar.value = app.getString(R.string.enterYourPositionToast)
            return
        }
        if (payRate.rate.isNullOrBlank() || payRate.rate.isNullOrEmpty()) {
            showSnackBar.value = app.getString(R.string.enterYourRateToast)
            return
        }
        viewModelScope.launch {
            dataSource.savePayRate(payRate.asDatabaseModel())
            showToast.value = app.getString(R.string.payRateSavedToast)
            navigationCommand.value = NavigationCommand.Back
        }
    }

    // Load selected PayRate into the view model. If false user is trying to create a new PayRate.
    fun loadSelectedPayRate(payRate: PayRateDataItem?): Boolean {
        return if (payRate == null) {
            false
        } else {
            payRateId.value = payRate.id
            payRateTier.value = payRate.tier
            payRatePosition.value = payRate.position
            payRateRate.value = payRate.rate
            enableEditing.value = false
            true
        }
    }

    // Inform the user that there's not any data if the payRateList is empty.
    private fun invalidateShowNoData() {
        showNoData.value = payRateList.value == null || payRateList.value!!.isEmpty()
    }

    fun navigateToAddNewFragment() {
        navigationCommand.postValue(NavigationCommand.To(PayRateListFragmentDirections.actionPayRateListFragmentToSavePayRateFragment()))
    }

    /**
     *  This feature will be added in future release.
     */
//    fun deletePayRate() {
//        viewModelScope.launch {
//            dataSource.deletePayRate(payRateId.value!!)
//            showToast.value = app.getString(R.string.payRateDeletedToast)
//            navigationCommand.value = NavigationCommand.Back
//            clearValues()
//        }
//    }

    fun navigateBack() {
        navigationCommand.postValue(NavigationCommand.Back)
    }
}