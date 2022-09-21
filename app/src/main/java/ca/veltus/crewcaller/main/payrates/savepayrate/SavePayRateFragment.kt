package ca.veltus.crewcaller.main.payrates.savepayrate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.base.BaseFragment
import ca.veltus.crewcaller.databinding.FragmentSavePayRateBinding
import ca.veltus.crewcaller.main.payrates.PayRateListViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SavePayRateFragment : BaseFragment() {

    companion object {
        const val TAG = "SavePayRateFragLog"
    }

    override val _viewModel by sharedViewModel<PayRateListViewModel>()

    lateinit var binding: FragmentSavePayRateBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_save_pay_rate, container, false)

        binding.viewModel = _viewModel

        // Hide action bar.
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        checkForPayRateEdit()

        createTierListMenu()

    }

    override fun onDestroy() {
        super.onDestroy()
        _viewModel.clearValues()
    }

    // Check for navigation arguments and if not null, user is editing PayRate.
    private fun checkForPayRateEdit() {
        val payRate = SavePayRateFragmentArgs.fromBundle(requireArguments()).payRate

        if (_viewModel.loadSelectedPayRate(payRate)) {
            binding.createPayRateTitle.text = getString(R.string.editRate)
        }
    }

    // Create tier list spinner and setup ArrayAdapter.
    private fun createTierListMenu() {
        _viewModel.tierListArray.observe(viewLifecycleOwner, Observer { it ->
            val arrayAdapter = ArrayAdapter(requireActivity(), R.layout.drop_down_menu_item, it)
            binding.payRateTierAutoComplete.setAdapter(arrayAdapter)
        })
    }
}