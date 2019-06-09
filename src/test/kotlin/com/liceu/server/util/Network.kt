package com.liceu.server.util

import com.liceu.server.presentation.Response
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.getForObject
import org.springframework.boot.test.web.client.postForObject
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.exchange

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

fun <T>TestRestTemplate.danielRequest(headers: HttpHeaders, body: Map<String, Any>, method: HttpMethod, url: String): Response<T> {
    val headers = HttpHeaders()
    headers["API_KEY"] = "apikey"
    val entity = HttpEntity<Void>(headers)
    val response = restTemplate.exchange<HashMap<String,Any>>(url, HttpMethod.POST, entity)
    return Response(
//            data as T,
//            status as String,
//            errorCode as Int?
    )
}