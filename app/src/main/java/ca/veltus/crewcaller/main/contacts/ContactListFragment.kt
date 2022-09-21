package ca.veltus.crewcaller.main.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.base.BaseFragment
import ca.veltus.crewcaller.base.NavigationCommand
import ca.veltus.crewcaller.databinding.FragmentContactListBinding
import ca.veltus.crewcaller.utils.setup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ContactListFragment : BaseFragment() {

    lateinit var binding: FragmentContactListBinding

    override val _viewModel by sharedViewModel<ContactListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_contact_list, container, false)

        binding.viewModel = _viewModel

        // Show action bar and set action bar title.
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.contact)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        checkForContactFilter()
    }

    // Check for production contact filter navigation arguments. If null load all contacts.
    private fun checkForContactFilter() {
        val production = ContactListFragmentArgs.fromBundle(requireArguments()).production

        if (_viewModel.loadSelectedContactList(production)) {
            val production = ContactListFragmentArgs.fromBundle(requireArguments()).production

            binding.productionNameTitleTextView.text = production
            (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        } else {
            _viewModel.loadContacts()
        }
    }

    // Setup recycler view with the ContactListAdapter.
    private fun setupRecyclerView() {
        val adapter = ContactListAdapter { contact ->
            val action =
                ContactListFragmentDirections.actionContactsFragmentToDetailContactFragment(contact.id)
            action.contactId = contact.id
            _viewModel.navigationCommand.postValue(
                NavigationCommand.To(
                    action
                )
            )
        }

        // Setup the recycler view using the extension function
        binding.contactsRecyclerView.setup(adapter)
    }
}