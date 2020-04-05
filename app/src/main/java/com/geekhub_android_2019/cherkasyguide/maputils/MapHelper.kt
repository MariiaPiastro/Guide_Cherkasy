package com.geekhub_android_2019.cherkasyguide.maputils

import android.content.Context
import android.graphics.BitmapFactory
import com.geekhub_android_2019.cherkasyguide.R
import com.geekhub_android_2019.cherkasyguide.models.routeapiresponse.DirectionResponse
import com.geekhub_android_2019.cherkasyguide.models.routeapiresponse.RoutesItem
import com.geekhub_android_2019.cherkasyguide.routeapi.DirectionsApiFactory
import com.geekhub_android_2019.cherkasyguide.routeapi.OnDrawRouteFailure
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.maps.android.PolyUtil
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.GridBasedAlgorithm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

object MapHelper {

    private lateinit var context: Context
    private lateinit var routePolyline: Polyline
    private var polylines: ArrayList<Polyline> = ArrayList()
    private lateinit var route: RoutesItem
    private val pinForRoute by lazy {
        BitmapFactory.decodeResource(
            context.resources,
            R.drawable.pin_route
        )
    }

    fun init(context: Context) {
        this.context = context
    }

    lateinit var drawRouteFailureCallback: OnDrawRouteFailure

    fun clearMap(googleMap: GoogleMap) = googleMap.clear()

    fun setUpCamera(googleMap: GoogleMap, cameraUpdate: CameraUpdate) {
        googleMap.moveCamera(cameraUpdate)
    }

    fun animateCamera(googleMap: GoogleMap, cameraUpdate: CameraUpdate) {
        googleMap.animateCamera(cameraUpdate)
    }

    fun updateCameraZoom(markerList: ArrayList<PlaceMarker>): CameraUpdate {
        return CameraUpdateFactory.newLatLngZoom(
            markerList[0].position,
            16.0f
        )
    }

    fun updateCameraBounds(markerList: ArrayList<PlaceMarker>): CameraUpdate {
        return CameraUpdateFactory.newLatLngBounds(
            Utils.setUpBounds(markerList), 150
        )
    }

    fun setUpClusterOfMarkers(
        googleMap: GoogleMap,
        markerList: ArrayList<PlaceMarker>,
        context: Context
    ): ClusterManager<PlaceMarker> {
        val manager = ClusterManager<PlaceMarker>(context, googleMap)
        manager.renderer = ClusterMarkerRenderer(context, googleMap, manager)
        manager.algorithm = GridBasedAlgorithm()
        manager.addItems(markerList)
        manager.cluster()
        return manager
    }

    fun setUpMarker(marker: PlaceMarker, number: String, googleMap: GoogleMap) {
        googleMap.addMarker(
            Utils.markerOptions(marker, number, pinForRoute)
        )
    }

    fun drawRoute(
        googleMap: GoogleMap,
        origin: String,
        destination: String,
        mode: String,
        waypoints: String
    ) {
        DirectionsApiFactory.getDirectionsApiService(origin, destination, mode, waypoints)
            .enqueue(object : Callback<DirectionResponse> {
                override fun onFailure(call: Call<DirectionResponse>, t: Throwable) {
                    drawRouteFailureCallback.drawRouteFailure()
                    t.printStackTrace()
                }

                override fun onResponse(
                    call: Call<DirectionResponse>,
                    response: Response<DirectionResponse>
                ) {
                    if (response.isSuccessful) {
                        route = response.body()?.routes?.get(0)!!
                        if (::routePolyline.isInitialized) {
                            routePolyline.remove()
                        }
                        drawPolyline(googleMap)
                    } else {
                        throw HttpException(response)
                    }
                }
            })
    }

    private fun drawPolyline(googleMap: GoogleMap) {
        val shape = route.overviewPolyline?.points
        routePolyline = addPolyline(
            googleMap,
            PolyUtil.decode(shape),
            16F,
            context.getColor(R.color.polylineColor)
        )
    }

    fun drawStepPolyline(googleMap: GoogleMap, count: Int) {
        val legs = route.legs
        val steps = legs!![count]?.steps
        val path = ArrayList<List<LatLng>>()
        steps?.forEach {
            val points = it?.polyline?.points
            path.add(PolyUtil.decode(points))
        }
        path.forEach {
            routePolyline = addPolyline(
                googleMap, it, 16F, context.getColor(R.color.colorSecondaryDark)
            )
            polylines.add(routePolyline)
        }
    }

    fun addPolyline(googleMap: GoogleMap, list: List<LatLng>, width: Float, color: Int): Polyline {
        return googleMap.addPolyline(
            Utils.polylineOptions(list, width, color)
        )
    }
}
