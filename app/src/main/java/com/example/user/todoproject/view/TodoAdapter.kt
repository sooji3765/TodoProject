package com.example.user.todoproject.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.user.todoproject.R
import com.example.user.todoproject.model.Todo
import io.realm.RealmResults
import kotlinx.android.synthetic.main.list_layout.view.*


open interface TodoRecyclerViewClickListener{
    fun onTodoClick(position : Int)
    fun onTodoCheck(check :Boolean, position:Int)
    fun onTodoDelete(position : Int)
}
class TodoAdapter(val context : Context, val todoList : RealmResults<Todo>, val click: TodoRecyclerViewClickListener) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkTodo = itemView?.checkbox
        val title = itemView?.title
        val deleteBtn = itemView?.deleteBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val  view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.checkTodo?.isChecked = todoList!![position]!!.checkbox
        holder.checkTodo?.setOnClickListener ({
            click.onTodoCheck(holder.checkTodo?.isChecked, position)
            })

        holder.title.text = todoList!![position]!!.content
        holder.deleteBtn?.setOnClickListener({
            click.onTodoDelete(position)
        })
        holder.itemView?.setOnClickListener({
            click.onTodoClick(position)
        })
    }

}


