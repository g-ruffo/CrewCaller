package ca.veltus.crewcaller.authentication

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.authentication.login.LoginFragmentDirections
import ca.veltus.crewcaller.base.BaseViewModel
import ca.veltus.crewcaller.base.NavigationCommand
import ca.veltus.crewcaller.main.data.CrewCallerDataSource

class LoginViewModel(val app: Application, val dataSource: CrewCallerDataSource) :
    BaseViewModel(app) {

    val username = MutableLiveData<String>()
    val emailAddress = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED
    }

    // If Firebase user is not null the account has been authenticated.
    // TODO -> Remove FirebaseUserLiveData
    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED

        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }

    // Check to see if entered email is valid and matches correct format. If valid return true.
    fun validateEmail(): Boolean {
        val email = emailAddress.value?.trim()
        if (TextUtils.isEmpty(email)) {
            showToast.value = app.getString(R.string.enterEmailAddressToast)
            return false
        }
        return if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches()
        ) {
            showToast.value = app.getString(R.string.emailAddressNotValidToast)
            false
        } else true
    }

    // Check to see if entered email and password are valid and match correct format. If valid return true.
    fun validateEmailAndPassword(): Boolean {
        emailAddress.value = emailAddress.value?.trim()
        password.value = password.value?.trim()
        username.value = username.value?.trim()

        if (TextUtils.isEmpty(emailAddress.value)) {
            showToast.value = app.getString(R.string.enterEmailAddressToast)
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress.value.toString()).matches()) {
            showToast.value = app.getString(R.string.emailAddressNotValidToast)
            return false
        }
        return if (TextUtils.isEmpty(password.value)) {
            showToast.value = app.getString(R.string.pleaseEnterPasswordToast)
            false
        } else true
    }

    // Returns email, password and username as a triple.
    fun getEmailPasswordAndName(): Triple<String, String?, String?> {
        return Triple(emailAddress.value!!, password.value, username.value)
    }

    fun navigateBack() {
        navigationCommand.postValue(NavigationCommand.Back)
    }

    fun navigateToForgottenPassword() {
        navigationCommand.postValue(
            NavigationCommand.To(
                LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
            )
        )
    }

    fun navigateToSignUp() {
        navigationCommand.postValue(
            NavigationCommand.To(
                LoginFragmentDirections.actionLoginFragmentToSignUpFragment()
            )
        )
    }
}
