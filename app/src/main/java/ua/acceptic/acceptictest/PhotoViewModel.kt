package ua.acceptic.acceptictest

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData

class PhotoViewModel(application: Application) : AndroidViewModel(application) {
    private var photos: LiveData<List<Media>>? = null
    private var repository: PhotoRepository = PhotoRepository(application.applicationContext)

    fun getAllPhotos(): LiveData<List<Media>> {
        if (photos == null) {
            photos = repository.getAllPhotosPath()
        }
        return photos as LiveData<List<Media>>
    }
}
