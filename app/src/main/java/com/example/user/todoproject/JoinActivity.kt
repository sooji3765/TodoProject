package com.example.user.todoproject

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import com.example.user.todoproject.model.User
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_join.*
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.toast
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class JoinActivity : AppCompatActivity() {

    val pwPattern = Pattern.compile("^(?=.*\\d)(?=.*[~`!@#\$%\\^&*()-])(?=.*[a-z])(?=.*[A-Z]).{8,}")

    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        joinBtn.isEnabled = false
        realm = Realm.getDefaultInstance()


        val lengthGreaterThan5 = ObservableTransformer<String, String> { obserable ->

            obserable.flatMap {
                io.reactivex.Observable.just(it).map { it.trim() }
                        .filter { it.length > 5 }
                        .singleOrError()
                        .onErrorResumeNext {
                            if (it is NoSuchElementException) {
                                Single.error(Exception("5자 이상 입력해주세요"))
                            } else {
                                Single.error(it)
                            }
                        }
                        .toObservable()
            }
        }

        var realmcheck = realm.where(User::class.java)


        // 이메일
        val verifyEmailPattern = ObservableTransformer<String, String> { observable ->
            observable.flatMap {
                io.reactivex.Observable.just(it).map { it.trim() }
                        .filter { Patterns.EMAIL_ADDRESS.matcher(it).matches() }
                        .singleOrError()
                        .onErrorResumeNext {
                            if (it is NoSuchElementException) {
                                Single.error(Exception("이메일 메형식이 아닙니다."))
                            } else {
                                Single.error(it)
                            }
                        }
                        .toObservable()
            }
        }

        //비밀번호 패턴
        val passwordPattern = ObservableTransformer<String, String> { observable ->
            observable.flatMap {
                io.reactivex.Observable.just(it).map { it.trim() }
                        .filter { pwPattern.matcher(it).matches() }
                        .singleOrError()
                        .onErrorResumeNext {
                            if (it is NoSuchElementException) {
                                Single.error(Exception("비밀번호 형식에 맞게 입력해주세요"))
                            } else {
                                Single.error(it)
                            }
                        }
                        .toObservable()
            }
        }

        // 비밀번호 비교
        val verifyPass2Pattern = ObservableTransformer<String, String> { observable ->
            observable.flatMap {
                io.reactivex.Observable.just(it).map { it.trim() }
                        .filter { it -> it.equals(passText.text.toString()) }
                        .singleOrError()
                        .onErrorResumeNext {
                            if (it is NoSuchElementException) {
                                Single.error(Exception("비밀번호를 동일하게 입력해주세요."))
                            } else {
                                Single.error(it)
                            }
                        }
                        .toObservable()
            }
        }

        val emailObservable = RxTextView.textChanges(emailText)
                .map { it ->
                    Patterns.EMAIL_ADDRESS.matcher(it).matches()
                }


        val passObservable = RxTextView.textChanges(passText)
                .map { it ->
                    pwPattern.matcher(it).matches()
                }


        RxTextView.textChanges(emailText)
                .skipInitialValue()
                .map {
                    emailLayout.error = null
                    it.toString()
                }
                .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(verifyEmailPattern)
                .compose(errorMessae {
                    emailLayout.error = it.message
                })
                .subscribe(

                )


        RxTextView.textChanges(idText)
                .skipInitialValue()
                .map {
                    idLayout.error = null
                    it.toString()
                }
                .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(lengthGreaterThan5)
                .compose(errorMessae {
                    idLayout.error = it.message
                })
                .subscribe(

                )


        // 에러표시  변경

        RxTextView.textChanges(passText)
                .skipInitialValue()
                .map { it.toString() }
                .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(passwordPattern)
                .compose(errorMessae {
                    passLayout.error = it.message
                })
                .subscribe()


        RxTextView.textChanges(pass2Text)
                .skipInitialValue()
                .map {
                    passLayout2.error = null
                    it.toString()
                }
                .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(verifyPass2Pattern)
                .compose(errorMessae {
                    passLayout2.error = it.message
                })
                .subscribe(
                        {
                            Log.i("onNext ", " $it looks good")
                        }, { Log.i("onError", " ${it.message}") }
                        , { Log.i("onComplete", "error") }
                )


        val signButton: Observable<Boolean> = Observable.combineLatest(emailObservable, passObservable, BiFunction { t1, t2 -> t1 && t2 })


        signButton.distinctUntilChanged()
                .subscribe { enable -> joinBtn.isEnabled = enable }

        joinBtn.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)

            realm.beginTransaction()

            val nextNum: Long = realm.where(User::class.java).count() + 1
            var user = realm.createObject(User::class.java, nextNum)

            user.email = emailText.text.toString().trim()
            user.id = idText.text.toString().trim()
            user.password = passText.text.toString().trim()

            Log.v("user infomation", "${user.email}")
            realm.commitTransaction()


            toast("회원가입이 완료되었습니다.")

            startActivity(intent)
            finish()
        }

    }

    private inline fun errorMessae(crossinline onError: (ex: Throwable) -> Unit): ObservableTransformer<String, String> = ObservableTransformer { observable ->
        observable.retryWhen { errors ->
            errors.flatMap {
                onError(it)
                io.reactivex.Observable.just("")
            }
        }
    }


    @Override
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
