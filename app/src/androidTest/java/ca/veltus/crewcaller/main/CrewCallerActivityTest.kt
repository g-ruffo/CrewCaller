package ca.veltus.crewcaller.main

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import ca.veltus.crewcaller.R
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
import ca.veltus.crewcaller.utils.DataBindingIdlingResource
import ca.veltus.crewcaller.utils.EspressoIdlingResource
import ca.veltus.crewcaller.utils.monitorActivity
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.*
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import org.junit.After
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
    private lateinit var activityScenario: ActivityScenario<CrewCallerActivity>
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun init() {
        stopKoin()//stop the original app koin
        app = ApplicationProvider.getApplicationContext()
        val myModule = module {
//            Declare a ViewModel - be later inject into Fragment with dedicated injector using by viewModel()
            viewModel {
                LoginViewModel(
                    app,
                    get() as CrewCallerDataSource

                )
            }

            viewModel {
                HomeViewModel(
                    app,
                    get() as CrewCallerDataSource
                )
            }

            viewModel {
                ScheduledWorkViewModel(
                    app,
                    get() as CrewCallerDataSource
                )
            }

            viewModel {
                ProductionsListViewModel(
                    app,
                    get() as CrewCallerDataSource
                )
            }

            viewModel {
                PayRateListViewModel(
                    app,
                    get() as CrewCallerDataSource
                )
            }

            viewModel {
                ContactListViewModel(
                    app,
                    get() as CrewCallerDataSource
                )
            }

            viewModel {
                CalendarViewModel(
                    app,
                    get() as CrewCallerDataSource
                )
            }

            viewModel {
                PreferencesViewModel(
                    app,
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

    @Before
    fun setupActivityScenario() {
        activityScenario = ActivityScenario.launch(CrewCallerActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
    }

    @After
    fun closeActivityScenario() {
        activityScenario.close()
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
        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open())
        onView(withText("Productions")).perform(click())
        onView(withId(R.id.addProductionFab)).perform(click())
        onView(withId(R.id.productionNameEditText)).perform(click()).perform(
            typeText("Superman"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.productionAddressEditText)).perform(click()).perform(
            typeText("999 Canada Pl, Vancouver, BC V6C 3T4"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.productionPhoneEditText)).perform(click()).perform(
            typeText("6049999999"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.productionEmailEditText)).perform(click()).perform(
            typeText("superman@gmail.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.addProductionFab)).perform(click())
        onView(withText("Superman")).perform(click())
        onView(withId(R.id.editButton)).perform(click())
        onView(withId(R.id.enableEditingSwitch)).perform(click())
        onView(withId(R.id.productionNameEditText)).perform(click()).perform(
            clearText(), typeText("Superman and Lois"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.addProductionFab)).perform(click())
        Espresso.pressBack()
        onView(withId(R.id.addProductionFab)).perform(click())
        onView(withId(R.id.productionNameEditText)).perform(click()).perform(
            typeText("Superman"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.productionAddressEditText)).perform(click()).perform(
            typeText("999 Canada Pl, Vancouver, BC V6C 3T4"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.productionPhoneEditText)).perform(click()).perform(
            typeText("6049999999"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.productionEmailEditText)).perform(click()).perform(
            typeText("superman@gmail.com"),
            closeSoftKeyboard()
        )

        onView(withId(R.id.addProductionFab)).perform(click())
        onView(withId(R.id.addProductionFab)).perform(click())
        onView(withId(R.id.productionNameEditText)).perform(click()).perform(
            typeText("Superman"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.productionAddressEditText)).perform(click()).perform(
            typeText("999 Canada Pl, Vancouver, BC V6C 3T4"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.productionPhoneEditText)).perform(click()).perform(
            typeText("6049999999"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.productionEmailEditText)).perform(click()).perform(
            typeText("superman@gmail.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.addProductionFab)).perform(click())
        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open())
        onView(withText("Contacts")).perform(click())

        for (i in 1..5) {
            onView(withId(R.id.addContactFab)).perform(click())
            onView(withId(R.id.contactFirstNameEditText)).perform(click()).perform(
                typeText("John $i"),
                closeSoftKeyboard()
            )
            onView(withId(R.id.contactLastnameEditText)).perform(click()).perform(
                typeText("Doe $i"),
                closeSoftKeyboard()
            )
            onView(withId(R.id.contactPhoneEditText)).perform(click()).perform(
                typeText("604$i$i$i$i$i$i$i"),
                closeSoftKeyboard()
            )

            onView(withId(R.id.contactPositionEditText)).perform(click()).perform(
                typeText("Grip"),
                closeSoftKeyboard()
            )

            onView(withId(R.id.contactProductionTextInputLayout)).perform(click())
            onData(equalTo("Superman")).inRoot(RootMatchers.isPlatformPopup()).perform(click())
            onView(withId(R.id.contactFacebookEditText)).perform(click()).perform(
                typeText("John.Doe$i"),
                closeSoftKeyboard()
            )
            onView(withId(R.id.contactInstagramEditText)).perform(click()).perform(
                typeText("@JohnDoe$i"),
                closeSoftKeyboard()
            )
            onView(withId(R.id.addContactFab)).perform(click())
        }
        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open())
        onView(withText("Productions")).perform(click())
        onView(withText("Superman and Lois")).perform(click())
        onView(withId(R.id.backButton)).perform(click())
        onView(withText("Superman")).perform(click())
        onView(withId(R.id.contactCardView)).perform(click())
    }

}