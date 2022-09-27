package ca.veltus.crewcaller.main.payrates

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ca.veltus.crewcaller.MainCoroutineRule
import ca.veltus.crewcaller.main.data.FakeCrewCallerDataSource
import ca.veltus.crewcaller.main.data.dto.PayRateDTO
import org.hamcrest.MatcherAssert.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(AndroidJUnit4::class)
class PayRateListViewModelTest {
    private lateinit var payRateListViewModel: PayRateListViewModel
    private val dataSource = FakeCrewCallerDataSource()
    private lateinit var app: Application

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    @Before
    fun initSetup() {
        stopKoin()
        app = ApplicationProvider.getApplicationContext()
        payRateListViewModel = PayRateListViewModel(app, dataSource)
    }

    @Test
    fun loadPayRates_listOfSavedPayRates_returnsSizeTen() {
        runBlocking {
            for (i in 0..9) {
                dataSource.savePayRate(PayRateDTO("id$i", "tier$i", "position$i", "${i}00.00"))
            }
            payRateListViewModel.loadPayRates()

            val result = payRateListViewModel.payRateList.value!!.size

            assertThat(result, IsEqual(10))
        }
    }

    @Test
    fun savePayRate_validValues_payRateAddedToDatabase() {
        payRateListViewModel.payRateTier.value = "tier"
        payRateListViewModel.payRatePosition.value = "position"
        payRateListViewModel.payRateRate.value = "pay rate"
        payRateListViewModel.payRateId.value = "id"

        runBlocking { payRateListViewModel.savePayRate()

            val result = dataSource.payRates!!.size

            assertThat(result, IsEqual(1))
        }
    }

    @Test
    fun savePayRate_invalidTier_snackBarValueSet() {
        payRateListViewModel.payRateTier.value = ""
        payRateListViewModel.payRatePosition.value = "position"
        payRateListViewModel.payRateRate.value = "pay rate"
        payRateListViewModel.payRateId.value = "id"

        runBlocking { payRateListViewModel.savePayRate()

            val result = payRateListViewModel.showSnackBar.value

            assertThat(result, IsEqual("Please Select a Tier"))
        }
    }

    @Test
    fun savePayRate_invalidPosition_snackBarValueSet() {
        payRateListViewModel.payRateTier.value = "tier"
        payRateListViewModel.payRatePosition.value = ""
        payRateListViewModel.payRateRate.value = "pay rate"
        payRateListViewModel.payRateId.value = "id"

        runBlocking { payRateListViewModel.savePayRate()

            val result = payRateListViewModel.showSnackBar.value

            assertThat(result, IsEqual("Please Enter Your Position"))
        }
    }

    @Test
    fun savePayRate_invalidRate_snackBarValueSet() {
        payRateListViewModel.payRateTier.value = "tier"
        payRateListViewModel.payRatePosition.value = "position"
        payRateListViewModel.payRateRate.value = ""
        payRateListViewModel.payRateId.value = "id"

        runBlocking { payRateListViewModel.savePayRate()

            val result = payRateListViewModel.showSnackBar.value

            assertThat(result, IsEqual("Please Enter Your Rate"))
        }
    }

}