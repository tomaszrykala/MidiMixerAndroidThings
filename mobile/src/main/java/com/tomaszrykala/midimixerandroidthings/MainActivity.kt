package com.tomaszrykala.midimixerandroidthings

import android.arch.lifecycle.LifecycleRegistry
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.ToggleButton
import com.tomaszrykala.midimixerandroidthings.midi.MidiController
import com.tomaszrykala.midimixerandroidthings.ui.DeviceAdapter
import com.tomaszrykala.midimixerandroidthings.ui.MidiMixer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val lifecycleRegistry: LifecycleRegistry by lazyFast { LifecycleRegistry(this) }
    override fun getLifecycle(): LifecycleRegistry = lifecycleRegistry

    val midiController: MidiController by viewModelProvider {
        MidiController(application)
    }

    private val deviceAdapter: DeviceAdapter by lazyFast {
        DeviceAdapter(this, { it.inputPortCount > 0 })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        initLockTaskOrToolbarTitle(false)

        midiController.observeDevices(this, deviceAdapter)

        supportFragmentManager.beginTransaction()?.also {
            it.replace(R.id.content_main, Fragment.instantiate(this, MidiMixer::class.java.canonicalName))
            it.commit()
        }
    }

    private fun initLockTaskOrToolbarTitle(initLockTaskButton: Boolean) {
        supportActionBar?.apply {
            if (!initLockTaskButton) {
                setTitle(R.string.app_name)
            }
        }
        findViewById<ToggleButton>(R.id.lock_task_button)?.apply {
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) startLockTask() else stopLockTask()
            }
            visibility = if (initLockTaskButton) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu?.findItem(R.id.app_bar_selector)?.actionView?.apply {
            findViewById<Spinner>(R.id.output_selector)?.apply {
                adapter = deviceAdapter
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        midiController.closeAll()
                    }

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        deviceAdapter[position].apply {
                            midiController.open(this)
                        }
                    }
                }
            }
        }
        return true
    }

    override fun onDestroy() {
        midiController.closeAll()
        super.onDestroy()
    }
}