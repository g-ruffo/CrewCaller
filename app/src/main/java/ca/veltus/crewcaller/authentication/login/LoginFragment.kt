package ca.veltus.crewcaller.authentication.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.authentication.LoginViewModel
import ca.veltus.crewcaller.base.BaseFragment
import ca.veltus.crewcaller.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore

class LoginFragment : BaseFragment() {
    companion object {
        const val TAG = "LoginFragmentLog"
        const val SIGN_UP_RESULT_CODE = 1001
    }

    override val _viewModel by viewModel<LoginViewModel>()

    private lateinit var binding: FragmentLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: FirebaseFirestore
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        binding.viewModel = _viewModel

        firebaseAuth = FirebaseAuth.getInstance()

        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this

        binding.signInButton.setOnClickListener {
            launchEmailSignIn()
        }
        binding.googleSignInButton.setOnClickListener {
            launchGoogleSignIn()
        }

    }

    // If email and password are valid pass login values to Firebase and log success or failure response.
    private fun launchEmailSignIn() {
        if (_viewModel.validateEmailAndPassword()) {
            firebaseAuth.signInWithEmailAndPassword(
                _viewModel.getEmailPasswordAndName().first,
                _viewModel.getEmailPasswordAndName().second!!
            ).addOnCompleteListener(
                OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        Log.i(TAG, "Login was Successful: $firebaseUser")
                    } else {
                        _viewModel.showSnackBar.value =
                            getString(R.string.firebaseLoginFailureToast)
                        Log.e(TAG, "Login not successful: ${task.exception}")
                    }
                }
            )
        }
    }

    // If the result code matches the Google sign in request code attempt to login and log result.
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_UP_RESULT_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {
                    // If task was successful, retrieve Google accounts id token and authorize it with Firebase.
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account.idToken!!)

                } catch (e: ApiException) {
                    Log.e(TAG, "Google Sign in FAILED!!")
                }
            } else {
                Log.w(TAG, exception.toString())
            }

        }
    }

    // Launch Firebase Google sign in intent and listen for result.
    private fun launchGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, SIGN_UP_RESULT_CODE)

    }

    // Attempt to login with Google account id token. If successful, store users info in HashMap and upload to Firebase.
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i(TAG, "Sign in with credential is Successful")
                databaseReference = FirebaseFirestore.getInstance()
                firebaseUser = firebaseAuth.currentUser!!

                // Create HashMap of users account details.
                val userHashMap = HashMap<String, Any>()
                userHashMap["uid"] = firebaseUser.uid
                userHashMap["username"] = firebaseUser.displayName!!
                userHashMap["email"] = firebaseUser.email!!
                userHashMap["password"] = "null"

                // Create Firebase document for user using the Firebase user uid as the identifier and upload HashMap.
                databaseReference.collection("Users").document(firebaseUser.uid).set(userHashMap)
                    .addOnCompleteListener {
                        if (!it.isSuccessful) {
                            Log.e(TAG, "Error Saving User Info")
                        } else {
                            Log.i(TAG, "Success Saving User Info")
                        }
                    }

            } else {
                Log.e(TAG, "Sign in with credential has failed!")
                _viewModel.showSnackBar.value = getString(R.string.authenticationFailed)

            }
        }
    }
}