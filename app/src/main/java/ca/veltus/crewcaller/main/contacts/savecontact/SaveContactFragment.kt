package ca.veltus.crewcaller.main.contacts.savecontact

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
import ca.veltus.crewcaller.databinding.FragmentSaveContactBinding
import ca.veltus.crewcaller.main.contacts.ContactListViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SaveContactFragment : BaseFragment() {

    companion object {
        const val TAG = "SaveContactFragLog"
    }

    override val _viewModel by sharedViewModel<ContactListViewModel>()

    lateinit var binding: FragmentSaveContactBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_save_contact, container, false)

        binding.viewModel = _viewModel

        // Hide action bar.
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        checkForContactEdit()

        _viewModel.getProductionsList()
        createProductionListMenu()
    }

    override fun onDestroy() {
        super.onDestroy()
        _viewModel.clearValues()
    }

    // Check for navigation arguments and if not null user is editing previously saved contact.
    private fun checkForContactEdit() {
        val contactId = SaveContactFragmentArgs.fromBundle(requireArguments()).contactId
        if (!contactId.isNullOrEmpty()) {
            _viewModel.loadSelectedContactId(contactId)
            binding.createContactTitle.text = getString(R.string.editContactTitle)
        }
    }

    // Create production list menu spinner to display all saved Productions.
    private fun createProductionListMenu() {
        // Once production list is loaded, setup ArrayAdapter.
        _viewModel.productionListArray.observe(
            viewLifecycleOwner,
            Observer {
                val arrayAdapter = ArrayAdapter(requireActivity(), R.layout.drop_down_menu_item, it)
                binding.contactProductionAutoComplete.setAdapter(arrayAdapter)
            })
    }
}