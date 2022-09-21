package ca.veltus.crewcaller.main.payrates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.base.BaseFragment
import ca.veltus.crewcaller.base.NavigationCommand
import ca.veltus.crewcaller.databinding.FragmentPayRateListBinding
import ca.veltus.crewcaller.utils.setup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PayRateListFragment : BaseFragment() {

    lateinit var binding: FragmentPayRateListBinding

    override val _viewModel by sharedViewModel<PayRateListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_pay_rate_list, container, false)

        binding.viewModel = _viewModel

        // Show action bar and set action bar title.
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.payRates)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        setupRecyclerView()

    }

    override fun onResume() {
        super.onResume()
        _viewModel.loadPayRates()
    }

    // Setup recycler view with a PayRateListAdapter.
    private fun setupRecyclerView() {
        val adapter = PayRateListAdapter { payRate ->
            val action =
                PayRateListFragmentDirections.actionPayRateListFragmentToSavePayRateFragment()
            action.payRate = payRate
            _viewModel.navigationCommand.postValue(
                NavigationCommand.To(
                    action
                )
            )
        }
        // Setup the recycler view using the extension function
        binding.payRatesRecyclerView.setup(adapter)
    }

}