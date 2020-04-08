package com.geekhub_android_2019.cherkasyguide.ui.placemap

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.geekhub_android_2019.cherkasyguide.R
import com.geekhub_android_2019.cherkasyguide.maputils.MapHelper
import com.geekhub_android_2019.cherkasyguide.maputils.PlaceMarker
import com.geekhub_android_2019.cherkasyguide.maputils.Utils
import com.geekhub_android_2019.cherkasyguide.models.Places
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.clustering.ClusterManager
import kotlin.math.log

class PlaceViewModel : ViewModel() {

    private lateinit var _places: Places
    private lateinit var clusterManager: ClusterManager<PlaceMarker>
    private lateinit var mMap: GoogleMap

    fun init(places: Places) {
        _places = places
    }

    fun createMap(googleMap: GoogleMap, context: Context) {
        mMap = googleMap
        mMap.uiSettings.isMapToolbarEnabled = false
        mMap.uiSettings.isZoomControlsEnabled = true
        MapHelper.clearMap(googleMap)
        val markersList = Utils.getMarkerList(_places)
        clusterManager = MapHelper.setUpClusterOfMarkers(mMap, markersList, context)
        val updateCamera =
            if (markersList.toList().size == 1) {
                MapHelper.updateCameraZoom(markersList)
            } else {
                MapHelper.updateCameraBounds(markersList)
            }
        MapHelper.setUpCamera(mMap, updateCamera)
    }

    fun onInfoWindowClick(navController: NavController) {
        clusterManager.setOnClusterItemInfoWindowClickListener { placeMarker ->
            _places.forEach { place ->
                if (place.name == placeMarker.title) {
                    PlaceMapFragmentDirections.actionToPlaceDetailFragment(
                        place,
                        place.name
                    ).also {
                        navController.navigate(it)
                    }
                }
            }
        }
    }
}
