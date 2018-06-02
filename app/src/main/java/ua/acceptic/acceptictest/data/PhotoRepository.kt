package ua.acceptic.acceptictest

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.provider.MediaStore

class PhotoRepository(private var context : Context) {

    private var proj = arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Files.FileColumns.MEDIA_TYPE)

    private var selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
            + " OR "
            + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)

    private val uri = MediaStore.Files.getContentUri("external")

    fun getAllPhotosPath() : LiveData<List<Media>> {
        val cursor = context.contentResolver.query(uri, proj, selection, null, MediaStore.Files.FileColumns.DATE_ADDED)
        val allImagesPath = ArrayList<Media>()

        while (cursor.moveToNext()) {
            val mediaType: MediaType = when(cursor.getInt(cursor.getColumnIndexOrThrow(proj[1]))) {
                MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> MediaType.VIDEO
                MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> MediaType.IMAGE
                else -> MediaType.OTHER
            }

            val media = Media(cursor.getString(cursor.getColumnIndexOrThrow(proj[0])), mediaType)
            allImagesPath.add(media)
        }

        val allImagesPathLive = MutableLiveData<List<Media>>()
        allImagesPathLive.value = allImagesPath.reversed()
        return allImagesPathLive
    }

}