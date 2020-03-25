package com.geekhub_android_2019.cherkasyguide.ui.routeslist.models

import androidx.lifecycle.*
import androidx.navigation.NavController
import com.geekhub_android_2019.cherkasyguide.data.*
import com.geekhub_android_2019.cherkasyguide.models.Route
import kotlinx.coroutines.flow.combine
import com.geekhub_android_2019.cherkasyguide.R
import com.geekhub_android_2019.cherkasyguide.models.Places
import com.geekhub_android_2019.cherkasyguide.ui.routeslist.RouteListFragmentDirections

class RoutesViewModel : ViewModel() {

    private val repo = Repository()

    val routes: LiveData<List<RouteItem>> =
        repo.getRoutes().combine(repo.getUserRouteOrNUll()) { routes, userRoute ->
            val regularRoutes = listOf(RouteItem.Separator(R.string.regular_routes)) +
                    routes.map(RouteItem::Regular)

            val userRoutes = listOf(
                RouteItem.Separator(R.string.user_route),
                userRoute?.let(RouteItem::User) ?: RouteItem.CreateNew
            )

            regularRoutes + userRoutes
        }.asLiveData()

    fun createNewRoute(navController: NavController) {
        RouteListFragmentDirections.actionRoutesFragmentToCreateRouteFragment().also {
            navController.navigate(it)
        }
    }

    fun viewRoute(navController: NavController, route: Route) {
        RouteListFragmentDirections.actionRoutesFragmentToRouteFragment(route, route.name).also {
            navController.navigate(it)
        }
    }

    fun viewRouteMap(navController: NavController, route: Route) {
        val places = Places().apply { addAll(route.places) }
        RouteListFragmentDirections.actionToRouteMap(places).also {
            navController.navigate(it)
        }
    }
}