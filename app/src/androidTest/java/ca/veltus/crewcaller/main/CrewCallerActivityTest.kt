package ca.veltus.crewcaller.main

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.authentication.LoginViewModel
import ca.veltus.crewcaller.main.contacts.ContactListViewModel
import ca.veltus.crewcaller.main.data.CrewCallerDataSource
import ca.veltus.crewcaller.main.data.dto.ProductionDTO
import ca.veltus.crewcaller.main.data.local.CrewCallerDatabase
import ca.veltus.crewcaller.main.data.local.CrewCallerLocalRepository
import ca.veltus.crewcaller.main.data.local.LocalDB
import ca.veltus.crewcaller.main.productions.ProductionsListViewModel
import ca.veltus.crewcaller.utils.DataBindingIdlingResource
import ca.veltus.crewcaller.utils.EspressoIdlingResource
import ca.veltus.crewcaller.utils.monitorActivity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
@LargeTest
class CrewCallerActivityTest : KoinTest {

    private lateinit var repository: CrewCallerDataSource
    private lateinit var app: Application
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun init() {
        stopKoin()//stop the original app koin
        app = ApplicationProvider.getApplicationContext()
        val myModule = module {
//            Declare a ViewModel - be later inject into Fragment with dedicated injector using by viewModel()
            viewModel {
                LoginViewModel(
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
                ContactListViewModel(
                    get(),
                    get() as CrewCallerDataSource
                )
            }
            single { CrewCallerLocalRepository(get()) as CrewCallerDataSource }
            single { LocalDB.createCrewCallerDao(app) }
        }

        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllProductions()
            repository.deleteAllContacts()

        }
    }

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun createMultipleProductionsAndContacts() {
        runBlocking {
            for (i in 1..10) {
                repository.saveProduction(
                    ProductionDTO(
                        "ProductionName$i",
                        "ProductionAddress$i",
                        "$i" + "$i" + "$i" + "$i" + "$i" + "$i" + "$i" + "$i" + "$i" + "$i",
                        "$i" + "production.email@veltus.ca",
                        "production$i"
                    )
                )
            }
        }
        val activityScenario = ActivityScenario.launch(CrewCallerActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open())
        onView(withText("Productions")).perform(ViewActions.click())
        onView(withId(R.id.addProductionFab)).perform(ViewActions.click())



        Thread.sleep(4000)




    }


}