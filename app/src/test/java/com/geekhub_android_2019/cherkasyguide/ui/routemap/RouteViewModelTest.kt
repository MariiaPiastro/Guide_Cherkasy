package com.geekhub_android_2019.cherkasyguide.ui.routemap

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.geekhub_android_2019.cherkasyguide.R
import com.geekhub_android_2019.cherkasyguide.getOrAwaitValue
import com.geekhub_android_2019.cherkasyguide.models.Place
import com.geekhub_android_2019.cherkasyguide.models.Places
import com.google.firebase.firestore.GeoPoint
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RouteViewModelTest {

    private lateinit var routeViewModel: RouteViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUpViewModel(){
        routeViewModel = RouteViewModel()
    }

    @Test
    fun selectTypeOfRoute_setDriving() {
        routeViewModel.selectTypeOfRoute(R.id.button_car)

        assertThat(routeViewModel.typeOfRoute.getOrAwaitValue(), `is`("driving"))
        assertThat(routeViewModel.lastRadioState.getOrAwaitValue(), `is`(R.id.button_car))
    }

    @Test
    fun selectTypeOfRoute_setWalking() {
        routeViewModel.selectTypeOfRoute(R.id.button_walking)

        assertThat(routeViewModel.typeOfRoute.getOrAwaitValue(), `is`("walking"))
        assertThat(routeViewModel.lastRadioState.getOrAwaitValue(), `is`(R.id.button_walking))
    }

    @Test
    fun buildWaypoints_returnEmptyString() {
        val placeForRoute = Places()
        placeForRoute.add(
            Place(
                "G4pSExkXa0j4C5x9YM65",
                "Дворец культуры «Дружба народов»",
                GeoPoint(49.437697, 32.072554),
                "",
                "",
                emptyList()
            )
        )
        placeForRoute.add(
            Place(
                "GlYgSRZH826NDIpjKSFT",
                "Парк Победы",
                GeoPoint(49.415137, 32.025642),
                "",
                "",
                emptyList()
            )
        )
        routeViewModel.init(placeForRoute)
        assertThat(routeViewModel.buildWaypoints(), `is`(""))
    }

    @Test
    fun buildWaypoints_returnWaypoints() {
        val placeForRoute = Places()
        placeForRoute.add(
            Place(
                "G4pSExkXa0j4C5x9YM65",
                "Дворец культуры «Дружба народов»",
                GeoPoint(49.437697, 32.072554),
                "",
                "",
                emptyList()
            )
        )
        placeForRoute.add(
            Place(
                "GlYgSRZH826NDIpjKSFT",
                "Парк Победы",
                GeoPoint(49.415137, 32.025642),
                "",
                "",
                emptyList()
            )
        )
        placeForRoute.add(
            Place(
                "QzBpM922P8SmRmhlytMV",
                "Парк Сосновый Бор",
                GeoPoint(49.4649963, 32.028101),
                "",
                "",
                emptyList()
            )
        )
        routeViewModel.init(placeForRoute)
        assertThat(routeViewModel.buildWaypoints(), `is`("49.415137,32.025642|"))
    }
}
