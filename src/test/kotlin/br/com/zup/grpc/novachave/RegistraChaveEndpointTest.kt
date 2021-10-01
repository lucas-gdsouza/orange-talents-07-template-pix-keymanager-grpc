package br.com.zup.grpc.novachave

import br.com.zup.domains.ChavePix
import br.com.zup.domains.ContaAssociada
import br.com.zup.integration.itau.ContasDeClientesNoItauClient
import br.com.zup.pix.*
import br.com.zup.repository.ChavePixRepository
import br.com.zup.response.DadosDaContaResponse
import br.com.zup.response.InstituicaoResponse
import br.com.zup.response.TitularResponse
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.util.*

@MicronautTest(transactional = false)
internal class RegistraChaveEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: KeyManagerRegistraGrpcServiceGrpc.KeyManagerRegistraGrpcServiceBlockingStub
) {

    @Inject
    lateinit var itauClient: ContasDeClientesNoItauClient

    companion object {
        val CLIENTE_ID = UUID.randomUUID()
    }

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun `deve registrar a nova chave`() {
        `when`(itauClient.buscaContaPorTipo(clienteId = CLIENTE_ID.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosDaContaResponse()))

        val response = grpcClient.registra(
            RegistraChavePixRequest.newBuilder()
                .setIdentificadorDoCliente(CLIENTE_ID.toString())
                .setTipoDeChave(TipoDeChave.EMAIL)
                .setChave("rponte@gmail.com")
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build()
        )

        with(response) {
            assertEquals(CLIENTE_ID.toString(), identificadorDoCliente)
            assertNotNull(identificadorPix)
        }

    }

    @Test
    fun `nao deve registrar a nova chave por ja existir uma de mesmo valor`() {
        repository.save(chave(tipo = br.com.zup.domains.enums.TipoDeChave.DOCUMENTO_CPF, chave = "63657520325", clienteId = CLIENTE_ID))

        val request = assertThrows<StatusRuntimeException> {
            grpcClient.registra(
                RegistraChavePixRequest.newBuilder()
                    .setIdentificadorDoCliente(CLIENTE_ID.toString())
                    .setTipoDeChave(TipoDeChave.DOCUMENTO_CPF)
                    .setChave("63657520325")
                    .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                    .build()
            )
        }

        with(request) {
            assertEquals(Status.ALREADY_EXISTS.code, this.status.code)
            assertEquals("A CHAVE JÁ ESTÁ CADASTRADA", this.status.description)
        }
    }

    @Test
    fun `nao deve registrar a nova chave por não encontrar dados da conta a ser associada`() {
        `when`(itauClient.buscaContaPorTipo(clienteId = CLIENTE_ID.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.notFound())

        val request = assertThrows<StatusRuntimeException> {
            grpcClient.registra(
                RegistraChavePixRequest.newBuilder()
                    .setIdentificadorDoCliente(CLIENTE_ID.toString())
                    .setTipoDeChave(TipoDeChave.DOCUMENTO_CPF)
                    .setChave("63657520325")
                    .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                    .build()
            )
        }

        with(request) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("Cliente não encontrado", status.description)
        }

    }

    
    @MockBean(ContasDeClientesNoItauClient::class)
    fun itauClient(): ContasDeClientesNoItauClient? {
        return Mockito.mock(ContasDeClientesNoItauClient::class.java)
    }

    private fun dadosDaContaResponse(): DadosDaContaResponse {
        return DadosDaContaResponse(
            tipo = "CONTA_CORRENTE",
            instituicao = InstituicaoResponse(
                "UNIBANCO ITAU SA", "ISPB_ITAU"
            ),
            agencia = "1218",
            numero = "291900",
            titular = TitularResponse("Rafael Ponte", "63657520325")
        )
    }

    private fun chave(
        tipo: br.com.zup.domains.enums.TipoDeChave,
        chave: String,
        clienteId: UUID = UUID.randomUUID(),
    ): ChavePix {
        return ChavePix(
            identificadorDoCliente = clienteId,
            tipoDeChave = tipo,
            chave = chave,
            tipoDeConta = br.com.zup.domains.enums.TipoDeConta.CONTA_CORRENTE,
            conta = ContaAssociada(
                instituicao = "UNIBANCO ITAU",
                nomeDoTitular = "Rafael Ponte",
                cpfDoTitular = "63657520325",
                agencia = "1218",
                numeroDaConta = "291900"
            )
        )
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerRegistraGrpcServiceGrpc.KeyManagerRegistraGrpcServiceBlockingStub? {
            return KeyManagerRegistraGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}