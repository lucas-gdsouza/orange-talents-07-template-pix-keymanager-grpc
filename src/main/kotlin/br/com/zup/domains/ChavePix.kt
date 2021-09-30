package br.com.zup.domains

import java.util.*
import javax.persistence.*
import javax.validation.constraints.Size

@Entity
class ChavePix(
    @field:Column(nullable = false) val identificadorDoCliente: UUID?,
    @field:Column(nullable = false) @Enumerated(EnumType.STRING) val tipoDeConta: br.com.zup.domains.enums.TipoDeConta,
    @field:Column(nullable = false) @Enumerated(EnumType.STRING) val tipoDeChave: br.com.zup.domains.enums.TipoDeChave,
    @field:Column(nullable = false) @field:Size(max = 77) val chave: String?,
    @field:Column(nullable = false) @Embedded val conta: ContaAssociada
) {
    @Id
    @GeneratedValue
    var id: Long? = null
}