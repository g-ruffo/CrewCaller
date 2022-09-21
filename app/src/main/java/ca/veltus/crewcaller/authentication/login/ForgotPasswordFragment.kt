package ca.veltus.crewcaller.authentication.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.authentication.LoginViewModel
import ca.veltus.crewcaller.base.BaseFragment
import ca.veltus.crewcaller.databinding.FragmentForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.viewModel

class ForgotPasswordFragment : BaseFragment() {
    companion object {
        const val TAG = "ForgotPasswordFragLog"
    }

    override val _viewModel by viewModel<LoginViewModel>()

    private lateinit var binding: FragmentForgotPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_forgot_password, container, false)

        binding.viewModel = _viewModel

        firebaseAuth = FirebaseAuth.getInstance()

        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this

        binding.submitButton.setOnClickListener {
            resetPassword()
        }

    }

    // Check to see if entered email is valid and if so send password reset link.
    private fun resetPassword() {
        if (_viewModel.validateEmail()) {
            firebaseAuth.sendPasswordResetEmail(_viewModel.getEmailPasswordAndName().first)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _viewModel.showToast.postValue(getString(R.string.resetEmailSentToast))
                        _viewModel.navigateBack()
                    } else {
                        _viewModel.showToast.postValue(task.exception!!.message.toString())
                    }
                }
        }
    }
}