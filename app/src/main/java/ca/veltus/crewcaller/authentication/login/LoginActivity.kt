package ca.veltus.crewcaller.authentication.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.Observer
import ca.veltus.crewcaller.authentication.LoginViewModel
import ca.veltus.crewcaller.databinding.ActivityLoginBinding
import ca.veltus.crewcaller.main.CrewCallerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginActivity : AppCompatActivity() {

    companion object {
        const val TAG = "LoginActivityLog"
    }

    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModel<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Set activity as fullscreen by hiding the status bar, bottom navigation bar and action bar
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        supportActionBar?.hide()

        observeAuthenticationState()

    }

    // Observe authentication state live data. Once authenticated, finish LoginActivity and navigate to the CrewCallerActivity.
    private fun observeAuthenticationState() {
        viewModel.authenticationState.observe(this, Observer { authenticationState ->
            when (authenticationState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    Log.i(TAG, "AuthenticationState.AUTHENTICATED")
                    startActivity(
                        Intent(this, CrewCallerActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    )
                    finish()
                }

                else -> {
                    return@Observer
                }
            }
        })
    }
}