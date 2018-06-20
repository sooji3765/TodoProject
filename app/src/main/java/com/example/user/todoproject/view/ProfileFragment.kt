package com.example.user.todoproject.view


import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Camera
import android.util.*
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.Toast
import com.bumptech.glide.Glide

import com.example.user.todoproject.R
import com.example.user.todoproject.model.User
import es.dmoral.toasty.Toasty
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ProfileFragment : Fragment() {

    private val GALLERY = 1
    private val CAMERA =2
    lateinit var realm: Realm
    var id : String?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        id = arguments!!.getString("id")

        realm = Realm.getDefaultInstance()
        val user = realm.where(User::class.java).equalTo("id",id).findAll()
        val email = user.get(0)!!.email.toString()
        val image = user.get(0)!!.profileImage.toString()

        userID.text = id
        userEmail.text = email

        Glide.with(this).load(image).into(imageButton)

        imageButton.setOnClickListener { showPictureDialog() }

    }

    @Suppress("UNREACHABLE_CODE")
    private fun showPictureDialog() {

        val pictureDialog = AlertDialog.Builder(context)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItem = arrayOf("카메라","갤러리")
        pictureDialog.setItems(pictureDialogItem){
            dialog, which ->
            when(which){
                0 -> choosePhotoFromGallery()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()

    }

    @Suppress("UNREACHABLE_CODE")
    private fun takePhotoFromCamera() {

        val galleryIntent = Intent(Intent.ACTION_PICK
        , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent,GALLERY)

    }

    @Suppress("UNREACHABLE_CODE")
    private fun choosePhotoFromGallery() {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent,CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GALLERY){
            if (data!= null){
                val contentURI = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver,contentURI)
                    val path = saveImage(bitmap)

                    Log.i("imgaePath",path)
                    realm.executeTransaction {
                        val user = realm.where(User::class.java).equalTo("id",id).findFirst()
                        user!!.profileImage = path
                    }
                    Toasty.success(this.context!!,"성공",Toast.LENGTH_SHORT).show()
                    imageButton.setImageBitmap(bitmap)

                }catch (e: IOException){
                    e.printStackTrace()
                    Toasty.error(this.context!!,"실패",Toast.LENGTH_SHORT).show()
                }

            }
        }else if (requestCode == CAMERA)
        {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            imageButton!!.setImageBitmap(thumbnail)
            saveImage(thumbnail)
            Toast.makeText(activity, "Image Saved!", Toast.LENGTH_SHORT).show()
        }


    }

    private fun saveImage(myBitmap: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
                (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
        // have the object build the directory structure, if needed.
        Log.d("fee",wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists())
        {

            wallpaperDirectory.mkdirs()
        }

        try
        {
            Log.d("heel",wallpaperDirectory.toString())
            val f = File(wallpaperDirectory, ((Calendar.getInstance()
                    .timeInMillis).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(context,
                    arrayOf(f.path),
                    arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.absolutePath)

            return f.absolutePath
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }

    companion object {
        private const val IMAGE_DIRECTORY = "/demonuts"
    }


}
