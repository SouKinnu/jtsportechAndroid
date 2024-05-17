package com.jtsportech.visport.android.playerdetail

import android.view.View
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.cloudhearing.android.lib_base.base.BaseBindingVmActivity
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.image.loadRoundCornerImage
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.Team1Player
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.ActivityPlayerDeatilBinding
import com.jtsportech.visport.android.utils.img

class PlayerDetailActivity :
    BaseBindingVmActivity<ActivityPlayerDeatilBinding, PlayerDetailViewModel>(
        ActivityPlayerDeatilBinding::inflate
    ) {
    private val teamPlayer: Team1Player by lazy {
        intent.extras!!.getSerializable("teamPlayer") as Team1Player
    }
    private val playerAdapter: PlayerAdapter by lazy {
        PlayerAdapter(this, teamPlayer.playerFrontUserId)
    }
    private lateinit var height: String
    private lateinit var weight: String
    private lateinit var number: String
    override fun initView() {
        height = teamPlayer.playerUserHeight.toString() + ContextCompat.getString(
            binding.root.context,
            R.string.player_height_unit
        )
        weight = teamPlayer.playerUserWeight.toString() + ContextCompat.getString(
            binding.root.context,
            R.string.player_weight_unit
        )
        number = teamPlayer.uniformNo.toString() + ContextCompat.getString(
            binding.root.context,
            R.string.player_no
        )
    }

    override fun initData() {
        binding.number.text = number
        binding.name.text = teamPlayer.playerFrontUserName
        binding.location.text = teamPlayer.pitchPosition
        binding.height.text = height
        binding.weight.text = weight
        binding.Place.text = teamPlayer.organizationName
        binding.image.loadRoundCornerImage(
            url = teamPlayer.playerUserAvatarPath?.img(),
            radius = 6.toDp.toInt(),
            placeHolder = R.drawable.ic_avatar_mine_def
        )
        binding.ViewPager2.adapter = playerAdapter
        binding.ViewPager2.currentItem = 0
    }

    override fun initEvent() {
        binding.back.setOnClickListener {
            finish()
        }
        binding.ViewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> binding.all.isChecked = true
                    1 -> binding.race.isChecked = true
                    2 -> binding.events.isChecked = true
                    3 -> binding.train.isChecked = true
                    4 -> binding.personal.isChecked = true
                }
            }
        })
        binding.RadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.all.id -> {
                    binding.ViewPager2.currentItem = 0
                    binding.allStrip.visibility = View.VISIBLE
                    binding.raceStrip.visibility = View.INVISIBLE
                    binding.eventsStrip.visibility = View.INVISIBLE
                    binding.trainStrip.visibility = View.INVISIBLE
                    binding.personalStrip.visibility = View.INVISIBLE
                }

                binding.race.id -> {
                    binding.ViewPager2.currentItem = 1
                    binding.allStrip.visibility = View.INVISIBLE
                    binding.raceStrip.visibility = View.VISIBLE
                    binding.eventsStrip.visibility = View.INVISIBLE
                    binding.trainStrip.visibility = View.INVISIBLE
                    binding.personalStrip.visibility = View.INVISIBLE
                }

                binding.events.id -> {
                    binding.ViewPager2.currentItem = 2
                    binding.allStrip.visibility = View.INVISIBLE
                    binding.raceStrip.visibility = View.INVISIBLE
                    binding.eventsStrip.visibility = View.VISIBLE
                    binding.trainStrip.visibility = View.INVISIBLE
                    binding.personalStrip.visibility = View.INVISIBLE
                }

                binding.train.id -> {
                    binding.ViewPager2.currentItem = 3
                    binding.allStrip.visibility = View.INVISIBLE
                    binding.raceStrip.visibility = View.INVISIBLE
                    binding.eventsStrip.visibility = View.INVISIBLE
                    binding.trainStrip.visibility = View.VISIBLE
                    binding.personalStrip.visibility = View.INVISIBLE
                }

                binding.personal.id -> {
                    binding.ViewPager2.currentItem = 4
                    binding.allStrip.visibility = View.INVISIBLE
                    binding.raceStrip.visibility = View.INVISIBLE
                    binding.eventsStrip.visibility = View.INVISIBLE
                    binding.trainStrip.visibility = View.INVISIBLE
                    binding.personalStrip.visibility = View.VISIBLE
                }
            }
        }
    }

}