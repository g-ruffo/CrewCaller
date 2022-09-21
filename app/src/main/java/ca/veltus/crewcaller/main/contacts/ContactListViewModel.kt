package ca.veltus.crewcaller.main.contacts

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.base.BaseViewModel
import ca.veltus.crewcaller.base.NavigationCommand
import ca.veltus.crewcaller.main.data.CrewCallerDataSource
import ca.veltus.crewcaller.main.data.dataitem.ContactDataItem
import ca.veltus.crewcaller.main.data.dataitem.asDatabaseModel
import ca.veltus.crewcaller.main.data.dto.*
import ca.veltus.crewcaller.utils.trimSpace
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ContactListViewModel(val app: Application, val dataSource: CrewCallerDataSource) :
    BaseViewModel(app) {

    val contactsList = MutableLiveData<List<ContactDataItem>>()

    val contactFirstName = MutableLiveData<String?>()
    val contactLastName = MutableLiveData<String?>()
    val contactPhone = MutableLiveData<String?>()
    val contactEmail = MutableLiveData<String?>()
    val contactPosition = MutableLiveData<String?>()
    val contactFacebook = MutableLiveData<String?>()
    val contactInstagram = MutableLiveData<String?>()
    val contactId = MutableLiveData<String?>()
    val contactProduction = MutableLiveData<String?>()

    val _productionListArray = MutableLiveData<Array<String>>()
    val productionListArray: LiveData<Array<String>>
        get() = _productionListArray

    val enableEditing = MutableLiveData<Boolean>()
    val filteredContactList = MutableLiveData<Boolean>()


    init {
        enableEditing.value = true
        filteredContactList.value = false
    }

    fun clearValues() {
        contactFirstName.value = null
        contactLastName.value = null
        contactEmail.value = null
        contactPhone.value = null
        contactPosition.value = null
        contactFacebook.value = null
        contactInstagram.value = null
        contactId.value = null
        contactProduction.value = null
        enableEditing.value = true
    }

    // Load all saved Contacts from Room.
    fun loadContacts() {
        filteredContactList.value = false
        viewModelScope.launch {
            val result = dataSource.getContacts()
            when (result) {
                is Result.Success<*> -> {

                    // If successful map retrieved database models to domain models to display in Ui.
                    val dataList = ArrayList<ContactDataItem>()
                    dataList.addAll((result.data as List<ContactDTO>).map { it.asDomainModel() })
                    contactsList.value = dataList
                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }
            // Check if no data has to be shown
            invalidateShowNoData()
        }
    }

    // Load selected Contact by id for Detail and Save Fragments.
    fun loadSelectedContactId(id: String) {
        viewModelScope.launch {
            val result = dataSource.getContact(id)
            when (result) {
                is Result.Success<*> -> {

                    // If successful map retrieved database models to domain models to display in Ui.
                    val contact = (result.data as ContactDTO).asDomainModel()
                    contactFirstName.value = contact.firstName
                    contactLastName.value = contact.lastName
                    contactPhone.value = contact.phone
                    contactPosition.value = contact.position
                    contactEmail.value = contact.email
                    contactFacebook.value = contact.facebook
                    contactInstagram.value = contact.instagram
                    contactId.value = contact.id
                    contactProduction.value = contact.production
                    enableEditing.value = false
                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }
            // Check if no data has to be shown
            invalidateShowNoData()
        }
    }

    // Filter Contacts by production.
    fun loadSelectedContactList(production: String?): Boolean {
        filteredContactList.value = true

        // If production is null, return false and load all Contacts.
        return if (production.isNullOrEmpty()) {
            false
        } else {
            viewModelScope.launch {
                val result = dataSource.getContactsFromProduction(production)
                when (result) {
                    is Result.Success<*> -> {

                        // If successful map retrieved database models to domain models to display in Ui.
                        val dataList = ArrayList<ContactDataItem>()
                        dataList.addAll((result.data as List<ContactDTO>).map { it.asDomainModel() })
                        contactsList.value = dataList
                    }
                    is Result.Error ->
                        showSnackBar.value = result.message
                }

                // Check if no data has to be shown
                invalidateShowNoData()
            }
            true
        }
    }

    // Load all saved Production names into a list to be displayed in Spinner.
    fun getProductionsList() {
        viewModelScope.launch {
            val result = dataSource.getProductionList()
            when (result) {
                is Result.Success<*> -> {

                    // If successful map retrieved database models to domain models to display in Ui.
                    val dataList = ArrayList<String>()
                    dataList.addAll((result.data as Array<String>).map { it })
                    _productionListArray.value = dataList.toTypedArray()
                }

                is Result.Error ->
                    showSnackBar.value = result.message
            }
        }
    }

    // Combine all user inputted fields into a single ContactDataItem and validate before saving.
    fun saveContact() {
        val firstName = contactFirstName.value
        val lastName = contactLastName.value
        val phone = contactPhone.value
        val position = contactPosition.value
        val email = contactEmail.value
        val facebook = contactFacebook.value
        val instagram = contactInstagram.value
        val production = contactProduction.value
        val id = contactId.value ?: UUID.randomUUID().toString()

        validateAndSaveContact(
            ContactDataItem(
                firstName!!,
                lastName,
                phone,
                position,
                email,
                facebook,
                instagram,
                production,
                id
            )
        )
    }

    // Ensure all required fields are valid and if so save Contact to database.
    private fun validateAndSaveContact(contact: ContactDataItem) {
        contact.firstName = contact.firstName.trimSpace()
        if (contactFirstName.value.isNullOrBlank() || contactFirstName.value.isNullOrEmpty()) {
            showSnackBar.value = app.getString(R.string.firstNameRequiredToast)
            return
        }

        // Save Contact and navigate back.
        viewModelScope.launch {
            dataSource.saveContact(contact.asDatabaseModel())
            showToast.value = app.getString(R.string.contactSavedToast)
            navigationCommand.value = NavigationCommand.Back
        }
    }

    // Generate Uri from phone number for intent.
    fun generatePhoneNumber(): Uri? {
        return if (contactPhone.value.isNullOrEmpty()) {
            null
        } else {
            Uri.parse("tel:" + contactPhone.value)
        }
    }

    // Generate Uri from email address for intent.
    fun generateEmail(): Uri? {
        return if (contactEmail.value.isNullOrEmpty()) {
            null
        } else {
            Uri.parse("mailto:" + contactEmail.value)
        }
    }

    // Generate Uri from Facebook username for intent.
    fun generateFacebook(): Uri? {
        return if (contactFacebook.value.isNullOrEmpty()) {
            null
        } else {
            Uri.parse("fb://profile/" + contactFacebook.value)
        }
    }

    // Generate Uri from Instagram username for intent.
    fun generateInstagram(): Uri? {
        return if (contactInstagram.value.isNullOrEmpty()) {
            null
        } else {
            Uri.parse("http://instagram.com/_u/" + contactInstagram.value)
        }
    }

    // Inform the user that there's not any data if the contactsList is empty.
    private fun invalidateShowNoData() {
        showNoData.value = contactsList.value == null || contactsList.value!!.isEmpty()
    }

    fun navigateToAddNewFragment() {
        navigationCommand.postValue(
            NavigationCommand.To(
                ContactListFragmentDirections.actionContactsFragmentToSaveContactFragment(
                    null
                )
            )
        )
    }

    // Delete Contact and navigate back.
    fun deleteContact() {
        viewModelScope.launch {
            dataSource.deleteContact(contactId.value!!)
            showToast.value = app.getString(R.string.contactDeletedToast)
            navigationCommand.value = NavigationCommand.BackTo(R.id.contactsFragment)
            clearValues()
        }
    }

    fun navigateBack() {
        navigationCommand.postValue(NavigationCommand.Back)
    }
}