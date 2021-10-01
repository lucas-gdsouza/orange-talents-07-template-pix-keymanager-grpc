package br.com.zup.grpc.novachave

import br.com.zup.domains.ChavePix
import br.com.zup.integration.itau.ContasDeClientesNoItauClient
import br.com.zup.repository.ChavePixRepository
import br.com.zup.shared.exceptions.ChavePixExistenteException
import br.com.zup.shared.exceptions.ClienteNaoExistenteException
import io.micronaut.validation.Validated
import jakarta.inject.Inject
import jakarta.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid


@Singleton
@Validated
class NovaChavePixService(@Inject val repository: ChavePixRepository,
                          @Inject val itauClient: ContasDeClientesNoItauClient) {

    @Transactional
    fun registra(@Valid novaChave : NovaChavePix) : ChavePix {

        if(repository.existsByChave(novaChave.chave)){
            throw ChavePixExistenteException("A CHAVE JÁ ESTÁ CADASTRADA")
        }

        val response = itauClient.buscaContaPorTipo(novaChave.identificadorDoCliente!!, novaChave.tipoDeConta!!.name)
        val conta = response.body()?.toModel() ?: throw ClienteNaoExistenteException("Cliente não encontrado")

        val chave = novaChave.toModel(conta)
        repository.save(chave)

        return chave
    }
}