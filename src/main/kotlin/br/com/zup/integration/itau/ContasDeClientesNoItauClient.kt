package br.com.zup.integration.itau

import br.com.zup.response.DadosDaContaResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client(value = "http://\${values.erp-itau.host}:\${values.erp-itau.port}")
interface ContasDeClientesNoItauClient {
    @Get("/api/v1/clientes/{clienteId}/contas{?tipo}")
    fun buscaContaPorTipo(@PathVariable clienteId: String, @QueryValue tipo: String): HttpResponse<DadosDaContaResponse>
}