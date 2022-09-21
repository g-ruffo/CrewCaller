package ca.veltus.crewcaller.main.productions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.base.BaseFragment
import ca.veltus.crewcaller.base.NavigationCommand
import ca.veltus.crewcaller.databinding.FragmentProductionsListBinding
import ca.veltus.crewcaller.utils.setup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class ProductionsListFragment : BaseFragment() {
    companion object {
        const val TAG = "ProductionsListFragLog"
    }

    override val _viewModel by sharedViewModel<ProductionsListViewModel>()

    lateinit var binding: FragmentProductionsListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_productions_list, container, false)

        binding.viewModel = _viewModel

        // Show action bar and set action bar title.
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.productions)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        setupRecyclerView()

    }

    override fun onResume() {
        super.onResume()
        _viewModel.loadProductions()
    }

    // Setup recycler view with a ProductionListAdapter.
    private fun setupRecyclerView() {
        val adapter = ProductionListAdapter { production ->
            _viewModel.navigationCommand.postValue(
                NavigationCommand.To(
                    ProductionsListFragmentDirections.actionProductionsFragmentToDetailProductionFragment(
                        production.id
                    )
                )
            )
        }
        // Setup the recycler view using the extension function
        binding.productionsRecyclerView.setup(adapter)
    }
}