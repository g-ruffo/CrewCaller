package ca.veltus.crewcaller.main.home

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ca.veltus.crewcaller.MainCoroutineRule
import ca.veltus.crewcaller.authentication.LoginViewModel
import ca.veltus.crewcaller.main.data.FakeCrewCallerDataSource
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
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

    fun invalidateShowNoData_validValues_returnFalse() {
//        homeViewModel._workId.value = "123456789"


    }
}