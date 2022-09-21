package ca.veltus.crewcaller.main.calendar

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.base.BaseFragment
import ca.veltus.crewcaller.databinding.FragmentCalendarBinding
import ca.veltus.crewcaller.utils.fadeIn
import ca.veltus.crewcaller.utils.setup
import org.koin.androidx.viewmodel.ext.android.viewModel

class CalendarFragment : BaseFragment() {

    companion object {
        const val TAG = "CalendarFragmentLog"
    }

    lateinit var binding: FragmentCalendarBinding
    override val _viewModel: CalendarViewModel by viewModel()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false)

        binding.viewModel = _viewModel

        // Show action bar and set action bar title.
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.calendar)

        _viewModel.loadWorkDays()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        // Observe saved WorkDays and once loaded setup calendar.
        _viewModel.workDayList.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                setupCalendar()
            }
        })

        // Set calendar to previous month.
        binding.previousCalendarButton.setOnClickListener {
            _viewModel.previousMonth()
            _viewModel.setCalendarMonth()
            setRecycleViewLayoutAnimation(R.anim.calender_layout_animation_left)
        }

        // Set calendar to previous month.
        binding.nextCalendarButton.setOnClickListener {
            _viewModel.nextMonth()
            _viewModel.setCalendarMonth()
            setRecycleViewLayoutAnimation(R.anim.calender_layout_animation_right)

        }
    }

    override fun onDestroy() {
        _viewModel.clearValues()
        super.onDestroy()
    }

    // Setup calendar with corresponding adapter.
    private fun setupCalendar() {
        val adapter = CalendarAdapter {}
        adapter.setHasStableIds(true)
        _viewModel.setCalendarMonth()
        binding.calendarRecyclerView.setup(adapter, true)
        binding.calendarRecyclerView.fadeIn()
    }

    private fun setRecycleViewLayoutAnimation(resource: Int) {
        binding.calendarRecyclerView.layoutAnimation =
            android.view.animation.AnimationUtils.loadLayoutAnimation(context, resource)
        binding.calendarRecyclerView.startLayoutAnimation()
    }
}