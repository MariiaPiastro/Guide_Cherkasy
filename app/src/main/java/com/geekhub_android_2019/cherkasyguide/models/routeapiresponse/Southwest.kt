package com.geekhub_android_2019.cherkasyguide.models.routeapiresponse

import com.squareup.moshi.Json

data class Southwest(@Json(name = "lng")
                     val lng: Double?,
                     @Json(name = "lat")
                     val lat: Double?)
