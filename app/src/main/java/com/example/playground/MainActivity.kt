package com.example.playground

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import com.example.playground.databinding.ActivityMainBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

private val TAG = MainActivity::class.simpleName

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var flow: Flow<Int>
    private lateinit var flow2: Flow<Int>

    private lateinit var observable: Observable<Int>
    private lateinit var observable2: Observable<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        setupObservable()
        setupClicks()
    }

    private fun setupFlow() {
        flow = flow {
            Log.d(TAG, "Start flow")
            (0..10).forEach {
                delay(500)
                Log.d(TAG, "Emitting $it")
                emit(it)
            }
            Log.d(TAG, "End flow")
        }.flowOn(Dispatchers.Default)

        flow2 = flow.map { it * it }
    }

    private fun setupClicks() {
        binding.fab.setOnClickListener {
//            CoroutineScope(Dispatchers.Main).launch {
//                flow.zip(flow2) { result1, result2 ->
//                    "$result1 - $result2"
//                }.collect {
//                    Log.d(TAG, it)
//                }
//            }
            Observable.zip(observable, observable2) { result1, result2 ->
                "$result1 - $result2"
            }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { Log.d(TAG, it) }
        }
    }

    private fun setupObservable() {
        observable = Observable.intervalRange(0, 11, 0, 500, TimeUnit.MILLISECONDS)
            .map { it.toInt() }
            .doOnNext { Log.d(TAG, "onNext($it)") }
            .doOnSubscribe { Log.d(TAG, "Start stream") }
            .doOnComplete { Log.d(TAG, "End stream") }
            .subscribeOn(Schedulers.computation())
        observable2 = observable.map { it * it }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}