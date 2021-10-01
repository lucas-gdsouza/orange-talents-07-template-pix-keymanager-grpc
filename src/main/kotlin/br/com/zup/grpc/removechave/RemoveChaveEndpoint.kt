package br.com.zup.grpc.removechave

import br.com.zup.pix.*
import br.com.zup.shared.handlers.ErrorHandler
import io.grpc.stub.StreamObserver
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
@ErrorHandler
class RemoveChaveEndpoint(@Inject private val service: RemoveChavePixService) :
    KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceImplBase() {

    override fun remove(request: RemoveChavePixRequest?, responseObserver: StreamObserver<RemoveChavePixResponse>?) {

        service.remove(
            identificadorDoCliente = request!!.identificadorDoCliente,
            identificadorPix = request!!.identificadorPix
        )

        responseObserver!!.onNext(
            RemoveChavePixResponse.newBuilder().setIdentificadorDoCliente(request!!.identificadorDoCliente.toString())
                .setIdentificadorPix(request!!.identificadorPix).build()
        )
        responseObserver!!.onCompleted()
    }
}