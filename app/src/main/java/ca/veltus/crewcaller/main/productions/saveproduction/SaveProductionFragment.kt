package ca.veltus.crewcaller.main.productions.saveproduction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.base.BaseFragment
import ca.veltus.crewcaller.databinding.FragmentSaveProductionBinding
import ca.veltus.crewcaller.main.productions.ProductionsListViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SaveProductionFragment : BaseFragment() {

    companion object {
        const val TAG = "NewProductionFragLog"
    }

    override val _viewModel by sharedViewModel<ProductionsListViewModel>()
    lateinit var binding: FragmentSaveProductionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_save_production, container, false)

        binding.viewModel = _viewModel

        // Hide action bar.
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        checkForProductionEdit()
    }

    override fun onDestroy() {
        super.onDestroy()
        _viewModel.clearValues()
    }

    // Check for navigation arguments and if not null user is editing previously saved production.
    private fun checkForProductionEdit() {
        val productionId = SaveProductionFragmentArgs.fromBundle(requireArguments()).productionId

        if (!productionId.isNullOrEmpty()) {
            _viewModel.loadProductionsWithWorkDaysAndContacts(productionId)
            binding.createProductionTitle.text = getString(R.string.editProduction)
        }
    }
}