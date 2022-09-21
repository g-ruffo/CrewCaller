package ca.veltus.crewcaller.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ca.veltus.crewcaller.R
import ca.veltus.crewcaller.authentication.login.LoginActivity
import ca.veltus.crewcaller.databinding.ActivityCrewCallerBinding
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class CrewCallerActivity : AppCompatActivity() {

    companion object {
        const val TAG = "CrewCallerActivityLog"
    }

    private lateinit var binding: ActivityCrewCallerBinding
    private lateinit var toggleActionBarDrawer: ActionBarDrawerToggle
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var firebaseAuth: FirebaseAuth
    private var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrewCallerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser

        setupNavigationAndToolbar()

    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
            return
        }
        super.onBackPressed()
    }

    private fun setupNavigationAndToolbar() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        setSupportActionBar(binding.toolbar)
        drawerLayout = binding.drawerLayout
        val navigationView = binding.navigationView
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.scheduledWorkFragment,
                R.id.productionsFragment,
                R.id.payRateFragment,
                R.id.contactsFragment,
                R.id.calendarFragment,
                R.id.preferencesFragment
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navigationView.setupWithNavController(navController)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        toggleActionBarDrawer =
            ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggleActionBarDrawer)
        toggleActionBarDrawer.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Get Firebase current user data and display it in header.
        navigationView.getHeaderView(0).findViewById<TextView>(R.id.usernameNavHeader).text = firebaseUser!!.displayName
        navigationView.getHeaderView(0).findViewById<TextView>(R.id.emailNavHeader).text = firebaseUser!!.email

        Glide.with(this)
            .load(firebaseUser!!.photoUrl)
            .into(navigationView.getHeaderView(0).findViewById(R.id.circleImageImageView))


        navigationView.menu.findItem(R.id.logoutMenu).setOnMenuItemClickListener {
            AuthUI.getInstance().signOut(this)
                        .addOnSuccessListener {
                            startActivity(
                                Intent(this, LoginActivity::class.java)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            )
                            finish()

                        }
            true
        }
    }
}


