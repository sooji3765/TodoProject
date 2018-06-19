package com.example.user.todoproject.app

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.user.todoproject.LoginActivity
import com.example.user.todoproject.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        try {
            //2초간 대기
            Thread.sleep(2000);

        }catch (e:InterruptedException){
            e.printStackTrace()
        }
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
