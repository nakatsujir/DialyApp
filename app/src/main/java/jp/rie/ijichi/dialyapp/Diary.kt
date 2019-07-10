package jp.rie.ijichi.dialyapp

import java.io.Serializable

class Diary (
    val title:String,
    val text:String,
    val day:String,
    val diaryId:String,
    bytes:ByteArray
):Serializable{
    val imageBytes:ByteArray

    init {
        imageBytes = bytes.clone()
    }
}