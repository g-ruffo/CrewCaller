package ca.veltus.crewcaller.authentication

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ca.veltus.crewcaller.MainCoroutineRule
import ca.veltus.crewcaller.main.data.FakeCrewCallerDataSource
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
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
class LoginViewModelTest {
    private lateinit var loginViewModel: LoginViewModel
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
            FirebaseApp.initializeApp(app)
            loginViewModel = LoginViewModel(app, dataSource)
    }



    @Test
    fun validateEmail_validEmail_returnTrue() {
        loginViewModel.emailAddress.value = "test.email@gmail.com"

        val result = loginViewModel.validateEmail()

        assertThat(result, IsEqual(true))
    }

    @Test
    fun validateEmail_invalidEmail_returnFalse() {
        loginViewModel.emailAddress.value = "test.emailgmail.com"

        val result = loginViewModel.validateEmail()
        val toastResult = loginViewModel.showToast.value


        assertThat(result, IsEqual(false))
        assertThat(toastResult, IsEqual("Email Address is Not Valid"))

    }

    @Test
    fun validateEmail_blankEmail_returnFalse() {
        loginViewModel.emailAddress.value = "  "

        val result = loginViewModel.validateEmail()
        val toastResult = loginViewModel.showToast.value

        assertThat(result, IsEqual(false))
        assertThat(toastResult, IsEqual("Please Enter Your Email Address"))

    }

    @Test
    fun validateEmailAndPassword_blankEmail_returnFalse() {
        loginViewModel.emailAddress.value = "  "
        loginViewModel.password.value = "123456789"
        loginViewModel.username.value = "John Doe"

        val result = loginViewModel.validateEmailAndPassword()
        val toastResult = loginViewModel.showToast.value

        assertThat(result, IsEqual(false))
        assertThat(toastResult, IsEqual("Please Enter Your Email Address"))

    }

    @Test
    fun validateEmailAndPassword_invalidEmail_returnFalse() {
        loginViewModel.emailAddress.value = "john.doegmail.com"
        loginViewModel.password.value = "123456789"
        loginViewModel.username.value = "John Doe"

        val result = loginViewModel.validateEmailAndPassword()
        val toastResult = loginViewModel.showToast.value

        assertThat(result, IsEqual(false))
        assertThat(toastResult, IsEqual("Email Address is Not Valid"))

    }

    @Test
    fun validateEmailAndPassword_emptyPassword_returnFalse() {
        loginViewModel.emailAddress.value = "john.doe@gmail.com"
        loginViewModel.password.value = "   "
        loginViewModel.username.value = "John Doe"

        val result = loginViewModel.validateEmailAndPassword()
        val toastResult = loginViewModel.showToast.value

        assertThat(result, IsEqual(false))
        assertThat(toastResult, IsEqual("Please Enter a Password"))

    }

    @Test
    fun getEmailPasswordAndName_returnTriple() {
        loginViewModel.emailAddress.value = "john.doe@gmail.com"
        loginViewModel.password.value = "password"
        loginViewModel.username.value = "John Doe"

        val result = loginViewModel.getEmailPasswordAndName()

        assertThat(result, IsEqual(Triple("john.doe@gmail.com", "password", "John Doe")))
    }

}