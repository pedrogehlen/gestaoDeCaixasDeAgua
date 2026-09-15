package pessoas

import enumeradores.Habilidade
import enumeradores.Turno
import java.math.BigDecimal

class Instalador (
    nome : String,
    cpf : String,
    idade : Int,
    val salario : BigDecimal,
    val turno : Turno,
    val habilidade : Habilidade
) : Pessoa(
    nome, cpf, idade
) {
    override fun receberConta(dinheiro : BigDecimal) : BigDecimal{
        return -dinheiro
    }//Uma função sobrescrita somente altera seu escopo
}