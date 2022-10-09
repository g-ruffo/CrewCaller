package ca.veltus.crewcaller

import android.app.Application
import ca.veltus.crewcaller.authentication.LoginViewModel
import ca.veltus.crewcaller.main.calendar.CalendarViewModel
import ca.veltus.crewcaller.main.contacts.ContactListViewModel
import ca.veltus.crewcaller.main.data.CrewCallerDataSource
import ca.veltus.crewcaller.main.data.local.CrewCallerLocalRepository
import ca.veltus.crewcaller.main.data.local.LocalDB
import ca.veltus.crewcaller.main.home.HomeViewModel
import ca.veltus.crewcaller.main.payrates.PayRateListViewModel
import ca.veltus.crewcaller.main.preferences.PreferencesViewModel
import ca.veltus.crewcaller.main.productions.ProductionsListViewModel
import ca.veltus.crewcaller.main.scheduled.ScheduledWorkViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


class CrewCallerApp : Application() {

    override fun onCreate() {
        super.onCreate()

        /**
         * use Koin Library as a service locator
         */
        val myModule = module {
//            Declare a ViewModel - be later inject into Fragment with dedicated injector using by viewModel()

            viewModel {
                LoginViewModel(
                    get(),
                    get() as CrewCallerDataSource

                )
            }

            viewModel {
                HomeViewModel(
                    get(),
                    get() as CrewCallerDataSource
                )
            }

            viewModel {
                ScheduledWorkViewModel(
                    get(),
                    get() as CrewCallerDataSource
                )
            }

            viewModel {
                ProductionsListViewModel(
                    get(),
                    get() as CrewCallerDataSource
                )
            }

            viewModel {
                PayRateListViewModel(
                    get(),
                    get() as CrewCallerDataSource
                )
            }

            viewModel {
                ContactListViewModel(
                    get(),
                    get() as CrewCallerDataSource
                )
            }

            viewModel {
                CalendarViewModel(
                    get(),
                    get() as CrewCallerDataSource
                )
            }

            viewModel {
                PreferencesViewModel(
                    get(),
                    get() as CrewCallerDataSource
                )
            }

            single { CrewCallerLocalRepository(get()) as CrewCallerDataSource }
            single { LocalDB.createCrewCallerDao(this@CrewCallerApp) }
        }

        startKoin {
            androidContext(this@CrewCallerApp)
            modules(listOf(myModule))
        }
    }
}