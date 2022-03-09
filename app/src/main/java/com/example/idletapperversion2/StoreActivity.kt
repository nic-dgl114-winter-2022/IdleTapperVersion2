package com.example.idletapperversion2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.idletapperversion2.adapter.ItemAdapter
import com.example.idletapperversion2.data.Datasource
import com.example.idletapperversion2.databinding.ActivityStoreBinding
import kotlin.math.roundToInt

class StoreActivity : AppCompatActivity() {

    companion object {
        const val STATE_TAPS = "tapCount"
        const val STATE_TAPPOW = "tapPower"
        const val STATE_IDLEPOW = "idlePower"
        const val STATE_TAPUPGRADES = "tapUpgrades"
        const val STATE_IDLEUPGRADES = "idleUpgrades"
    }

    val TAG = "storeActivity"

    //flag to prevent overwriting loaded bundle varibles with extras from MainActivity
    private var loadedBundle = false

    var tapCount = 0
    var tapPower = 1
    var idlePower = 0

    //base factors for determining upgrade cost
    private val baseUpgradeCost = 10
    private val costIncreaseFactor = 1.15

    //upgrade Levels for buttons
    private var tapUpgradeLevel = 0
    private var idleUpgradeLevel = 0

    private lateinit var binding: ActivityStoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storeTextList = Datasource().loadData()
        binding.recyclerView.adapter = ItemAdapter(this, storeTextList)


        val returnButton : Button = binding.returnButton
        returnButton.setOnClickListener {
            val context = returnButton.context
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("taps", tapCount)
            intent.putExtra("tapPower", tapPower)
            intent.putExtra("idlePower", idlePower)
            intent.putExtra("tapUpgrades", tapUpgradeLevel)
            intent.putExtra("idleUpgrades", idleUpgradeLevel)
            context.startActivity(intent)
        }

        //load the saved instance state if one exists
        if (savedInstanceState != null) {
            with(savedInstanceState) {
                tapCount = getInt(STATE_TAPS)
                tapPower = getInt(STATE_TAPPOW)
                idlePower = getInt(STATE_IDLEPOW)
                tapUpgradeLevel = getInt(STATE_TAPUPGRADES)
                idleUpgradeLevel = getInt(STATE_IDLEUPGRADES)

                loadedBundle = true
            }

        }

        //start function to generate idle taps
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                idleTap()
                handler.postDelayed(this, 1000)//1 sec delay
            }
        }, 0)
    }

    override fun onStart() {
        super.onStart()
        //if statement to prevent the contents of the bundle from getting overwritten
        //if they were loaded in
        if(!loadedBundle) {
            tapCount = intent.getIntExtra("taps", tapCount)
            tapPower = intent.getIntExtra("tapPower", tapPower)
            idlePower = intent.getIntExtra("idlePower", idlePower)
            tapUpgradeLevel = intent.getIntExtra("tapUpgrades", tapUpgradeLevel)
            idleUpgradeLevel = intent.getIntExtra("idleUpgrades", idleUpgradeLevel)
        }

        updateTapCount()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.run {
            putInt(STATE_TAPS, tapCount)
            putInt(STATE_TAPPOW, tapPower)
            putInt(STATE_IDLEPOW, idlePower)
            putInt(STATE_TAPUPGRADES, tapUpgradeLevel)
            putInt(STATE_IDLEUPGRADES, idleUpgradeLevel)
        }

    }

    private fun idleTap() {
        tapCount += idlePower
        binding.tapCounter.text = getString(R.string.tap_count, tapCount)
    }

    fun updateTapCount() {
        binding.tapCounter.text = getString(R.string.tap_count, tapCount)
    }

    //determines the costs of an upgrade
    fun getUpgradeCost(baseCost: Int, costIncreaseFactor: Double, upgradeLevel: Int): Int {
        var cost = baseCost
        var x = 0

        //each upgrade level cost is the previous level cost * costIncreaseFactor
        while(x < upgradeLevel) {
            cost = (cost * costIncreaseFactor).roundToInt()
            x++
        }

        return cost
    }
}