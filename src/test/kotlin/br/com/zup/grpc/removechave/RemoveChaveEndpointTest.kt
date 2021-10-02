package br.com.zup.grpc.removechave

import br.com.zup.domains.ChavePix
import br.com.zup.domains.ContaAssociada
import br.com.zup.domains.enums.TipoDeChave
import br.com.zup.grpc.novachave.RegistraChaveEndpointTest
import br.com.zup.integration.itau.ContasDeClientesNoItauClient
import br.com.zup.pix.KeyManagerRegistraGrpcServiceGrpc
import br.com.zup.pix.KeyManagerRemoveGrpcServiceGrpc
import br.com.zup.pix.RemoveChavePixRequest
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
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*

@MicronautTest(transactional = false)
internal class RemoveChaveEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceBlockingStub
) {

    lateinit var CHAVE_EXISTENTE: ChavePix

    @BeforeEach
    fun setUp() {
        CHAVE_EXISTENTE =
            repository.save(chave(tipo = br.com.zup.domains.enums.TipoDeChave.DOCUMENTO_CPF, chave = "63657520325",
                                    clienteId = RegistraChaveEndpointTest.CLIENTE_ID))
    }

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }

    @Test
    fun `deve remover a chave pix existente`(){
        val response = grpcClient.remove(
            RemoveChavePixRequest.newBuilder()
                .setIdentificadorDoCliente(CHAVE_EXISTENTE.identificadorDoCliente.toString())
                .setIdentificadorPix(CHAVE_EXISTENTE.chave.toString())
                .build()
        )

        assertEquals(CHAVE_EXISTENTE.chave.toString(), response.identificadorPix)
        assertEquals(CHAVE_EXISTENTE.identificadorDoCliente.toString(), response.identificadorDoCliente)
    }

    @Test
    fun `nao deve remover a chave pix que nao existe`() {
        val chavePixInexistente = UUID.randomUUID().toString()

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.remove(
                RemoveChavePixRequest.newBuilder()
                    .setIdentificadorDoCliente(CHAVE_EXISTENTE.identificadorDoCliente.toString())
                    .setIdentificadorPix(chavePixInexistente)
                    .build()
            )
        }

        with(thrown) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave Pix não encontrada", status.description)
        }
    }

    @Test
    fun `nao deve remover a chave pix de outro cliente`() {
        val simulacaoDeOutroCliente = UUID.randomUUID().toString()

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.remove(
                RemoveChavePixRequest.newBuilder()
                    .setIdentificadorDoCliente(simulacaoDeOutroCliente)
                    .setIdentificadorPix(CHAVE_EXISTENTE.chave.toString())
                    .build()
            )
        }

        with(thrown) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave Pix não encontrada", status.description)
        }
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
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceBlockingStub? {
            return KeyManagerRemoveGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}