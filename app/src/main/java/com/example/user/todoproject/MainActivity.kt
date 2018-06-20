package com.example.user.todoproject

import android.app.FragmentManager
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.example.user.todoproject.model.User
import com.example.user.todoproject.view.ProfileFragment
import com.example.user.todoproject.view.TodoFragment

import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var realm: Realm
    lateinit var id : String
    var todoFragment=TodoFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        var intent = intent
        id = intent.getStringExtra("id")

        val fragmentTransaction  = supportFragmentManager.beginTransaction()
        val bundle = Bundle(1)
        bundle.putString("id",id)

        todoFragment.arguments = bundle

        fragmentTransaction.add(R.id.fragment,todoFragment,id).commit()

        realm = Realm.getDefaultInstance()
        val user = realm.where(User::class.java).equalTo("id", id).findAll()



        val nav_header_view: View = nav_view.getHeaderView(0)
        val userImage = user.get(0)!!.profileImage.toString()

        // 이미지 설정
        if(userImage==""){
            Glide.with(this).load(R.mipmap.ic_launcher_round)
                    .into(nav_header_view.nav_image)
        }else{
            Glide.with(this).load(userImage)
                    .into(nav_header_view.nav_image)
        }

        nav_header_view.nav_id.text = "${user.get(0)!!.id.toString()}"
        nav_header_view.nav_email.text = "${user.get(0)!!.email.toString()}"


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_out -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_list -> {
                replaceFragment(TodoFragment())

            }
            R.id.nav_setting-> {
                replaceFragment(ProfileFragment())

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    // Fragment 변환을 해주기 위한 부분, Fragment의 id를 받아서 변경
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val bundle = Bundle(1)
        bundle.putString("id",id)
        fragment.arguments = bundle
        fragmentTransaction.replace(R.id.fragment, fragment).commit()
    }


}

