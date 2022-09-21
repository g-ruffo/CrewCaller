package ca.veltus.crewcaller.main.productions

import android.app.Application
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.base.BaseViewModel
import ca.veltus.crewcaller.base.NavigationCommand
import ca.veltus.crewcaller.main.data.CrewCallerDataSource
import ca.veltus.crewcaller.main.data.dataitem.*
import ca.veltus.crewcaller.main.data.dto.*
import ca.veltus.crewcaller.main.data.dto.relations.ProductionWithWorkDaysWithContacts
import ca.veltus.crewcaller.main.productions.detailproduction.DetailProductionFragmentDirections
import ca.veltus.crewcaller.main.productions.saveproduction.SaveProductionFragmentDirections
import ca.veltus.crewcaller.utils.calculateEarnings
import ca.veltus.crewcaller.utils.trimSpace
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ProductionsListViewModel(val app: Application, val dataSource: CrewCallerDataSource) :
    BaseViewModel(app) {

    companion object {
        const val TAG = "ProductionsListVMLog"
    }

    val productionsList = MutableLiveData<List<ProductionDataItem>>()

    val workDayList = MutableLiveData<List<WorkDayDataItem>>()

    val contactList = MutableLiveData<List<ContactDataItem>>()

    val productionName = MutableLiveData<String?>()
    val productionAddress = MutableLiveData<String?>()
    val productionPhone = MutableLiveData<String?>()
    val productionEmail = MutableLiveData<String?>()
    val productionId = MutableLiveData<String?>()

    private val productionDaysWorked = MutableLiveData<String>()

    val productionTotalEarnings = MutableLiveData<Double>()
    val enableEditing = MutableLiveData<Boolean>()
    var updateProduction: Boolean = true


    init {
        enableEditing.value = true
        updateProduction = false
        productionDaysWorked.value = "0"
        productionTotalEarnings.value = 0.00
    }


    fun clearValues() {
        productionName.value = null
        productionAddress.value = null
        productionPhone.value = null
        productionEmail.value = null
        productionId.value = null
        enableEditing.value = true
        updateProduction = false
        productionTotalEarnings.value = 0.00
        workDayList.value = listOf()
        workDayList.value = listOf()

    }

    // Load Production by id with corresponding WorkDays and Contacts
    fun loadProductionsWithWorkDaysAndContacts(id: String) {
        viewModelScope.launch {
            val result = dataSource.loadProductionWithWorkDaysWithContacts(id)
            when (result) {
                is Result.Success<*> -> {
                    val item = (result.data as ProductionWithWorkDaysWithContacts)
                    val workDays = ArrayList<WorkDayDataItem>()
                    val contacts = ArrayList<ContactDataItem>()
                    val production = item.production.asDomainModel()
                    productionName.value = production.name
                    productionAddress.value = production.address
                    productionPhone.value = production.phoneNumber
                    productionEmail.value = production.email
                    productionId.value = production.id
                    enableEditing.value = false
                    updateProduction = true

                    // Add all WorkDays and Contacts to live data lists and map them as domain models.
                    workDays.addAll((item.workDays as List<WorkDayDTO>).map { it.asDomainModel() })
                    contacts.addAll((item.contacts as List<ContactDTO>).map { it.asDomainModel() })

                    workDayList.value = workDays
                    contactList.value = contacts

                    // If WorkDays is not empty calculate the total earnings.
                    if (workDays.isNotEmpty()) {
                        getEarningsFromDaysWorked(workDays)
                    }
                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }
        }
    }

    // Load all saved Productions and map them to domain model.
    fun loadProductions() {
        viewModelScope.launch {
            val result = dataSource.getProductions()
            when (result) {
                is Result.Success<*> -> {
                    val dataList = ArrayList<ProductionDataItem>()
                    dataList.addAll((result.data as List<ProductionDTO>).map { it.asDomainModel() })
                    productionsList.value = dataList
                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }
            // Check if no data has to be shown
            invalidateShowNoData()
        }
    }

    // Calculate the earnings from each WorkDay and set total for display in Ui.
    private fun getEarningsFromDaysWorked(dataList: ArrayList<WorkDayDataItem>) {
        viewModelScope.launch {
            val result = dataSource.getPayRates()
            when (result) {
                is Result.Success<*> -> {
                    val payRateList = ArrayList<PayRateDataItem>()
                    payRateList.addAll((result.data as List<PayRateDTO>).map { it.asDomainModel() })

                    var earnings = 0.0

                    dataList.forEach {
                        if (!it.startTime.isNullOrEmpty() && !it.endTime.isNullOrEmpty() && !it.rate.isNullOrEmpty()) {
                            var payRateId = it.rate
                            var payRate = payRateList.find { it.id == payRateId }
                            earnings += calculateEarnings(
                                payRate!!.rate,
                                null,
                                it.startTime,
                                it.endTime
                            ).first
                        }
                        productionTotalEarnings.value = earnings

                    }
                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }
        }
    }

    // Create ProductionDataItem from input fields and validate before saving.
    fun saveProduction() {
        val name = productionName.value
        val address = productionAddress.value
        val phone = productionPhone.value
        val email = productionEmail.value
        val id = productionId.value ?: UUID.randomUUID().toString()

        validateAndSaveNewProduction(
            ProductionDataItem(
                name!!,
                address,
                phone,
                email,
                id
            )
        )
    }

    // Check that all required data is valid and if so save to database.
    private fun validateAndSaveNewProduction(productionDataItem: ProductionDataItem) {
        productionDataItem.name = productionDataItem.name.trimSpace()
        if (productionName.value.isNullOrBlank() || productionName.value.isNullOrEmpty()) {
            showSnackBar.value = app.getString(R.string.properProductionNameRequiredToast)
            return
        }

        viewModelScope.launch {
            dataSource.saveProduction(productionDataItem.asDatabaseModel())
            showToast.value = app.getString(R.string.productionSaved)

            // If user is updating an existing production, navigate back to detail page.
            if (updateProduction) {
                navigationCommand.postValue(
                    NavigationCommand.To(
                        SaveProductionFragmentDirections.actionSaveProductionFragmentToDetailProductionFragment(
                            productionDataItem.id
                        )
                    )
                )
            } else {
                navigateBack()
            }
        }
    }

    // Inform the user that there's not any data if the productionsList is empty
    private fun invalidateShowNoData() {
        showNoData.value = productionsList.value == null || productionsList.value!!.isEmpty()
    }

    fun navigateToAddNewFragment() {
        navigationCommand.postValue(
            NavigationCommand.To(
                ProductionsListFragmentDirections.actionProductionsFragmentToNewProductionFragment(
                    null
                )
            )
        )
    }

    private fun navigateToProductionListFragment() {
        navigationCommand.postValue(NavigationCommand.BackTo(R.id.productionsFragment))
    }

    fun navigateBack() {
        navigationCommand.postValue(NavigationCommand.Back)
    }

    // Generate Uri from phone number for intent.
    fun generatePhoneNumber(): Uri? {
        return if (productionPhone.value.isNullOrEmpty()) {
            null
        } else {
            Uri.parse("tel:" + productionPhone.value)
        }
    }

    // Generate Uri from email for intent.
    fun generateEmail(): Uri? {
        return if (productionEmail.value.isNullOrEmpty()) {
            null
        } else {
            Uri.parse("mailto:" + productionEmail.value)
        }
    }

    // Generate Uri from address for intent.
    fun generateAddress(): Uri? {
        return if (productionAddress.value.isNullOrEmpty()) {
            null
        } else {
            Uri.parse("google.navigation:mode=d&q=" + Uri.encode(productionAddress.value))
        }
    }

    // Check to see if there are any contacts associated with the given production. If so, navigate to the ContactListFragment.
    fun navigateToProductionContactList() {
        val contactArray = contactList.value
        val production = productionName.value
        if (!contactArray.isNullOrEmpty()) {
            val action =
                DetailProductionFragmentDirections.actionDetailProductionFragmentToContactsFragment()
            action.production = production
            navigationCommand.postValue(NavigationCommand.To(action))
        }
    }

    fun deleteProduction() {
        viewModelScope.launch {
            dataSource.deleteProduction(productionId.value!!)
            showToast.value = app.getString(R.string.productionDeletedToast)
            navigateToProductionListFragment()
            clearValues()
        }
    }
}