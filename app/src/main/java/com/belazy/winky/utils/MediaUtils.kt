//package com.belazy.winky.utils
//
//import android.content.ContentUris
//import android.content.Context
//import android.provider.MediaStore
//import com.belazy.winky.data.model.MediaFile
//
//object MediaUtils {
//
//    fun getAllImages(context: Context): List<MediaFile> {
//        val imageList = mutableListOf<MediaFile>()
//        val projection = arrayOf(
//            MediaStore.Images.Media._ID,
//            MediaStore.Images.Media.DISPLAY_NAME,
//            MediaStore.Images.Media.DATA,
//            MediaStore.Images.Media.DATE_ADDED,
//
//        )
//
//        val cursor = context.contentResolver.query(
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//            projection, null, null, "${MediaStore.Images.Media.DATE_ADDED} DESC"
//        )
//
//        cursor?.use {
//            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
//            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
//            val pathColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//            val dateColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
//
//            while (it.moveToNext()) {
//                val id = it.getLong(idColumn)
//                val name = it.getString(nameColumn)
//                val timestampSeconds = it.getLong(dateColumn)
//                val date = java.util.Date(timestampSeconds * 1000)
////                val path = it.getString(pathColumn)
//                val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
//
//                imageList.add(MediaFile(uri, false, name, date,))
//            }
//        }
//        return imageList
//    }
//
//    fun getAllVideos(context: Context): List<MediaFile> {
//        val videoList = mutableListOf<MediaFile>()
//        val projection = arrayOf(
//            MediaStore.Video.Media._ID,
//            MediaStore.Video.Media.DISPLAY_NAME,
//            MediaStore.Video.Media.DATA,
//            MediaStore.Video.Media.DATE_ADDED,
//
//        )
//
//        val cursor = context.contentResolver.query(
//            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//            projection, null, null, "${MediaStore.Video.Media.DATE_ADDED} DESC"
//        )
//
//        cursor?.use {
//            val idColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
//            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
//            val pathColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
//            val dateColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
//
//            while (it.moveToNext()) {
//                val id = it.getLong(idColumn)
//                val name = it.getString(nameColumn)
//                val timestampSeconds = it.getLong(dateColumn)
//                val date = java.util.Date(timestampSeconds * 1000)
//                val path = it.getString(pathColumn)
//                val uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
//
//                videoList.add(MediaFile(uri, true, name, date,))
//            }
//        }
//        return videoList
//    }
//}
