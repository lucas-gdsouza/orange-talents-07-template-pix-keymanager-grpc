package br.com.zup.grpc.novachave

import br.com.zup.domains.ChavePix
import br.com.zup.domains.ContaAssociada
import br.com.zup.domains.enums.TipoDeChave
import br.com.zup.domains.enums.TipoDeConta
import br.com.zup.shared.validations.ValidPixKey
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidPixKey
@Introspected
data class NovaChavePix(

    @field:NotBlank
    val identificadorDoCliente: String,

    @field:NotNull
    val tipoDeConta: br.com.zup.domains.enums.TipoDeConta?,

    @field:NotNull
    val tipoDeChave: br.com.zup.domains.enums.TipoDeChave?,

    @field:Size(max = 77)
    val chave: String
) {
    fun toModel(conta: ContaAssociada): ChavePix {
        return ChavePix(
            identificadorDoCliente = UUID.fromString(this.identificadorDoCliente),
            tipoDeChave = TipoDeChave.valueOf(this.tipoDeChave!!.name),
            chave = if (this.tipoDeChave == TipoDeChave.CHAVE_ALEATORIA) UUID.randomUUID().toString() else this.chave,
            tipoDeConta = TipoDeConta.valueOf(this.tipoDeConta!!.name),
            conta = conta
        )
    }
}