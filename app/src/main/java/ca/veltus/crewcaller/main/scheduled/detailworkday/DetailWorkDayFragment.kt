package ca.veltus.crewcaller.main.scheduled.detailworkday

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.base.BaseFragment
import ca.veltus.crewcaller.base.NavigationCommand
import ca.veltus.crewcaller.databinding.FragmentDetailWorkDayBinding
import ca.veltus.crewcaller.main.scheduled.ScheduledWorkViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DetailWorkDayFragment : BaseFragment() {
    companion object{
        const val TAG = "DetailWorkDayFragLog"
    }

    override val _viewModel by sharedViewModel<ScheduledWorkViewModel>()
    lateinit var binding: FragmentDetailWorkDayBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_detail_work_day, container, false)

        binding.viewModel = _viewModel

        // Hide the action bar.
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        getWorkDayAndSetupArgs()

        binding.addressCardView.setOnClickListener {
            // Check for valid saved data and launch intent if it's not null.
            val uri = _viewModel.generateAddress()
            if (uri != null) {
                mapsProduction(uri)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _viewModel.clearValues()
    }

    // Retrieve workDayId from navigation args and pass it to view model.
    private fun getWorkDayAndSetupArgs() {
        val workDayId = DetailWorkDayFragmentArgs.fromBundle(requireArguments()).workDayId
        _viewModel.loadSelectedWorkDayId(workDayId!!)

        val action =
            DetailWorkDayFragmentDirections.actionDetailWorkDayFragmentToSaveWorkDayFragment(_viewModel.workId.value)

        action.workDayId = workDayId

        binding.editButton.setOnClickListener {
            _viewModel.navigationCommand.postValue(NavigationCommand.To(action)
            )
        }
    }

    // Launch Maps intent to the saved address.
    private fun mapsProduction(uri: Uri) {
        val mapsIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(mapsIntent)
    }
}