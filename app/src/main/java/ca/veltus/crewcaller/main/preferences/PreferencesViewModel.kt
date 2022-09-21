package ca.veltus.crewcaller.main.preferences

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.base.BaseViewModel
import ca.veltus.crewcaller.main.data.CrewCallerDataSource
import ca.veltus.crewcaller.main.data.dto.*
import ca.veltus.crewcaller.main.data.dto.relations.*
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.*
import java.time.LocalDateTime
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PreferencesViewModel(val app: Application, val dataSource: CrewCallerDataSource) :
    BaseViewModel(app) {

    companion object {
        const val TAG = "PreferencesViewModel"
    }

    private val payRateList = MutableLiveData<List<PayRateDTO>?>()
    private val productionList = MutableLiveData<List<ProductionDTO>?>()
    private val contactList = MutableLiveData<List<ContactDTO>?>()
    private val workDayList = MutableLiveData<List<WorkDayDTO>?>()

    val backUpDatabase = MutableLiveData<BackUpDatabase?>()

    private val _uid = MutableLiveData<String?>()
    val uid: LiveData<String?>
        get() = _uid

    fun clearValues() {
        payRateList.value = null
        productionList.value = null
        contactList.value = null
        workDayList.value = null
        backUpDatabase.value = null
        _uid.value = null

    }

    // Load all saved WorkDays and add them to LiveData list.
    private fun loadWorkDays() {
        viewModelScope.launch {
            val result = dataSource.getWorkDays()
            when (result) {
                is Result.Success<*> -> {
                    val dataList = ArrayList<WorkDayDTO>()
                    dataList.addAll(result.data as List<WorkDayDTO>)
                    workDayList.value = dataList
                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }
        }
        loadProductions()
    }

    // Load all saved Productions and add them to LiveData list.
    private fun loadProductions() {
        viewModelScope.launch {
            val result = dataSource.getProductions()
            when (result) {
                is Result.Success<*> -> {
                    val dataList = ArrayList<ProductionDTO>()
                    dataList.addAll(result.data as List<ProductionDTO>)
                    productionList.value = dataList
                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }
        }
        loadPayRates()
    }

    // Load all saved PayRates and add them to LiveData list.
    private fun loadPayRates() {
        viewModelScope.launch {
            val result = dataSource.getPayRates()
            when (result) {
                is Result.Success<*> -> {
                    val dataList = ArrayList<PayRateDTO>()
                    dataList.addAll(result.data as List<PayRateDTO>)
                    payRateList.value = dataList

                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }
        }
        loadContacts()
    }

    // Load all saved Contracts and add them to LiveData list.
    private fun loadContacts() {
        viewModelScope.launch {
            val result = dataSource.getContacts()
            when (result) {
                is Result.Success<*> -> {
                    val dataList = ArrayList<ContactDTO>()
                    dataList.addAll(result.data as List<ContactDTO>)
                    contactList.value = dataList
                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }
        }
    }

    // Get Firebase user uid from provided FirebaseUser. Return true if successful.
    fun getFirebaseUserUid(firebaseUser: FirebaseUser?): Boolean {
        return if (firebaseUser == null) {
            showSnackBar.value = app.getString(R.string.unableToConnectFirebaseAccountToast)
            false
        } else {
            _uid.value = firebaseUser.uid
            loadWorkDays()
            true
        }
    }

    // Create HashMap of all saved entries in database.
    fun buildHashMap(): HashMap<String, Any> {
        val userHashMap = HashMap<String, Any>()
        userHashMap["date"] = LocalDateTime.now().toString()
        userHashMap["contacts"] = contactList.value!!
        userHashMap["productions"] = productionList.value!!
        userHashMap["work_days"] = workDayList.value!!
        userHashMap["pay_rates"] = payRateList.value!!
        return userHashMap
    }

    // Restore Firebase backup document and save to database. Return true if task was successful.
    fun restoreDatabaseBackup(): Boolean {
        showLoading.postValue(true)
        val backup = backUpDatabase.value
        if (backup != null) {
            val contactsBackup = ArrayList<ContactDTO>()
            val productionsBackup = ArrayList<ProductionDTO>()
            val workDaysBackup = ArrayList<WorkDayDTO>()
            val payRatesBackup = ArrayList<PayRateDTO>()

            // Add all entries to ArrayLists and map them to database model.
            contactsBackup.addAll((backup.contacts as List<ContactFirebaseObject>).map { it.asDatabaseModel() })
            productionsBackup.addAll((backup.productions as List<ProductionFirebaseObject>).map { it.asDatabaseModel() })
            workDaysBackup.addAll((backup.work_days as List<WorkDayFirebaseObject>).map { it.asDatabaseModel() })
            payRatesBackup.addAll((backup.pay_rates as List<PayRateFirebaseObject>).map { it.asDatabaseModel() })

            // Block return value until suspend functions finishes.
            runBlocking {
                coroutineScope {
                    launch {
                        contactsBackup.forEach { dataSource.saveContact(it) }
                        productionsBackup.forEach { dataSource.saveProduction(it) }
                        workDaysBackup.forEach { dataSource.saveWorkDay(it) }
                        payRatesBackup.forEach { dataSource.savePayRate(it) }
                    }
                }
            }

            showLoading.postValue(false)
            return true

        } else {
            showLoading.postValue(false)
            return false
        }
    }

    fun setBackupDatabaseItem(data: BackUpDatabase?) {
        showLoading.postValue(true)
        if (data != null) {
            backUpDatabase.value = data
        } else {
            showSnackBar.value = app.getString(R.string.backupDatabaseItemNullToast)
        }
        showLoading.postValue(false)
    }
}
