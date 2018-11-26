package com.sergiocruz.Matematica.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.sergiocruz.Matematica.R
import com.sergiocruz.Matematica.fragment.*
import com.sergiocruz.Matematica.helper.Ads

class MainActivity : AppCompatActivity() {
    private var mContent: Fragment? = null
    private var navigationView: NavigationView? = null
    private var drawer: DrawerLayout? = null
    private var toolbar: Toolbar? = null
    // toolbar titles respected to selected nav menu item
    private var activityTitles: Array<String>? = null
    // flag to load home fragment when user presses back key
    private val shouldLoadHomeFragOnBackPress = true
    private var adView: AdView? = null

    private val fragment: Fragment
        get() {
            return when (navItemIndex) {
                0 -> HomeFragment()
                1 -> MMCFragment()
                2 -> MDCFragment()
                3 -> FatorizarFragment()
                4 -> DivisoresFragment()
                5 -> PrimesTableFragment()
                6 -> PrimorialFragment()
                else -> HomeFragment()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        adView = findViewById(R.id.adView)

        drawer = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        // Icones coloridos no menu de gaveta lateral
        navigationView!!.itemIconTintList = null

        // load toolbar titles from string resources
        activityTitles = resources.getStringArray(R.array.nav_item_activity_titles)

        // initializing navigation menu
        setUpNavigationView()

        if (savedInstanceState == null) {
            navItemIndex = 0
            CURRENT_TAG = TAG_HOME
            loadFragment()
            navigationView!!.menu.getItem(navItemIndex).setActionView(R.layout.menu_dot)
        } else {
            //Restore the fragment's instance
            mContent = supportFragmentManager.getFragment(savedInstanceState, "mContent")

        }

    }

    override fun onResume() {
        super.onResume()
        MobileAds.initialize(this, getString(R.string.ads_application_id))
        Ads.showIn(this, adView)
    }


    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private fun loadFragment() {
        // selecting appropriate nav menu item
        selectNavMenu()

        // set toolbar title
        setToolbarTitle()

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (supportFragmentManager.findFragmentByTag(CURRENT_TAG) != null) {
            drawer!!.closeDrawers()
            return
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app

        // update the main content by replacing fragments
        val fragment = fragment
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG)
        fragmentTransaction.commitAllowingStateLoss()

        //Closing drawer on item click
        drawer!!.closeDrawers()

        // refresh toolbar menu
        invalidateOptionsMenu()
    }

    private fun setToolbarTitle() {
        supportActionBar!!.title = activityTitles!![navItemIndex]
    }

    private fun selectNavMenu() {
        navigationView!!.menu.getItem(navItemIndex).isChecked = true
        navigationView!!.menu.getItem(navItemIndex).setActionView(R.layout.menu_dot)
    }

    private fun setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView!!.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { menuItem ->
            // This method will trigger on item Click of navigation menu
            //remove dot in menu
            navigationView!!.menu.getItem(navItemIndex).actionView = null

            //Check to see which item was being clicked and perform appropriate action
            when (menuItem.itemId) {
                //Replacing the main content with ContentFragment Which is our Inbox View;
                R.id.home -> {
                    navItemIndex = 0
                    CURRENT_TAG = TAG_HOME
                }
                R.id.nav_mmc -> {
                    navItemIndex = 1
                    CURRENT_TAG = TAG_MMC
                }
                R.id.nav_mdc -> {
                    navItemIndex = 2
                    CURRENT_TAG = TAG_MDC
                }
                R.id.nav_fatorizar -> {
                    navItemIndex = 3
                    CURRENT_TAG = TAG_FATORIZAR
                }
                R.id.nav_divisores -> {
                    navItemIndex = 4
                    CURRENT_TAG = TAG_DIVISORES
                }
                R.id.nav_prime_table -> {
                    navItemIndex = 5
                    CURRENT_TAG = TAG_PRIMES_TABLE
                }
                R.id.nav_primorial -> {
                    navItemIndex = 6
                    CURRENT_TAG = TAG_PRIMORIAL
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                    drawer!!.closeDrawers()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_about -> {
                    // launch new intent instead of loading fragment
                    startActivity(Intent(this@MainActivity, AboutActivity::class.java))
                    drawer!!.closeDrawers()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_send -> {
                    // launch new intent instead of loading fragment
                    startActivity(Intent(this@MainActivity, SendMailActivity::class.java))
                    drawer!!.closeDrawers()
                    return@OnNavigationItemSelectedListener true
                }
                else -> {
                    navItemIndex = 0
                    CURRENT_TAG = TAG_HOME
                }
            }

            //Checking if the item is in checked state or not, if not make it in checked state
            menuItem.isChecked = !menuItem.isChecked

            loadFragment()

            true
        })


        val actionBarDrawerToggle = object : ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

        }

        //Setting the actionbarToggle to drawer layout
        drawer!!.setDrawerListener(actionBarDrawerToggle)

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState()
    }

    override fun onBackPressed() {
        if (drawer!!.isDrawerOpen(GravityCompat.START)) {
            drawer!!.closeDrawers()
            return
        }

        //remove dot from selected item menu
        navigationView!!.menu.getItem(navItemIndex).actionView = null

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0
                CURRENT_TAG = TAG_HOME
                loadFragment()
                return
            }
        }

        super.onBackPressed()
    }

    fun mmc(view: View) {
        //remove dot in menu
        navigationView!!.menu.getItem(navItemIndex).actionView = null
        navItemIndex = 1
        CURRENT_TAG = TAG_MMC
        loadFragment()
    }

    fun mdc(view: View) {
        //remove dot in menu
        navigationView!!.menu.getItem(navItemIndex).actionView = null
        navItemIndex = 2
        CURRENT_TAG = TAG_MDC
        loadFragment()
    }

    fun fatorizar(view: View) {
        //remove dot in menu
        navigationView!!.menu.getItem(navItemIndex).actionView = null
        navItemIndex = 3
        CURRENT_TAG = TAG_FATORIZAR
        loadFragment()
    }

    fun divisores(view: View) {
        //remove dot in menu
        navigationView!!.menu.getItem(navItemIndex).actionView = null
        navItemIndex = 4
        CURRENT_TAG = TAG_DIVISORES
        loadFragment()
    }

    fun primes_table(view: View) {
        //remove dot in menu
        navigationView!!.menu.getItem(navItemIndex).actionView = null
        navItemIndex = 5
        CURRENT_TAG = TAG_PRIMES_TABLE
        loadFragment()
    }

    fun primorial(view: View) {
        //remove dot in menu
        navigationView!!.menu.getItem(navItemIndex).actionView = null
        navItemIndex = 6
        CURRENT_TAG = TAG_PRIMORIAL
        loadFragment()
    }

    companion object {

        // tags used to attach the fragments
        private val TAG_HOME = "home"
        private val TAG_MMC = "mmc"
        private val TAG_MDC = "mdc"
        private val TAG_FATORIZAR = "fatorizar"
        private val TAG_DIVISORES = "divisores"
        private val TAG_PRIMES_TABLE = "primes_table"
        private val TAG_PRIMORIAL = "primorial"
        // index to identify current nav menu item
        var navItemIndex = 0

        var CURRENT_TAG = TAG_HOME
    }


}
