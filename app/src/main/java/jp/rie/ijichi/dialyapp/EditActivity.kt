package jp.rie.ijichi.dialyapp

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_edit.*
import java.io.ByteArrayOutputStream

class EditActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabaseReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        mDatabaseReference = FirebaseDatabase.getInstance().reference

        mAuth = FirebaseAuth.getInstance()


        edit_done_button.setOnClickListener { view ->
            diaryRegister()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_edit_delete -> deleteDialog()

        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteDialog() {
        AlertDialog.Builder(this)
            .setTitle("この記録を削除しますか？")
            .setPositiveButton("OK") { dialog, which ->

            }
            .setNegativeButton("CANCEL"){dialog, which ->

            }
            .show()
    }

    private fun diaryRegister(){
            val diaryRef = mDatabaseReference.child(DiaryPATH)
            val data = HashMap<String,String>()
            val title = edit_title_edit.text.toString()
            val text = edit_text_edit.text.toString()
            val image = edit_image.drawable as? BitmapDrawable

            if (image != null){
                val bitmap = image.bitmap
                val byte = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,byte)
                val bitmapString = Base64.encodeToString(byte.toByteArray(),Base64.DEFAULT)
                data["image"] = bitmapString

            }
            if (title.isEmpty()){
                Toast.makeText(this,"タイトルを入力してください",Toast.LENGTH_SHORT).show()
            }
            if (text.isEmpty()){
                Toast.makeText(this,"本文を入力してください",Toast.LENGTH_SHORT).show()
            }
            data["title"] = title
            data["text"] = text

        diaryRef.push().setValue(data,this)


    }


}
