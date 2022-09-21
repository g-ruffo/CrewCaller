package ca.veltus.crewcaller.authentication.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.Observer
import ca.veltus.crewcaller.authentication.LoginViewModel
import ca.veltus.crewcaller.authentication.login.LoginActivity
import ca.veltus.crewcaller.databinding.ActivitySplashScreenBinding
import ca.veltus.crewcaller.main.CrewCallerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashScreenActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SplashScreenActivity"
    }

    private lateinit var binding: ActivitySplashScreenBinding
    private val viewModel by viewModel<LoginViewModel>()
    private lateinit var motionLayout: MotionLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set activity as fullscreen by hiding the status bar, bottom navigation bar and action bar
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        supportActionBar?.hide()

        // Start Splash Screen animation
        this.window.setWindowAnimations(0)
        motionLayout = binding.mainLayout
        motionLayout.startLayoutAnimation()

        // Wait for animation to finish before checking the authentication state.
        motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                observeAuthenticationState()
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
            }

        })

    }

    // Observe authentication state. If authenticated navigate to CrewCallerActivity and if not launch the LoginActivity
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
                    Log.i(TAG, "AuthenticationState.UNAUTHENTICATED")
                    startActivity(
                        Intent(this, LoginActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    )
                    finish()
                }
            }
        })
    }
}