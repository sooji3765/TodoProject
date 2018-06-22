package com.example.user.todoproject.view


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

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

    lateinit var realm: Realm

    private var adapter : TodoAdapter? = null
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

        realm = Realm.getDefaultInstance()
        var todoList = realm.where(Todo::class.java).equalTo("id",id).findAll()


        recycleview.layoutManager = LinearLayoutManager(activity)
        recycleview.setHasFixedSize(true)
        adapter = TodoAdapter(activity!!.applicationContext,todoList, object : TodoRecyclerViewClickListener{
            override fun onTodoClick(position: Int) {
                toast("${todoList.get(position)!!.content}")
            }

            override fun onTodoCheck(check: Boolean, position: Int) {
                realm.beginTransaction()
                todoList.get(position)?.checkbox = check
                realm.commitTransaction()
                adapter!!.notifyDataSetChanged()
            }

            override fun onTodoDelete(position: Int) {

                realm.beginTransaction()
                todoList.get(position)!!.deleteFromRealm()
                realm.commitTransaction()
                realm.close()
                adapter!!.notifyDataSetChanged()
                Log.i("todo 삭제","삭제 성공")
            }

        })
        recycleview.adapter = adapter
        adapter!!.notifyDataSetChanged()

        memoAdd.setOnClickListener {
            buildAndShowInputDialog(id)
        }
    }
    private fun buildAndShowInputDialog(userid: String?) {
        val builder = AlertDialog.Builder(this!!.context!!)
        builder.setTitle("할 일을 적아주세요")

        val li = LayoutInflater.from(context)
        val dialogView = li.inflate(R.layout.dialog_layout, null)
        val input = dialogView.dialogEt

        builder.setView(dialogView)
        builder.setPositiveButton("OK") { dialog, which -> addToDoItem(input.text.toString(), userid) }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        builder.show()
    }

    private fun addToDoItem(toDoItemText: String?, userid: String?) {
        if (toDoItemText == null || toDoItemText.length == 0) {
            Toast.makeText(context, "Empty ToDos don't get stuff done!", Toast.LENGTH_SHORT).show()
            return
        }

        realm.beginTransaction()
        var nextNum: Long = realm.where(User::class.java).count() + 1
        val todoItem = realm.createObject(Todo::class.java)
        var simpleDateFormat = SimpleDateFormat("yyyy-mm-dd")
        todoItem.todoNum = nextNum
        todoItem.date = simpleDateFormat.format(System.currentTimeMillis())
        todoItem.content = toDoItemText
        todoItem.id = userid
        todoItem.checkbox = false
        realm.commitTransaction()
        adapter?.notifyDataSetChanged()
    }


}
