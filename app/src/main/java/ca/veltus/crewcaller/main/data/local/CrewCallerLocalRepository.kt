package ca.veltus.crewcaller.main.data.local

import android.util.Log
import androidx.lifecycle.MutableLiveData
import ca.veltus.crewcaller.main.data.CrewCallerDataSource
import ca.veltus.crewcaller.main.data.dto.*
import ca.veltus.crewcaller.main.data.dto.relations.ProductionWithWorkDaysWithContacts
import ca.veltus.crewcaller.main.data.dto.relations.WorkDayAndPayRate
import ca.veltus.crewcaller.main.data.network.NetworkApiStatus
import ca.veltus.crewcaller.main.data.network.WeatherApi
import ca.veltus.crewcaller.main.data.network.asDatabaseModel
import ca.veltus.crewcaller.main.preferences.PreferencesFragment
import ca.veltus.crewcaller.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class CrewCallerLocalRepository(
    private val crewCallerDao: CrewCallerDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CrewCallerDataSource {

    companion object {
        const val TAG = "RepositoryLog"
    }

    private val networkStatus = MutableLiveData<NetworkApiStatus>()

    // Get all Contacts
    override suspend fun getContacts(): Result<List<ContactDTO>> = withContext(ioDispatcher) {
        return@withContext try {
            Result.Success(crewCallerDao.getContacts())
        } catch (ex: Exception) {
            Result.Error("Contacts not found")
        }
    }

    // Get a list of all Contact with Id
    override suspend fun getContactIdList(id: String): Result<List<String>> =
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                return@withContext try {
                    Result.Success(crewCallerDao.getContactIdList(id))
                } catch (ex: Exception) {
                    Result.Error("No Contacts found")
                }
            }
        }

    // Get a list of all Contacts with production name
    override suspend fun getContactsFromProduction(productionName: String): Result<List<ContactDTO>> =
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                return@withContext try {
                    Result.Success(crewCallerDao.getContactsFromProduction(productionName))
                } catch (ex: Exception) {
                    Result.Error("No Contacts found")
                }
            }
        }

    // Get all PayRates
    override suspend fun getPayRates(): Result<List<PayRateDTO>> = wrapEspressoIdlingResource {
        withContext(ioDispatcher) {
            return@withContext try {
                Result.Success(crewCallerDao.getPayRates())
            } catch (ex: Exception) {
                Result.Error("PayRates not found")
            }
        }
    }

    // Get all Productions
    override suspend fun getProductions(): Result<List<ProductionDTO>> =
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                return@withContext try {
                    Result.Success(crewCallerDao.getProductions())
                } catch (ex: Exception) {
                    Result.Error("Productions not found")
                }
            }
        }

    // Get a list of all Production Ids
    override suspend fun getProductionList(): Result<Array<String>> = wrapEspressoIdlingResource {
        withContext(ioDispatcher) {
            return@withContext try {
                Result.Success(crewCallerDao.getProductionList())
            } catch (ex: Exception) {
                Result.Error("Productions not found")
            }
        }
    }


    // Get all WorkDays
    override suspend fun getWorkDays(): Result<List<WorkDayDTO>> = wrapEspressoIdlingResource {
        withContext(ioDispatcher) {
            return@withContext try {
                Result.Success(crewCallerDao.getWorkDays())
            } catch (ex: Exception) {
                Result.Error("WorkDays not found")
            }
        }
    }

    // Get all WorkDays for Production
    override suspend fun getWorkDaysForProduction(production: String): Result<List<WorkDayDTO>> =
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                return@withContext try {
                    Result.Success(crewCallerDao.getWorkDaysForProduction(production))
                } catch (ex: Exception) {
                    Result.Error("WorkDays not found")
                }
            }
        }

    // Save Contact
    override suspend fun saveContact(contactDTO: ContactDTO) = wrapEspressoIdlingResource {
        Log.v(PreferencesFragment.TAG, "saveContact Called")
        withContext(ioDispatcher) {
            crewCallerDao.saveContact(contactDTO)
        }
    }

    // Save PayRate
    override suspend fun savePayRate(payRateDTO: PayRateDTO) = wrapEspressoIdlingResource {
        Log.v(PreferencesFragment.TAG, "savePayRate Called")
        withContext(ioDispatcher) {
            crewCallerDao.savePayRate(payRateDTO)

        }
    }

    // Save Production
    override suspend fun saveProduction(productionDTO: ProductionDTO) = wrapEspressoIdlingResource {
        Log.v(PreferencesFragment.TAG, "saveProduction Called")
        withContext(ioDispatcher) {
            crewCallerDao.saveProduction(productionDTO)
        }
    }

    // Save WorkDay
    override suspend fun saveWorkDay(workDayDTO: WorkDayDTO) = wrapEspressoIdlingResource {
        Log.v(PreferencesFragment.TAG, "saveWorkDay Called")
        withContext(ioDispatcher) {
            crewCallerDao.saveWorkDay(workDayDTO)
        }
    }

    // Get Contact by id
    override suspend fun getContact(id: String): Result<ContactDTO> = wrapEspressoIdlingResource {
        withContext(ioDispatcher) {
            try {
                val contact = crewCallerDao.getContactById(id)
                if (contact != null) {
                    return@withContext Result.Success(contact)
                } else {
                    return@withContext Result.Error("Contact not found!")
                }
            } catch (e: Exception) {
                return@withContext Result.Error(e.localizedMessage)
            }
        }
    }

    // Get PayRate by id
    override suspend fun getPayRate(id: String): Result<PayRateDTO> = wrapEspressoIdlingResource {
        withContext(ioDispatcher) {
            try {
                val payRate = crewCallerDao.getPayRateById(id)
                if (payRate != null) {
                    return@withContext Result.Success(payRate)
                } else {
                    return@withContext Result.Error("PayRate not found!")
                }
            } catch (e: Exception) {
                return@withContext Result.Error(e.localizedMessage)
            }
        }
    }

    // Get Production by id
    override suspend fun getProduction(id: String): Result<ProductionDTO> =
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                try {
                    val production = crewCallerDao.getProductionById(id)
                    if (production != null) {
                        return@withContext Result.Success(production)
                    } else {
                        return@withContext Result.Error("Production not found!")
                    }
                } catch (e: Exception) {
                    return@withContext Result.Error(e.localizedMessage)
                }
            }
        }

    // Get WorkDay by id
    override suspend fun getWorkDay(id: String): Result<WorkDayDTO> = wrapEspressoIdlingResource {
        withContext(ioDispatcher) {
            try {
                val workDay = crewCallerDao.getWorkDayById(id)
                if (workDay != null) {
                    return@withContext Result.Success(workDay)
                } else {
                    return@withContext Result.Error("WorkDay not found!")
                }
            } catch (e: Exception) {
                return@withContext Result.Error(e.localizedMessage)
            }
        }
    }

    // Delete all Contacts
    override suspend fun deleteAllContacts() = wrapEspressoIdlingResource {
        withContext(ioDispatcher) {
            crewCallerDao.deleteAllContacts()
        }
    }

    // Delete all PayRates
    override suspend fun deleteAllPayRates() = wrapEspressoIdlingResource {
        withContext(ioDispatcher) {
            crewCallerDao.deleteAllPayRates()
        }
    }

    // Delete all Productions
    override suspend fun deleteAllProductions() = wrapEspressoIdlingResource {
        withContext(ioDispatcher) {
            crewCallerDao.deleteAllProductions()
        }
    }

    // Delete all WorkDays
    override suspend fun deleteAllWorkDays() = wrapEspressoIdlingResource {
        withContext(ioDispatcher) {
            crewCallerDao.deleteAllWorkDays()
        }
    }

    // Delete Contact with id
    override suspend fun deleteContact(id: String) = wrapEspressoIdlingResource {
        withContext(ioDispatcher) {
            crewCallerDao.deleteContact(id)
        }
    }

    // Delete PayRate with id
    override suspend fun deletePayRate(id: String) = wrapEspressoIdlingResource {
        withContext(ioDispatcher) {
            crewCallerDao.deletePayRate(id)
        }
    }

    // Delete Production with id
    override suspend fun deleteProduction(id: String) = wrapEspressoIdlingResource {
        withContext(ioDispatcher) {
            crewCallerDao.deleteProduction(id)
        }
    }

    // Delete WorkDay with id
    override suspend fun deleteWorkDay(id: String) = wrapEspressoIdlingResource {
        withContext(ioDispatcher) {
            crewCallerDao.deleteWorkDay(id)
        }
    }

    // Update selected WorkDay end time
    override suspend fun updateWorkDayEndTime(id: String, time: String) =
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                crewCallerDao.updateWorkDayEndTime(id, time)
            }
        }


    // Get the current weather forecast at the provided latLong
    override suspend fun getWeatherForecast(latitude: String, longitude: String) =
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                try {
                    networkStatus.postValue(NetworkApiStatus.LOADING)

                    crewCallerDao.saveCurrentWeather(
                        WeatherApi.retrofitService.getWeatherForecast(
                            latitude,
                            longitude
                        ).asDatabaseModel()
                    )

                    networkStatus.postValue(NetworkApiStatus.DONE)

                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.v(TAG, "Error $e")
                    networkStatus.postValue(NetworkApiStatus.ERROR)

                }
            }
        }

    // Get the current weather saved
    override suspend fun getCurrentWeather(): Result<WeatherDTO> = wrapEspressoIdlingResource {
        withContext(ioDispatcher) {
            return@withContext try {
                Result.Success(crewCallerDao.getCurrentWeather())
            } catch (ex: Exception) {
                Result.Error("No Weather Found")
            }
        }
    }

    // Get Production with corresponding WorkDays and Contacts
    override suspend fun loadProductionWithWorkDaysWithContacts(productionId: String): Result<ProductionWithWorkDaysWithContacts> =
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                return@withContext try {
                    Result.Success(crewCallerDao.loadProductionWithWorkDaysWithContacts(productionId))
                } catch (ex: Exception) {
                    Result.Error("No Production Found")
                }
            }
        }

    // Get WorkDays with corresponding PayRates
    override suspend fun loadWorkDaysAndPayRate(today: String): Result<List<WorkDayAndPayRate>> =
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                return@withContext try {
                    Result.Success(crewCallerDao.loadWorkDaysAndPayRate(today))
                } catch (ex: Exception) {
                    Result.Error("WorkDays not found")
                }
            }
        }

    // Get WorkDay with corresponding PayRate
    override suspend fun loadWorkDayAndPayRateFromId(workDayId: String): Result<WorkDayAndPayRate> =
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                return@withContext try {
                    Result.Success(crewCallerDao.loadWorkDayAndPayRateFromId(workDayId))
                } catch (ex: Exception) {
                    Result.Error("WorkDay not found")
                }
            }
        }
}
















