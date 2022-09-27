package ca.veltus.crewcaller.main.home

import android.app.Application
import android.net.Uri
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ca.veltus.crewcaller.MainCoroutineRule
import ca.veltus.crewcaller.main.data.FakeCrewCallerDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
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
class HomeViewModelTest {
    private lateinit var homeViewModel: HomeViewModel
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
        homeViewModel = HomeViewModel(app, dataSource)
    }


    @Test
    fun calculateTimeAndEarnings_timeWorkedNull_calculatedEarningsSet() {
        homeViewModel.workRate.value = "100.00"
        homeViewModel.workStartTime.value = "10:00"
        homeViewModel.workEndTime.value = "18:00"

        homeViewModel.calculateTimeAndEarnings(null)

        val totalPay = homeViewModel.totalPay.value
        val totalHoursWorked = homeViewModel.totalHoursWorked.value


        assertThat(totalPay, IsEqual(800.00))
        assertThat(totalHoursWorked, IsEqual("8 Hours\n0 Minutes"))

    }

    @Test
    fun calculateTimeAndEarnings_withTimeWorked_calculatedEarningsSet() {
        homeViewModel.workRate.value = "100.00"
        homeViewModel.workStartTime.value = "10:00"
        homeViewModel.workEndTime.value = "18:00"

        homeViewModel.calculateTimeAndEarnings(3600000)

        val totalPay = homeViewModel.totalPay.value
        val totalHoursWorked = homeViewModel.totalHoursWorked.value


        assertThat(totalPay, IsEqual(100.00))
        assertThat(totalHoursWorked, IsEqual("1 Hours\n0 Minutes"))

    }

    @Test
    fun generateAddress_validWorkLocation_returnUri() {
        homeViewModel.workLocation.value = "777 Pacific Blvd, Vancouver, BC V6B 4Y8"

        val result = homeViewModel.generateAddress()

        assertThat(
            result,
            IsEqual(Uri.parse("google.navigation:mode=d&q=777%20Pacific%20Blvd%2C%20Vancouver%2C%20BC%20V6B%204Y8"))
        )
    }

    @Test
    fun generateAddress_nullWorkLocation_returnUri() {
        homeViewModel.workLocation.value = null

        val result = homeViewModel.generateAddress()

        assertThat(result, IsEqual(null))
    }
}