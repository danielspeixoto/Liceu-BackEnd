package com.liceu.server.util

import com.maxmind.geoip2.DatabaseReader
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import java.io.File
import java.net.InetAddress

@Component
class GeoLocator {

    val dbReader = DatabaseReader.Builder(ClassPathResource("GeoLite2-City.mmdb").file).build()

    fun location(ip: String): GeoLocation {
        val inet = InetAddress.getByName(ip)
        val response = dbReader.city(inet)

        val city = response.city.name
        val latitude = response.location.latitude
        val longitude = response.location.longitude

        return GeoLocation(
                city, latitude, longitude
        )
    }
}

data class GeoLocation(
        val city: String,
        val latitude: Double,
        val longitude: Double
)