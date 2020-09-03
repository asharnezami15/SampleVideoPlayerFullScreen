package com.android.videoplayer


/**
 *
 * @author Khwaja Ashar Nezami
 */
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.MediaController
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var isFullScreen: Boolean = false
    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initVideoPlayer()
        clickListener()
    }

    private fun initVideoPlayer() { // initialize video player
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        val uri: Uri = Uri.parse("android.resource://$packageName/" + R.raw.videos)  // add your videos from drawable

        videoView.apply {
            setMediaController(mediaController)
            setVideoURI(uri)
            requestFocus()
            pause()
            setOnPreparedListener {
                mediaController.show() // to show initial default button
            }
        }
    }

    private fun clickListener() {
        iv_expand.setOnClickListener {
            checkOrientation()
        }

    }

    private fun checkOrientation() { // validation of landscape and portrait mode
        if (!isFullScreen) {
            fullScreen()
        } else {
            exitFullScreen()
        }

    }

    override fun onPause() {
        videoView.pause()
        position = videoView.currentPosition // save video position when app is in background
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        videoView.seekTo(position) // retrieve position when video came back in foreground

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) { // retrieve position while change orientation
        savedInstanceState.let {
            val currentPos: Int = it.getInt(AppConstants.CURRENT_POSITION)
            videoView.seekTo(currentPos)
        }
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) { // save position while changing orientation
        outState.putInt(AppConstants.CURRENT_POSITION, videoView.currentPosition)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() { // stop playback when activity destroyed
        videoView.stopPlayback()
        super.onDestroy()
    }


    private fun exitFullScreen() { // get back his original position (portrait)
        isFullScreen = false
        iv_expand.setImageResource(R.drawable.ic_expand)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.apply {
            addRule(RelativeLayout.ALIGN_PARENT_TOP)
            addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            videoView.layoutParams = layoutParams
        }

    }

    private fun fullScreen() { // play in full screen
        isFullScreen = true
        iv_expand.setImageResource(R.drawable.fullscreen_exit)
       /* window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )*/
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams.apply {
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            addRule(RelativeLayout.ALIGN_PARENT_TOP)
            addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            videoView.layoutParams = layoutParams
        }
    }

    override fun onBackPressed() { // when user tap on device back button
        if (isFullScreen) {
            exitFullScreen()
        } else {
            super.onBackPressed()
        }

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

}