package com.es.aplicacion.service

import com.es.aplicacion.model.DatosMunicipios
import com.es.aplicacion.model.DatosProvincias
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class ExternalApiService(private val webClient: WebClient.Builder) {
    @Value("\${API_KEY}")
    private lateinit var apiKey: String

    fun obtenerDatosProvincias(): DatosProvincias? {
        return webClient.build()
            .get()
            .uri("https://apiv1.geoapi.es/provincias?type=JSON&key=$apiKey")
            .retrieve()
            .bodyToMono(DatosProvincias::class.java)
            .block()
    }

    @Value("\${API_KEY}")
    fun obtenerDatosMunicipios(): DatosMunicipios? {
        return webClient.build()
            .get()
            .uri("https://apiv1.geoapi.es/municipios?type=JSON&key=$apiKey")
            .retrieve()
            .bodyToMono(DatosMunicipios::class.java)
            .block()
    }
}
