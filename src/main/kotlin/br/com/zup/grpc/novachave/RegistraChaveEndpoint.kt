package br.com.zup.grpc.novachave

import br.com.zup.pix.*
import io.grpc.stub.StreamObserver
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class RegistraChaveEndpoint(@Inject private val service: NovaChavePixService) :
    KeyManagerRegistraGrpcServiceGrpc.KeyManagerRegistraGrpcServiceImplBase() {

    override fun registra(
        request: RegistraChavePixRequest?,
        responseObserver: StreamObserver<RegistraChavePixResponse>?
    ) {
        val novaChave = request?.toModel()
        val chaveCriada = service.registra(novaChave!!)

        responseObserver!!.onNext(RegistraChavePixResponse.newBuilder().
                            setIdentificadorDoCliente(chaveCriada.identificadorDoCliente.toString()).
                            setIdentificadorPix(chaveCriada.chave).build())
        responseObserver!!.onCompleted()
    }

    /*Extension Function*/
    fun RegistraChavePixRequest.toModel(): NovaChavePix {
        return NovaChavePix(
            identificadorDoCliente = identificadorDoCliente,
            tipoDeChave = when (tipoDeChave) {
                TipoDeChave.CHAVE_DESCONHECIDA -> null
                else -> br.com.zup.domains.enums.TipoDeChave.valueOf(tipoDeChave.name)
            },
            chave = this.chave,
            tipoDeConta = when (tipoDeConta) {
                TipoDeConta.CONTA_DESCONHECIDA -> null
                else -> br.com.zup.domains.enums.TipoDeConta.valueOf(tipoDeConta.name)
            }
        )
    }
}