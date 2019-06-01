package com.liceu.server.util

import com.liceu.server.presentation.v1.Response
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForObject
import org.springframework.boot.test.web.client.postForObject

fun getListResponse(restTemplate: TestRestTemplate, url: String): Response<List<HashMap<String, Any>>> {
    val result = restTemplate.getForObject<HashMap<String, *>>(url)!!
    val status = result["status"]
    val errorCode = result["errorCode"]
    val data = result["data"]
    return Response(
            data as List<HashMap<String, Any>>?,
            status as String,
            errorCode as Int?
    )
}


fun postResponse(restTemplate: TestRestTemplate, url: String, req: HashMap<String, Any>): Response<HashMap<String, Any>> {
    val result = restTemplate.postForObject<HashMap<String, *>>(url, req)!!
    val status = result["status"]
    val errorCode = result["errorCode"]
    val data = result["data"]
    return Response(
            data as HashMap<String, Any>?,
            status as String,
            errorCode as Int?
    )
}