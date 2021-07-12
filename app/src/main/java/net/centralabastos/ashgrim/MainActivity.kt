package net.centralabastos.ashgrim


import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.work.*


import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import net.centralabastos.ashgrim.databinding.ActivityMainBinding
import net.centralabastos.ashgrim.worker.PreciosWorker
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener {
    private val preciosViewModel: PreciosModelo by viewModels {
        PreciosModeloFactory(application as PreciosApplication)
    }

    private lateinit var navController : NavController

    override fun getDefaultViewModelProviderFactory(): PreciosModeloFactory {
        return PreciosModeloFactory(application as PreciosApplication)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding:ActivityMainBinding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpWorker()

        val toolbar: MaterialToolbar = findViewById(R.id.my_toolbar)
        toolbar.inflateMenu(R.menu.menu)

        toolbar.setOnMenuItemClickListener(this)


        val navHostFragment=supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController
        val appBarConfig = AppBarConfiguration(navController.graph)

        NavigationUI.setupWithNavController(toolbar, navController, appBarConfig)

        binding.btnReload.setOnClickListener {
            preciosViewModel.loadPrecios()
        }

         preciosViewModel.status.observe(this, {
            when (it) {
                Utils.PreciosApiStatus.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                Utils.PreciosApiStatus.ERROR -> {
                    Snackbar.make(binding.root,getString(R.string.string_error_snackbar), Snackbar.LENGTH_LONG).show()
                    binding.laySinInfo.visibility = View.GONE
                    binding.progressBar.visibility=View.GONE
                    binding.fragmentContainer.visibility = View.GONE

                }
                Utils.PreciosApiStatus.EMPTY-> {
                    binding.laySinInfo.visibility = View.VISIBLE
                    binding.progressBar.visibility=View.GONE
                    binding.fragmentContainer.visibility = View.GONE
                    toolbar.menu.clear()

                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    binding.laySinInfo.visibility = View.GONE
                    binding.fragmentContainer.visibility = View.VISIBLE
                }
            }
        })



    }

    private fun calculateFlex(hourOfTheDay: Int, periodInDays: Int): Long {
        // Initialize the calendar with today and the preferred time to run the job.
        val cal1: Calendar = Calendar.getInstance()
        cal1.set(Calendar.HOUR_OF_DAY, hourOfTheDay)
        cal1.set(Calendar.MINUTE, 0)
        cal1.set(Calendar.SECOND, 0)

        // Initialize a calendar with now.
        val cal2: Calendar = Calendar.getInstance()
        if (cal2.timeInMillis < cal1.timeInMillis) {
            // Add the worker periodicity.
            cal2.timeInMillis = cal2.timeInMillis + TimeUnit.DAYS.toMillis(periodInDays.toLong())
        }
        val delta: Long = cal2.timeInMillis - cal1.timeInMillis

        return if (delta > PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS) delta else PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS
    }

    private fun setUpWorker() {
        val workManager = WorkManager.getInstance(this)

        val contraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED).build()
        val hora = 14
        val intervalo = 1
        val flexTime = calculateFlex(hora, intervalo)

        val loadPreciosWorkRequest = PeriodicWorkRequest.Builder(
            PreciosWorker::class.java,
            intervalo.toLong(), TimeUnit.DAYS, flexTime, TimeUnit.MILLISECONDS
        )
            .setConstraints(contraints)
            .setBackoffCriteria(BackoffPolicy.LINEAR,PeriodicWorkRequest.DEFAULT_BACKOFF_DELAY_MILLIS,
            TimeUnit.MILLISECONDS)
            .build()


        workManager.enqueueUniquePeriodicWork(
            "LoadPrecios",
            ExistingPeriodicWorkPolicy.REPLACE,
            loadPreciosWorkRequest
        )
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.item_about-> {
                navController.navigate(R.id.action_navHomeFragment_to_navAboutFragment)
            }
            R.id.item_reload->{
                preciosViewModel.loadPrecios()
            }
        }
        return true
    }




}

