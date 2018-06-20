package com.example.user.todoproject.view


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.user.todoproject.R
import com.example.user.todoproject.model.Todo
import com.example.user.todoproject.model.User
import io.realm.Realm
import kotlinx.android.synthetic.main.dialog_layout.view.*
import kotlinx.android.synthetic.main.fragment_todo.*
import org.jetbrains.anko.support.v4.toast
import java.text.SimpleDateFormat


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */

class TodoFragment : Fragment() {

    var id:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = arguments!!.getString("id")
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        return inflater.inflate(R.layout.fragment_todo, container, false)
        // Inflate the layout for this fragment

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textTitle.text = id
    }


}
