package ca.veltus.crewcaller.authentication.login

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.authentication.LoginViewModel
import ca.veltus.crewcaller.base.BaseFragment
import ca.veltus.crewcaller.databinding.FragmentSignUpBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.androidx.viewmodel.ext.android.viewModel


class SignUpFragment : BaseFragment() {
    companion object {
        const val TAG = "SignUpFragmentLog"
    }

    override val _viewModel by viewModel<LoginViewModel>()

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: FirebaseFirestore
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)

        binding.viewModel = _viewModel

        firebaseAuth = FirebaseAuth.getInstance()

        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this

        binding.signUpButton.setOnClickListener {
            launchEmailSignUp()
        }
    }

    // If entered email and password is valid, create Firebase user and log result.
    private fun launchEmailSignUp() {
        if (_viewModel.validateEmailAndPassword()) {
            firebaseAuth.createUserWithEmailAndPassword(
                _viewModel.getEmailPasswordAndName().first,
                _viewModel.getEmailPasswordAndName().second!!
            ).addOnCompleteListener(
                OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {

                        firebaseUser = firebaseAuth.currentUser!!
                        databaseReference = FirebaseFirestore.getInstance()

                        // Set Firebase user display name to the entered name
                        firebaseUser.updateProfile(
                            UserProfileChangeRequest.Builder()
                                .setDisplayName(_viewModel.getEmailPasswordAndName().third)
                                .build()
                        )

                        // Create HashMap of users account details.
                        val userHashMap = HashMap<String, Any>()
                        userHashMap["uid"] = firebaseUser.uid
                        userHashMap["username"] = _viewModel.getEmailPasswordAndName().third!!
                        userHashMap["email"] = _viewModel.getEmailPasswordAndName().first
                        userHashMap["password"] = _viewModel.getEmailPasswordAndName().second!!

                        // Create Firebase document for user using the Firebase user uid as the identifier and upload HashMap.
                        databaseReference.collection("Users").document(firebaseUser.uid)
                            .set(userHashMap)
                            .addOnCompleteListener {
                                if (!it.isSuccessful) {
                                    Log.e(TAG, "Error Saving User Info")
                                } else {
                                    Log.i(TAG, "Success Saving User Info")
                                }
                            }
                    } else {
                        // If Firebase cannot create account with provided details show helper text with task exception message.
                        binding.emailEditTextLayout.helperText = task.exception!!.message.toString()
                        binding.emailEditTextLayout.setHelperTextColor(
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.warningRed
                                )
                            )
                        )
                    }
                }
            )
        }
    }

}