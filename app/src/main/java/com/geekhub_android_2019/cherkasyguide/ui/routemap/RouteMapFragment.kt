package com.geekhub_android_2019.cherkasyguide.ui.routemap

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.geekhub_android_2019.cherkasyguide.R
import com.geekhub_android_2019.cherkasyguide.models.Place
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import kotlinx.android.synthetic.main.fragment_map.map_view
import kotlinx.android.synthetic.main.fragment_map_route.*

class RouteMapFragment : Fragment(R.layout.fragment_map_route), OnMapReadyCallback,
    View.OnClickListener {

    private val args: RouteMapFragmentArgs by navArgs()
    private val routeViewModel: RouteViewModel by viewModels()

    var mCount = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        map_view.onCreate(savedInstanceState)

        map_view.getMapAsync(this)

        textView_end_point.visibility = GONE

        button_walking.setOnClickListener(this)
        button_car.setOnClickListener(this)
        button_start_route.setOnClickListener {
            routeViewModel.buttonStartClick(mCount, it)
            button_car.visibility = GONE
            button_walking.visibility = GONE
            textView_end_point.visibility = VISIBLE
            mCount++
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        routeViewModel.init(args.places)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        routeViewModel.createMap(googleMap)
        routeViewModel.typeOfRoute.observe(this, Observer {
            routeViewModel.drawRoute(it)
        })

        routeViewModel.statusDrawButton.observe(this, Observer {
            button_start_route.isEnabled = it
        })

        routeViewModel.lastRadioState.observe(this, Observer {
            listOf(button_car, button_walking).forEach {
                if (it.id == routeViewModel.lastRadioState.value) {
                    it.setIconTintResource(R.color.colorPrimary)
                } else {
                    it.setIconTintResource(R.color.black)
                }
            }
        })

        routeViewModel.endPlace.observe(this, Observer<Place> {
            textView_end_point.text = getString(R.string.move_to, it.name)
        })
    }

    override fun onResume() {
        map_view.onResume()
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        map_view.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        map_view.onLowMemory()
    }

    override fun onClick(view: View) {
        routeViewModel.selectTypeOfRoute(view)
    }
}
