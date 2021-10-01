package br.com.zup.grpc.removechave

import br.com.zup.domains.ChavePix
import br.com.zup.repository.ChavePixRepository
import br.com.zup.shared.exceptions.ChavePixNaoEncontradaException
import io.micronaut.validation.Validated
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.*
import javax.transaction.Transactional
import javax.validation.constraints.NotBlank

@Validated
@Singleton
class RemoveChavePixService(@Inject val repository: ChavePixRepository) {

    @Transactional
    fun remove(
        @NotBlank identificadorDoCliente: String?, //Validar UUID
        @NotBlank identificadorPix: String? //Validar UUID
    ) {

        val identificadorDoCliente = UUID.fromString(identificadorDoCliente)

        val chave : ChavePix = repository.findByIdentificadorDoClienteAndChave(identificadorDoCliente, identificadorPix)
            .orElseThrow {ChavePixNaoEncontradaException("Chave Pix não encontrada")}

        repository.delete(chave)
    }
}
