package com.shubham.emergencyapplication.Ui.Fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shubham.emergencyapplication.Models.FamilyLocation

class MapViewModel : ViewModel() {
    private val _locations = MutableLiveData<List<FamilyLocation>>()
    val locations: LiveData<List<FamilyLocation>> get() = _locations

    fun setLocations(newLocations: List<FamilyLocation>) {
        _locations.value = newLocations
    }

    fun updateLocation(updatedLocation: FamilyLocation) {
        _locations.value = _locations.value?.map {
            if (it.name == updatedLocation.name) updatedLocation else it
        }
    }
}
