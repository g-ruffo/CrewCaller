package ca.veltus.crewcaller.main.productions.detailproduction

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
import ca.veltus.crewcaller.databinding.FragmentDetailProductionBinding
import ca.veltus.crewcaller.main.productions.ProductionsListViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class DetailProductionFragment : BaseFragment() {
    companion object {
        const val TAG = "ProductionDetailFragLog"
    }

    override val _viewModel by sharedViewModel<ProductionsListViewModel>()
    lateinit var binding: FragmentDetailProductionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_detail_production, container, false)

        binding.viewModel = _viewModel

        // Hide action bar.
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        getProductionAndSetupArgs()

        binding.phoneCardView.setOnClickListener {
            // Check for valid saved data and launch intent if it's not null.
            val uri = _viewModel.generatePhoneNumber()
            if (uri != null) {
                callProduction(uri)
            }
        }
        binding.emailCardView.setOnClickListener {
            // Check for valid saved data and launch intent if it's not null.
            val uri = _viewModel.generateEmail()
            if (uri != null) {
                emailProduction(uri)
            }
        }
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

    // Launch phone intent to the saved phone number.
    private fun callProduction(uri: Uri) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = uri
        startActivity(dialIntent)
    }

    // Launch Email intent to the saved email address.
    private fun emailProduction(uri: Uri) {
        val emailIntent = Intent(Intent.ACTION_VIEW)
        emailIntent.data = uri
        startActivity(emailIntent)
    }

    // Launch Maps intent to the saved address.
    private fun mapsProduction(uri: Uri) {
        val mapsIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(mapsIntent)
    }

    // Retrieve productionId from navigation args and pass it to view model.
    private fun getProductionAndSetupArgs() {
        val productionId = DetailProductionFragmentArgs.fromBundle(requireArguments()).productionId
        _viewModel.loadProductionsWithWorkDaysAndContacts(productionId!!)

        val action =
            DetailProductionFragmentDirections.actionDetailProductionFragmentToSaveProductionFragment(
                _viewModel.productionId.value
            )

        action.productionId = productionId

        binding.editButton.setOnClickListener {
            _viewModel.navigationCommand.postValue(
                NavigationCommand.To(action)
            )
        }
    }
}