package br.com.zup.repository

import br.com.zup.domains.ChavePix
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository : JpaRepository<ChavePix, Long> {
    fun existsByChave(chave: String?): Boolean
    fun findByIdentificadorDoClienteAndChave(identificadorDoCliente: UUID?, identificadorPix: String?) : Optional<ChavePix>
}