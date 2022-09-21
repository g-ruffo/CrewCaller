package ca.veltus.crewcaller.main.scheduled.saveworkday

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.base.BaseFragment
import ca.veltus.crewcaller.databinding.FragmentSaveWorkDayBinding
import ca.veltus.crewcaller.main.scheduled.ScheduledWorkViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.SimpleDateFormat
import java.util.*


class SaveWorkDayFragment : BaseFragment() {

    companion object {
        const val TAG = "SaveWorkDayFragLog"
    }

    override val _viewModel by sharedViewModel<ScheduledWorkViewModel>()

    lateinit var binding: FragmentSaveWorkDayBinding

    private var hour = 0
    private var minute = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_save_work_day, container, false)

        binding.viewModel = _viewModel

        // Hide the action bar.
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        checkForWorkDayEdit()

        createProductionListMenu()
        createPayRateListMenu()

        _viewModel.getProductionsList()
        _viewModel.getPayRateList()

        binding.selectDateAutoComplete.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                v.performClick()
            }
        }

        binding.selectDateAutoComplete.setOnClickListener {
            launchDatePickerDialog()
        }

        binding.selectStartTimeAutoComplete.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                v.performClick()
            }
        }

        binding.selectStartTimeAutoComplete.setOnClickListener {
            launchStartTimeDialog()
        }

        binding.selectEndTimeAutoComplete.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                v.performClick()
            }
        }

        binding.selectEndTimeAutoComplete.setOnClickListener {
            launchEndTimeDialog()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _viewModel.clearValues()
    }

    // Check for navigation arguments and if not null user is editing previously saved WorkDay.
    private fun checkForWorkDayEdit() {
        val workDayId = SaveWorkDayFragmentArgs.fromBundle(requireArguments()).workDayId
        if (!workDayId.isNullOrEmpty()) {
            _viewModel.loadSelectedWorkDayId(workDayId)
            binding.createWorkDayTitle.text = getString(R.string.editWorkDay)
        }

    }

    // Launch start time dialog and listen for its result.
    private fun launchStartTimeDialog() {
        val onTimeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minuteOfDay ->
            hour = hourOfDay
            minute = minuteOfDay
            _viewModel.setStartTime(
                String.format(
                    Locale.getDefault(),
                    "%02d:%02d:00",
                    hour,
                    minute
                )
            )
        }

        val style = AlertDialog.THEME_HOLO_DARK
        val timePickerDialog =
            TimePickerDialog(context, style, onTimeSetListener, hour, minute, true)
        timePickerDialog.show()
    }

    // Launch end time dialog and listen for its result.
    private fun launchEndTimeDialog() {
        val onTimeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minuteOfDay ->
            hour = hourOfDay
            minute = minuteOfDay
            _viewModel.setEndTime(String.format(Locale.getDefault(), "%02d:%02d:00", hour, minute))
        }

        val style = AlertDialog.THEME_HOLO_DARK
        val timePickerDialog =
            TimePickerDialog(context, style, onTimeSetListener, hour, minute, true)
        timePickerDialog.show()
    }

    // Launch date dialog and listen for its result.
    private fun launchDatePickerDialog() {
        val calendar = Calendar.getInstance()
        var selectedYear = calendar.get(Calendar.YEAR)
        var selectedMonth = calendar.get(Calendar.MONTH)
        var selectedDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerListener =
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                _viewModel.setWorkDate(
                    SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(
                        calendar.time
                    )
                )
            }

        val dialog = DatePickerDialog(
            requireContext(),
            datePickerListener,
            selectedYear,
            selectedMonth,
            selectedDay
        )

        dialog.show()
    }

    // Create Production list spinner.
    private fun createProductionListMenu() {
        _viewModel.productionListArray.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val arrayAdapter = ArrayAdapter(requireActivity(), R.layout.drop_down_menu_item, it)
            binding.workProductionAutoComplete.setAdapter(arrayAdapter)
        })
    }

    // Create PayRate list spinner.
    private fun createPayRateListMenu() {
        _viewModel.payRateListArray.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val arrayAdapter =
                AutoCompletePayRateAdapter(requireActivity(), R.layout.drop_down_menu_item_two, it)
            binding.workPayRateAutoComplete.setAdapter(arrayAdapter)

            binding.workPayRateAutoComplete.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    _viewModel.workPayRateId.value = it[position].id
                }
        })

    }
}