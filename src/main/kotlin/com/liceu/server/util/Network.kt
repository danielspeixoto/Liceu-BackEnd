package com.liceu.server.util

import com.liceu.server.domain.global.LOCATION
import com.liceu.server.domain.global.LOG
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

@Component
class NetworkUtils {

    @Autowired
    lateinit var locator: GeoLocator

    fun networkData(request: HttpServletRequest): Map<String, Any> {
        var data = mapOf<String, Any>(
            "ip" to request.remoteAddr
        )
        try {
            val geoInfo = locator.location(request.remoteAddr)
            data += hashMapOf(
                    "latitude" to geoInfo.latitude,
                    "longitude" to geoInfo.longitude,
                    "city" to geoInfo.city
            )
        } catch (e: Exception) {
            Logging.error("geo_location", listOf(LOCATION, LOG), e, data)
        }
        return data
    }
}
