package com.example.rickmorty_paging3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.rickmorty_paging3.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


/* LifeCycle-aware
    - perform an actions in response to a change in the lifecycle status of another component such
    as activities and fragments
 */
/* ViewBinding
    - an instances of binding class contains direct references to all views that have an ID in
    corresponding layout
 */

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        getSupportActionBar()?.hide();
    }

}