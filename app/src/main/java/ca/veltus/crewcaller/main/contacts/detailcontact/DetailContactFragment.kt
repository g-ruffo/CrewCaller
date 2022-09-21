package ca.veltus.crewcaller.main.contacts.detailcontact

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
import ca.veltus.crewcaller.databinding.FragmentDetailContactBinding
import ca.veltus.crewcaller.main.contacts.ContactListViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DetailContactFragment : BaseFragment() {

    companion object {
        const val TAG = "DetailContactFragLog"
    }

    override val _viewModel by sharedViewModel<ContactListViewModel>()
    lateinit var binding: FragmentDetailContactBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_detail_contact, container, false)

        binding.viewModel = _viewModel

        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        getContactAndSetupArgs()

        binding.phoneCardView.setOnClickListener {
            // Check for valid saved data and launch intent if it's not null.
            val uri = _viewModel.generatePhoneNumber()
            if (uri != null) {
                callContact(uri)
            }
        }
        binding.emailCardView.setOnClickListener {
            // Check for valid saved data and launch intent if it's not null.
            val uri = _viewModel.generateEmail()
            if (uri != null) {
                emailContact(uri)
            }
        }
        binding.facebookCardView.setOnClickListener {
            // Check for valid saved data and launch intent if it's not null.
            val uri = _viewModel.generateFacebook()
            if (uri != null) {
                facebookContact(uri)
            }
        }
        binding.instagramCardView.setOnClickListener {
            // Check for valid saved data and launch intent if it's not null.
            val uri = _viewModel.generateInstagram()
            if (uri != null) {
                instagramContact(uri)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _viewModel.clearValues()
    }

    // Launch phone intent to call the saved number.
    private fun callContact(uri: Uri) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = uri
        startActivity(dialIntent)
    }

    // Launch email intent to message the saved email address.
    private fun emailContact(uri: Uri) {
        val emailIntent = Intent(Intent.ACTION_VIEW)
        emailIntent.data = uri
        startActivity(emailIntent)
    }

    // Launch Facebook intent to the saved username.
    private fun facebookContact(uri: Uri) {
        val facebookIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(facebookIntent)
    }

    // Launch Instagram intent to the saved username.
    private fun instagramContact(uri: Uri) {
        val instagramIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(instagramIntent)
    }

    // Retrieve contactId from navigation args and pass it to view model.
    private fun getContactAndSetupArgs() {
        val contactId = DetailContactFragmentArgs.fromBundle(requireArguments()).contactId
        _viewModel.loadSelectedContactId(DetailContactFragmentArgs.fromBundle(requireArguments()).contactId!!)

        val action =
            DetailContactFragmentDirections.actionDetailContactFragmentToSaveContactFragment(
                _viewModel.contactId.value
            )

        action.contactId = contactId

        // Setup onClickListener for edit button passing the current contactId
        binding.editButton.setOnClickListener {
            _viewModel.navigationCommand.postValue(
                NavigationCommand.To(action)
            )
        }

    }
}