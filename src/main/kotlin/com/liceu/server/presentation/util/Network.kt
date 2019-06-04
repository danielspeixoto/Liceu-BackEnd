package com.liceu.server.presentation.util

import javax.servlet.http.HttpServletRequest

fun networkData(request: HttpServletRequest): HashMap<String, Any> {
    return hashMapOf(
            "ip" to request.remoteAddr
//    TODO get location
    )
}