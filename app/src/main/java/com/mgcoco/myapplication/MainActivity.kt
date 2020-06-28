package com.mgcoco.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val frameAnimation = findViewById<View>(R.id.bbb).background as AnimationDrawable
//        frameAnimation.start()
    }

//    fun load_animations() {
//        AnimationUtils()
//        val rotation = AnimationUtils.loadAnimation(this, R.anim.rotation)
//        findViewById(R.id.bbb).startAnimation(rotation)
//    }
}
