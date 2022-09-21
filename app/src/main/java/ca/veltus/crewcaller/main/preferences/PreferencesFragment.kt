package ca.veltus.crewcaller.main.preferences

import android.graphics.drawable.Animatable2.AnimationCallback
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.base.BaseFragment
import ca.veltus.crewcaller.databinding.FragmentPreferencesBinding
import ca.veltus.crewcaller.main.data.dto.relations.BackUpDatabase
import ca.veltus.crewcaller.utils.hasNetworkConnection
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.androidx.viewmodel.ext.android.viewModel


class PreferencesFragment : BaseFragment() {

    companion object {
        const val TAG = "PreferencesFragLog"
    }

    lateinit var binding: FragmentPreferencesBinding

    override val _viewModel: PreferencesViewModel by viewModel()
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: FirebaseFirestore
    private var firebaseUser: FirebaseUser? = null
    private lateinit var animatedVectorDrawable: AnimatedVectorDrawable
    private var uid: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_preferences, container, false)

        binding.viewModel = _viewModel

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser

        // Show action bar and set action bar title.
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.preferences)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this

        if (_viewModel.getFirebaseUserUid(firebaseUser)) {
            databaseReference = FirebaseFirestore.getInstance()
        }

        // Observe view model for uid and once valid check for backups.
        _viewModel.uid.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                uid = it
                checkForBackups(it)
            }
        })

        binding.restoreButton.setOnClickListener {
            // Wait for restore to complete before starting animation.
            if (_viewModel.restoreDatabaseBackup()) {
                runCompleteCheckAnimation()
            }
        }

        binding.backupButton.setOnClickListener {
            if (!uid.isNullOrEmpty()) {
                backupDatabase(uid!!)
            }
        }
    }

    // Check for previous Firebase backups.
    private fun checkForBackups(uid: String) {
        // Check for network connection, if no connection display toast.
        if (hasNetworkConnection(requireContext())) {
            databaseReference.collection("Users").document(uid)
                .collection("Backup").document("User Database").get().addOnCompleteListener {
                    // Check if document path exists. If not no backups have been created.
                    if (it.result.exists()) {
                        _viewModel.showToast.value = getString(R.string.databaseRetrievedToast)
                        val item = it.result.toObject(BackUpDatabase::class.java)
                        // Send Firebase document to view model
                        _viewModel.setBackupDatabaseItem(item)

                    } else {
                        _viewModel.showToast.value = getString(R.string.noBackupsFoundToast)
                    }
                }
        } else {
            _viewModel.showSnackBar.value = getString(R.string.unableToConnectToNetworkToast)
        }
    }

    // Backup database and log result.
    private fun backupDatabase(uid: String) {
        // Check for network connection, if no connection display toast.
        if (hasNetworkConnection(requireContext())) {
            _viewModel.showLoading.postValue(true)
            // Build HashMap of database entities and add them to Firebase document. Log the result and if successful check for backups.
            val backup = _viewModel.buildHashMap()
            databaseReference.collection("Users").document(uid)
                .collection("Backup").document("User Database").set(backup)
                .addOnCompleteListener {
                    if (!it.isSuccessful) {
                        Log.e(TAG, "Error Saving User Info")
                    } else {
                        Log.i(TAG, "Success Saving User Info")
                        checkForBackups(uid)

                    }
                    _viewModel.showLoading.postValue(false)

                }.addOnFailureListener {
                    Log.e(TAG, it.message.toString())
                }
        } else {
            _viewModel.showSnackBar.value = getString(R.string.unableToConnectToNetworkToast)
        }
    }

    // Set image view to visible and run animation. After complete reset and clear callbacks.
    private fun runCompleteCheckAnimation() {
        binding.doneCheckAnimation.isVisible = true
        animatedVectorDrawable = binding.doneCheckAnimation.drawable as AnimatedVectorDrawable
        animatedVectorDrawable.registerAnimationCallback(object : AnimationCallback() {
            override fun onAnimationEnd(drawable: Drawable?) {
                super.onAnimationEnd(drawable)
                animatedVectorDrawable.reset()
                animatedVectorDrawable.clearAnimationCallbacks()
                binding.doneCheckAnimation.isVisible = false
            }
        })
        animatedVectorDrawable.start()
    }
}