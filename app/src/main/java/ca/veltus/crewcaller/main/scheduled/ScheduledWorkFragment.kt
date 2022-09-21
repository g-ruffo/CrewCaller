package ca.veltus.crewcaller.main.scheduled

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.base.BaseFragment
import ca.veltus.crewcaller.base.NavigationCommand
import ca.veltus.crewcaller.databinding.FragmentScheduledWorkBinding
import ca.veltus.crewcaller.utils.setup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class ScheduledWorkFragment : BaseFragment() {
    companion object{
        const val TAG = "ScheduledWorkFragLog"
    }

    lateinit var binding: FragmentScheduledWorkBinding
    override val _viewModel by sharedViewModel<ScheduledWorkViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_scheduled_work, container, false)

        binding.viewModel = _viewModel

        // Show action bar and set action bar title.
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.scheduledWork)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        _viewModel.clearValues()
        _viewModel.loadWorkDays()
    }

    // Setup recycler view with a ScheduledWorkListAdapter.
    private fun setupRecyclerView() {
        val adapter = ScheduledWorkListAdapter { workDay ->
            val action = ScheduledWorkFragmentDirections.actionScheduledWorkFragmentToDetailWorkDayFragment(workDay.id)
            action.workDayId = workDay.id
            _viewModel.navigationCommand.postValue(
                NavigationCommand.To(
                    action
                )
            )
        }

        // Setup the recycler view using the extension function.
        binding.scheduledWorkRecyclerView.setup(adapter)
    }
}