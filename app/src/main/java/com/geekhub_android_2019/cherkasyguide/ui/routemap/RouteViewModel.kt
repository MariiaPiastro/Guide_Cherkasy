package com.geekhub_android_2019.cherkasyguide.ui.routemap

import android.view.View
import android.view.View.GONE
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.geekhub_android_2019.cherkasyguide.R
import com.geekhub_android_2019.cherkasyguide.maputils.MapHelper
import com.geekhub_android_2019.cherkasyguide.maputils.Utils
import com.geekhub_android_2019.cherkasyguide.models.Place
import com.geekhub_android_2019.cherkasyguide.models.Places
import com.geekhub_android_2019.cherkasyguide.routeapi.OnDrawRouteFailure
import com.google.android.gms.maps.GoogleMap

class RouteViewModel : ViewModel(), OnDrawRouteFailure {

    private lateinit var placesForRoute: Places

    private var _endPlace = MutableLiveData<Place>()
    val endPlace: LiveData<Place> = _endPlace

    private var _lastRadioState = MutableLiveData<Int> (R.id.button_car)
    val lastRadioState: LiveData<Int> = _lastRadioState

    private val _typeOfRoute = MutableLiveData<String>("driving")
    val typeOfRoute: LiveData<String> = _typeOfRoute

    private val _statusDrawButton = MutableLiveData<Boolean>(true)
    val statusDrawButton: LiveData<Boolean> = _statusDrawButton

    private lateinit var mMap: GoogleMap

    init {
        MapHelper.drawRouteFailureCallback = this
    }

    fun init(places: Places) {
        placesForRoute = places
    }

    fun createMap(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMapToolbarEnabled = false
        MapHelper.clearMap(mMap)
        val markersList = Utils.getMarkerList(placesForRoute)
        markersList.forEachIndexed { index, placeMarker ->
            MapHelper.setUpMarker(placeMarker, (index + 1).toString(), mMap)
        }
        val updateCamera = MapHelper.updateCameraBounds(markersList)
        MapHelper.setUpCamera(mMap, updateCamera)
    }

    fun drawRoute(mode: String) {
        val originLocation = placesForRoute[0].location
        val destanitionLocation = placesForRoute[placesForRoute.size - 1].location
        val origin =
            originLocation?.latitude.toString() + "," + originLocation?.longitude.toString()
        val destination =
            destanitionLocation?.latitude.toString() + "," + destanitionLocation?.longitude.toString()
        val waypoints = buildWaypoints()
        MapHelper.drawRoute(
            mMap,
            origin,
            destination,
            mode,
            waypoints
        )
    }

    override fun drawRouteFailure() {
        _statusDrawButton.value = false
    }

    private fun buildWaypoints(): String {
        val waypointsBuilder = StringBuilder("")
        if (placesForRoute.size > 2) {
            var startElement = 1
            val lastElement = placesForRoute.size - 1
            while (startElement < lastElement) {
                val waypointLocation = placesForRoute[startElement].location
                waypointsBuilder
                    .append(waypointLocation?.latitude)
                    .append(",")
                    .append(waypointLocation?.longitude)
                    .append("|")
                startElement++
            }
        }
        return waypointsBuilder.toString()
    }

    fun selectTypeOfRoute(view: View) {
        _lastRadioState.value = view.id
        when (view.id) {
            R.id.button_car -> {
                _typeOfRoute.value = "driving"
            }
            R.id.button_walking ->
                _typeOfRoute.value = "walking"
        }
    }

    fun buttonStartClick(count: Int, view: View) {
        if (count < placesForRoute.size - 1) {
            val startPoint = placesForRoute[count]
            val endPoint = placesForRoute[count + 1]
            _endPlace.value = endPoint
            val places = Places()
            places.add(startPoint)
            places.add(endPoint)
            val markers = Utils.getMarkerList(places)
            with(MapHelper) {
                clearMap(mMap)
                setUpMarker(markers[0], (count+1).toString(), mMap)
                setUpMarker(markers[1], (count+2).toString(), mMap)
                drawStepPolyline(mMap, count)
                animateCamera(mMap, updateCameraBounds(markers))
            }

            if (count == placesForRoute.size - 2) {
                view.visibility = GONE
            }
        }
    }
}
