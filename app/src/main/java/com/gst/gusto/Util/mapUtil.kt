package com.gst.gusto.Util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Handler
import android.os.SystemClock
import android.os.ext.SdkExtensions
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationServices
import com.gst.clock.Fragment.MapFragment
import com.gst.gusto.ListView.ViewModel.MapViewModel
import com.gst.gusto.R
import net.daum.mf.map.api.CameraUpdateFactory
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapPointBounds
import net.daum.mf.map.api.MapPolyline
import net.daum.mf.map.api.MapView
import kotlin.concurrent.thread

class mapUtil {
    companion object {
        data class MarkerItem (val id : Int, val num : Int, val latitude : Double, val longitude : Double)

        private val MAPPERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        private val LOCATION_PERMISSION_REQUEST_CODE = 5000
        private val ROUTE_MARKER_IMAGES = arrayOf(
            R.drawable.route_marker_1_img,
            R.drawable.route_marker_2_img,
            R.drawable.route_marker_3_img,
            R.drawable.route_marker_4_img
        )
        @SuppressLint("MissingPermission")
        fun setMapInit(mapView : MapView,kakaoMap : RelativeLayout,context : Context,activity : Activity,option : String)  {
            kakaoMap.addView(mapView)

            if (!hasPermission(context)) {
                ActivityCompat.requestPermissions(
                    activity,
                    MAPPERMISSIONS,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            } else {
                val fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(activity)

                fusedLocationProviderClient.lastLocation
                    .addOnSuccessListener { success: Location? ->
                        success?.let { location ->
                            val geocoder = Geocoder(context)
                            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                            if (addresses != null) {
                                val address = addresses[0]
                                val currentAddress = address.getAddressLine(0) // 필요에 따라 세부 정보를 더 가져올 수 있습니다

                                // currentAddress를 필요에 따라 사용하세요
                                Log.d("현재 주소: ", "$currentAddress")
                                Log.d("좌표","${location.latitude}, ${location.longitude}")
                                MapView.setMapTilePersistentCacheEnabled(true)  //다운로드한 지도를 캐시에 저장
                                if(option =="map") {
                                    mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(location.latitude, location.longitude), true)
                                    mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving)
                                    mapView.setShowCurrentLocationMarker(true)
                                } else if(option == "route"){

                                }
                                //mapView.setCurrentLocationEventListener()
                                //mapView.setCurrentLocationRadius(10)
                                //mapView.setCurrentLocationRadiusStrokeColor(Color.BLUE)
                            }
                        }
                    }
                    .addOnFailureListener { fail ->
                        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633), true)
                    }
            }
        }

        fun setMarker(mapView : MapView,markerList: ArrayList<MarkerItem>) {
            mapView.removeAllPOIItems()
            for(data in markerList) {
                val marker = MapPOIItem()
                marker.itemName = "Default Marker"
                marker.tag = data.id // id
                marker.mapPoint = MapPoint.mapPointWithGeoCoord(data.latitude, data.longitude)
                marker.markerType = MapPOIItem.MarkerType.CustomImage
                marker.customImageResourceId = R.drawable.marker_color_small_img
                marker.isShowCalloutBalloonOnTouch = false

                mapView.addPOIItem(marker)
            }
            mapView.setPOIItemEventListener(MarkerEventListener())
        }
        fun setRoute(mapView : MapView,markerList: ArrayList<MarkerItem>) {
            mapView.removeAllPOIItems()
            val route = MapPolyline()
            route.lineColor = Color.argb(255, 253, 105, 7)
            for(data in markerList) {
                route.addPoint(MapPoint.mapPointWithGeoCoord(data.latitude, data.longitude))
                val marker = MapPOIItem()
                marker.itemName = "Default Marker"
                marker.setCustomImageAnchor(0.5f,0.5f)
                marker.tag = data.id // id
                marker.mapPoint = MapPoint.mapPointWithGeoCoord(data.latitude, data.longitude)
                marker.markerType = MapPOIItem.MarkerType.CustomImage
                marker.customImageResourceId = ROUTE_MARKER_IMAGES[data.num]
                marker.isShowCalloutBalloonOnTouch = false
                mapView.addPOIItem(marker)
            }
            mapView.addPolyline(route)
            mapView.setPOIItemEventListener(MarkerEventListener())
            val mapPointBounds = MapPointBounds(route.mapPoints)
            mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds,300))
        }
        class MarkerEventListener(): MapView.POIItemEventListener {
            override fun onPOIItemSelected(mapView: MapView?, poiItem: MapPOIItem?) {
                // 마커 클릭 시 이벤트
            }

            override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?) {
                // 말풍선 클릭 시 (Deprecated)
                // 이 함수도 작동하지만 그냥 아래 있는 함수에 작성하자
            }

            override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?, buttonType: MapPOIItem.CalloutBalloonButtonType?) {
                // 말풍선 클릭 시
            }

            override fun onDraggablePOIItemMoved(mapView: MapView?, poiItem: MapPOIItem?, mapPoint: MapPoint?) {
                // 마커의 속성 중 isDraggable = true 일 때 마커를 이동시켰을 경우
            }
        }
        private fun hasPermission(context : Context): Boolean {
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