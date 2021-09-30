package br.com.zup.domains

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class ContaAssociada(
    @field:Column(nullable = false) val instituicao: String,
    @field:Column(nullable = false) val nomeDoTitular: String,
    @field:Column(nullable = false, updatable = false) val cpfDoTitular: String,
    @field:Column(nullable = false) val agencia: String,
    @field:Column(nullable = false) val numeroDaConta: String
)