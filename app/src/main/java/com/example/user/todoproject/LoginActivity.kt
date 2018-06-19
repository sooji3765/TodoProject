package com.example.user.todoproject

import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.user.todoproject.model.User
import es.dmoral.toasty.Toasty
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity() {

    lateinit var realm: Realm
    lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        realm = Realm.getDefaultInstance()

        // 임시저장
        var setting : SharedPreferences = getSharedPreferences("setting",0)
        editor = setting.edit()


        /*val checkSwitch = setting.getBoolean("logincheck",false)
        switchAuto.isChecked = checkSwitch


        //자동 로그인
        if (checkSwitch){
            val id = setting.getString("id","")
            val pass = setting.getString("pass","")
            val user = realm.where(User::class.java).equalTo("Id",id).findAll()

            if (user.last()?.password == pass&&user.size > 0){
                var intent = Intent(this, MainActivity::class.java)
                intent.putExtra("id", id)
                startActivity(intent)
                finish()
            }

        }*/


        loginBtn.setOnClickListener {

            if (userCheck() == true) {
                Toasty.success(this,"로그인 성공",Toast.LENGTH_SHORT,true).show()
                var intent = Intent(this, MainActivity::class.java)
                intent.putExtra("id", idInput.text.toString().trim())
                startActivity(intent)
                finish()
            }
        }

        // 회원가입
        signup.setOnClickListener {
            var intentJoin = Intent(this, JoinActivity::class.java)
            startActivity(intentJoin)
        }


    }
    private fun userCheck(): Boolean {
        var id = idInput.text.toString().trim()
        var pass = passInput.text.toString().trim()


        val query = realm.where(User::class.java)
        val user = query.equalTo("id", id).findAll()

        // 결과가 없으면
        if (user.size == 0) {
            Toasty.warning(this,"일치하는 정보 없습니다.",Toast.LENGTH_SHORT,true).show()

            return false
        } else {
            if (user.get(0)!!.password.toString() == pass) {

                if(switchAuto.isChecked){
                    editor.putBoolean("logincheck",true)

                }else{
                    editor.putBoolean("logincheck",false)
                }

                editor.putString("id",id)
                editor.putString("pass",pass)
                editor.apply()

                return true
            } else {
                Toasty.error(this,"잘못된 비밀번호 입니다.",Toast.LENGTH_SHORT,true).show()
            }
        }
        return false
    }
}
