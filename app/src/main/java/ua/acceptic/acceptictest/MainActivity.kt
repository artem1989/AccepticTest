package ua.acceptic.acceptictest

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import android.widget.VideoView

import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val RECORD_REQUEST_CODE = 100

    private var photoViewModel: PhotoViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupPermissions()
    }

    private fun initAnalogClock() {
        analogClock.setAutoUpdate(true)
        analogClock.setScale(0.9f)
    }

    private fun initViewFlipper() {
        viewFlipper.inAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        viewFlipper.outAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        viewFlipper.isAutoStart = true
        viewFlipper.setFlipInterval(3000)
        viewFlipper.startFlipping()
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")
            makeRequest()
        } else {

        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                RECORD_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Read storage Denied", Toast.LENGTH_SHORT).show()
                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    initAnalogClock()
                    initViewFlipper()

                    photoViewModel = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
                    photoViewModel!!.getAllPhotos().observe(this, Observer { photos: List<Media>? ->
                        for (photo in photos!!) {

                            var mediaView: View
                            when (photo.mediaType) {
                                MediaType.IMAGE -> {
                                    mediaView = ImageView(this)
                                    mediaView.scaleType = ImageView.ScaleType.FIT_CENTER
                                    Picasso.get().load("file:///" + photo.path).into(mediaView)
                                    viewFlipper.addView(mediaView)
                                }

                                MediaType.VIDEO -> {
                                    mediaView = VideoView(this)
                                    mediaView.setVideoPath(photo.path)
                                    mediaView.start()
                                    viewFlipper.addView(mediaView)
                                }
                            }
                        }
                    })
                    Log.i(TAG, "Permission has been granted by user")
                }
            }
        }
    }
}
