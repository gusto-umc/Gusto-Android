package com.gst.gusto.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Point
import android.graphics.PointF
import android.location.Location
import android.util.Log
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationServices
import com.gst.gusto.R
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle
import com.kakao.vectormap.route.RouteLineStyles


class mapUtil {
    companion object {
        data class MarkerItem(
            val storeId: Long,
            var ordinal: Int,
            val routeListId: Long,
            var latitude: Double,
            var longitude: Double,
            val storeName: String,
            var address: String,
            var bookMark: Boolean
        )

        val MAPPERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val LOCATION_PERMISSION_REQUEST_CODE = 5000
        private val ROUTE_MARKER_IMAGES = arrayOf(
            R.drawable.route_marker_1_img,
            R.drawable.route_marker_2_img,
            R.drawable.route_marker_3_img,
            R.drawable.route_marker_4_img,
            R.drawable.route_marker_5_img,
            R.drawable.route_marker_6_img
        )

        @SuppressLint("MissingPermission")
        fun getCurrentLocation(context : Context, fragment: Fragment, activity: Activity,
                               callback: (Location) -> Unit) {
            if (!hasPermission(context)) {
                fragment.requestPermissions(
                    MAPPERMISSIONS,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            } else {
                val fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(activity)
                fusedLocationProviderClient.lastLocation
                    .addOnSuccessListener { success: Location? ->
                        success?.let { location ->
                            callback(location)
                        }
                    }
                    .addOnFailureListener { fail ->
                    }
            }

        }


        fun setMarker(kakaoMap: KakaoMap,markerList: ArrayList<MarkerItem>) {
            var labelManager = kakaoMap.labelManager
            if(labelManager!=null) {
                var styles = labelManager.addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.marker_color_small_img)))
                var layer = labelManager.layer
                if (layer != null) {
                    layer.removeAll()
                    for(data in markerList) {
                        val label = LabelOptions.from(LatLng.from(data.latitude,data.longitude))
                            .setStyles(styles);
                        label.tag = data.ordinal
                        label.clickable = true
                        layer.addLabel(label)
                    }
                }

            }
        }
        fun setRoute(kakaoMap: KakaoMap,markerList: ArrayList<MarkerItem>) {
            var labelManager = kakaoMap.labelManager
            val routeLineStyle = RouteLineStyles.from(RouteLineStyle.from(16f,Color.argb(255, 253, 105, 7)))
            val routeManager = kakaoMap.routeLineManager
            if(labelManager!=null&&routeManager!=null) {
                var layer = labelManager.layer
                var routLayer = routeManager.layer
                var latLngArray = ArrayList<LatLng>()
                if (layer != null) {
                    layer.removeAll()
                    for((index, data) in markerList.withIndex()) {
                        var labelStyle = LabelStyle.from(ROUTE_MARKER_IMAGES[index])
                        labelStyle.anchorPoint = PointF(0.5f,0.5f)
                        var styles = labelManager.addLabelStyles(LabelStyles.from(labelStyle))

                        val label = LabelOptions.from(LatLng.from(data.latitude,data.longitude))
                            .setStyles(styles);
                        label.tag = data.ordinal
                        label.clickable = true
                        layer.addLabel(label)
                        latLngArray.add(LatLng.from(data.latitude,data.longitude))
                    }
                }

                var routeLineSegment = RouteLineSegment.from(latLngArray).setStyles(routeLineStyle)
                routLayer.addRouteLine(RouteLineOptions.from(routeLineSegment))

                var cameraUpdate = CameraUpdateFactory.fitMapPoints(latLngArray.toArray(arrayOf<LatLng>()),300)
                kakaoMap.moveCamera(cameraUpdate, CameraAnimation.from(0, true, true))
            }
        }
        /*
        fun setStores(mapView : MapView,markerList: ArrayList<MarkerItem>) {
            mapView.removeAllPOIItems()
            for(data in markerList) {
                val marker = MapPOIItem()
                marker.itemName = data.ordinal.toString()
                marker.tag = data.ordinal // id
                marker.mapPoint = MapPoint.mapPointWithGeoCoord(data.latitude, data.longitude)
                marker.markerType = MapPOIItem.MarkerType.CustomImage
                marker.customImageResourceId = R.drawable.marker_color_small_img
                marker.isShowCalloutBalloonOnTouch = false
                marker.showAnimationType = MapPOIItem.ShowAnimationType.DropFromHeaven

                mapView.addPOIItem(marker)
            }
            if(!markerList.isEmpty())
                mapView.moveCamera(CameraUpdateFactory.newMapPoint(MapPoint.mapPointWithGeoCoord(markerList[0].latitude, markerList[0].longitude)))
        }*/
        fun hasPermission(context : Context): Boolean {
            for (permission in MAPPERMISSIONS) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
            return true
        }

    }

}