package jp.rie.ijichi.dialyapp

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.PendingIntent.getActivity
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v7.app.AlertDialog
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.activity_edit.view.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Exception
import java.security.Permissions
import java.util.*
import kotlin.collections.HashMap

class EditActivity : AppCompatActivity() {

    companion object {
        private val REQUEST_CODE_CAMERA = 100
        private val REQUEST_CODE_LIBRALY = 200
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabaseReference: DatabaseReference

    private lateinit var rxPermissions: RxPermissions

    private var cameraFileUri: Uri? = null

    private var editType = false

    private var isDeleteMenuVisible = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        mDatabaseReference = FirebaseDatabase.getInstance().reference

        mAuth = FirebaseAuth.getInstance()


        edit_done_button.setOnClickListener {
            diaryRegister()
        }

        edit_day_text.setOnClickListener {
            showDatePickerDialog()
        }

        val preference = PreferenceManager.getDefaultSharedPreferences(this)
        editType = preference.getBoolean(EDIT_TYPE, false)
        if (editType) {
            //編集時
            intent.extras.let {
                val day = it.getString(KEY_DAY)
                val title = it.getString(KEY_TITLE)
                val text = it.getString(KEY_TEXT)
                edit_day_text.text = day
                edit_title_edit.setText(title)
                edit_text_edit.setText(text)
            }
            setTitle("編集")
            invalidateOptionsMenu()
        } else {
            //新規作成時
            setTitle("新規作成")
            isDeleteMenuVisible = false
            invalidateOptionsMenu()
        }

        edit_image.setOnClickListener {
            showPhotoSelectionDialog()
            rxPermissions = RxPermissions(this)
            if (rxPermissions.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showPhotoSelectionDialog()
                Toast.makeText(this, "すでに許可されてる", Toast.LENGTH_SHORT).show()
            } else {
                showPhotoSelectionDialog()
                Toast.makeText(this, "許可されてない", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != Activity.RESULT_OK) return
        when (requestCode) {
            REQUEST_CODE_CAMERA -> {
                data?.data?.let {
                    val image: Bitmap
                    try {
                        val contentResolver = contentResolver
                        val inputStream = contentResolver.openInputStream(it)
                        image = BitmapFactory.decodeStream(inputStream)
                        inputStream!!.close()
                    } catch (e: Exception) {
                        return
                    }

                    val imageWidth = image.width
                    val imageHeight = image.height
                    val scale = Math.min(500.toFloat() / imageWidth, 500.toFloat() / imageHeight)

                    val matrix = Matrix()
                    matrix.postScale(scale, scale)

                    val resizeImage = Bitmap.createBitmap(image, 0, 0, imageWidth, imageHeight, matrix, true)

                    edit_image.setImageBitmap(resizeImage)

                    cameraFileUri = null

                }
            }
            REQUEST_CODE_LIBRALY -> {
                val uri = data?.data ?: cameraFileUri ?: return
                val image: Bitmap
                try {
                    val contentResolver = contentResolver
                    val inputStream = contentResolver.openInputStream(uri)
                    image = BitmapFactory.decodeStream(inputStream)
                    inputStream!!.close()
                } catch (e: Exception) {
                    return
                }

                val imageWidth = image.width
                val imageHeight = image.height
                val scale = Math.min(500.toFloat() / imageWidth, 500.toFloat() / imageHeight)

                val matrix = Matrix()
                matrix.postScale(scale, scale)

                val resizeImage = Bitmap.createBitmap(image, 0, 0, imageWidth, imageHeight, matrix, true)

                edit_image.setImageBitmap(resizeImage)

                cameraFileUri = null
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        menu?.findItem(R.id.menu_edit_delete)?.isVisible = isDeleteMenuVisible
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_edit_delete -> deleteDialog()
            R.id.menu_edit_stop -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteDialog() {
        AlertDialog.Builder(this)
            .setTitle("この記録を削除しますか？")
            .setPositiveButton("OK") { dialog, which ->
            }
            .setNegativeButton("CANCEL") { dialog, which ->
            }
            .show()
    }

    private fun diaryRegister() {
        val diaryRef = mDatabaseReference.child(DiaryPATH)
        val data = HashMap<String, String>()
        val title = edit_title_edit.text.toString()
        val text = edit_text_edit.text.toString()
        val day = edit_day_text.text.toString()
        val image = edit_image.drawable as? BitmapDrawable

        if (image != null) {
            val bitmap = image.bitmap
            val byte = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byte)
            val bitmapString = Base64.encodeToString(byte.toByteArray(), Base64.DEFAULT)
            data["image"] = bitmapString

        }
        if (title.isEmpty()) {
            Toast.makeText(this, "タイトルを入力してください", Toast.LENGTH_SHORT).show()
        }
        if (text.isEmpty()) {
            Toast.makeText(this, "本文を入力してください", Toast.LENGTH_SHORT).show()
        }
        if (day.isEmpty()) {
            Toast.makeText(this, "日付を入力してください", Toast.LENGTH_SHORT).show()
        }
        data["title"] = title
        data["text"] = text
        data["day"] = day

        diaryRef.push().setValue(data)
        Toast.makeText(this, "保存しました。", Toast.LENGTH_SHORT).show()

    }

    private fun showDatePickerDialog() {
        val calender = Calendar.getInstance()
        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val day = calender.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val dataString = String.format("$year/$month/$dayOfMonth")
                edit_day_text.text = dataString

            }, year, month, day
        )
        datePickerDialog.show()
    }

    private fun showPhotoSelectionDialog() {
        val items = arrayOf("写真を撮る", "ライブラリから選択")
        AlertDialog.Builder(this).setItems(items, DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> showRequestPermission(true) {
                    startCamera()
                }
                1 -> showRequestPermission(false) {
                    startLibrary()
                }
            }
        }).show()

    }

    private fun showRequestPermission(isCamera: Boolean, transitionCallback: () -> Unit) {
        if (isCamera) {
            rxPermissions
                .request(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
        } else {
            rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        }
            .subscribe { granted ->
                if (granted) {
                    transitionCallback.invoke()
                }
            }
    }


    private fun startCamera() {
        val filename = System.currentTimeMillis().toString() + ".jpg"
        val valuse = ContentValues()
        valuse.put(MediaStore.Images.Media.TITLE, filename)
        valuse.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        val cameraFileUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, valuse)
        this.cameraFileUri = cameraFileUri

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileUri)
        startActivityForResult(intent, REQUEST_CODE_CAMERA)
    }

    private fun startLibrary() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_LIBRALY)
    }
}


