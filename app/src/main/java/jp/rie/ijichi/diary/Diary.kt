package jp.rie.ijichi.diary

import java.io.Serializable

class Diary (
    val title:String,
    val text:String,
    val day:String,
    val diaryId:String,
    val name:String,
    val uid:String,
    bytes:ByteArray
):Serializable{
    val imageBytes:ByteArray

    init {
        imageBytes = bytes.clone()
    }
}