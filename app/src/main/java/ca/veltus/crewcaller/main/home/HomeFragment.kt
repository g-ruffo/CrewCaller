package ca.veltus.crewcaller.main.home

import android.annotation.SuppressLint
import android.app.Activity.RESULT_CANCELED
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import ca.veltus.crewcaller.BuildConfig
import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.base.BaseFragment
import ca.veltus.crewcaller.databinding.FragmentHomeBinding
import ca.veltus.crewcaller.utils.isLocationEnabled
import ca.veltus.crewcaller.utils.isLocationPermissionsEnabled
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject
import java.time.LocalDateTime
import java.util.*


class HomeFragment : BaseFragment() {

    companion object {
        const val TAG = "HomeFragmentLog"
    }

    lateinit var binding: FragmentHomeBinding
    override val _viewModel: HomeViewModel by inject()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var snackbar: Snackbar? = null
    private val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions.getOrDefault(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    false
                ) -> {
                    // Precise location access granted.
                    Log.i(TAG, "ACCESS_FINE_LOCATION GRANTED")
                    getCurrentLocation()
                }
                permissions.getOrDefault(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    false
                ) -> {
                    // Only approximate location access granted.
                    Log.i(TAG, "ACCESS_COARSE_LOCATION GRANTED")
                    getCurrentLocation()
                }
                else -> {
                    val showRationaleFineLocation =
                        shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    val showRationaleCourseLocation =
                        shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    Log.e(TAG, "Location Permissions Denied = $permissions")
                    snackbar = Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        R.string.locationRequiredError,
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction(android.R.string.ok) {
                        requestPermission(showRationaleCourseLocation && showRationaleFineLocation)
                    }
                    snackbar?.show()
                }
            }
        }

    private val locationSettingsRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_CANCELED) {
                requestPermission()
            } else {
                getCurrentLocation()
            }
        }

    private val enableDeviceLocationRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            getCurrentLocation()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.viewModel = _viewModel

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.upcomingWork)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        // Observe loaded WorkDay and if not null get weather data.
        _viewModel.workId.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                getCurrentLocation()
            }
        })

        binding.wrapTimeCardView.setOnClickListener {
            launchEndTimeDialog()
        }

        binding.addressCardView.setOnClickListener {
            // Check for valid saved data and launch intent if it's not null.
            val uri = _viewModel.generateAddress()
            if (uri != null) {
                mapsProduction(uri)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        _viewModel.loadNextWorkDay()

        // Observe weather and if not null load corresponding image.
        _viewModel.weather.observe(this, Observer {
            if (it != null) {
                Glide.with(this)
                    .load("https://openweathermap.org/img/w/" + it.icon + ".png")
                    .into(binding.weatherImageView)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _viewModel.clearValues()
        _viewModel.cancelWorkTimer()
    }

    override fun onStop() {
        super.onStop()
        _viewModel.cancelWorkTimer()
        snackbar?.dismiss()
    }

    // Launch map intent to the saved address.
    private fun mapsProduction(uri: Uri) {
        val mapsIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(mapsIntent)
    }

    // Check for location permissions and device location. If granted and on fetch weather data.
    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if (isLocationPermissionsEnabled(requireContext())) {
            if (isLocationEnabled(requireActivity())) {
                fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                    if (it != null) {
                        _viewModel.getWeather(
                            it.latitude.toString(),
                            it.longitude.toString()
                        )
                    } else {
                        getCurrentLocation()
                    }
                }

            } else {
                requestTurnOnDeviceLocation()
            }
        } else {
            requestPermission()
        }
    }

    private fun requestTurnOnDeviceLocation() {
        snackbar = Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            R.string.enableYourLocation,
            Snackbar.LENGTH_INDEFINITE
        ).setAction(android.R.string.ok) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            enableDeviceLocationRequest.launch(intent)
        }
        snackbar?.show()
    }

    // Check if permission request has been denied multiple times and if so launch settings intent instead.
    private fun requestPermission(resolve: Boolean = true) {
        if (resolve) {
            locationPermissionRequest.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
            )
            locationSettingsRequest.launch(intent)
        }
    }

    // Launch end time dialog and save result to current WorkDay.
    private fun launchEndTimeDialog() {
        var hour = LocalDateTime.now().hour
        var minute = LocalDateTime.now().minute
        val onTimeSetListener =
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minuteOfDay ->
                hour = hourOfDay
                minute = minuteOfDay
                _viewModel.setEndTimeAndUpdateWorkDay(
                    String.format(
                        Locale.getDefault(),
                        "%02d:%02d:00",
                        hour,
                        minute
                    )
                )
                _viewModel.cancelWorkTimer()
            }

        val style = AlertDialog.THEME_HOLO_DARK
        val timePickerDialog =
            TimePickerDialog(context, style, onTimeSetListener, hour, minute, true)
        timePickerDialog.show()
    }
}







