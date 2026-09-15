package pessoas

import java.math.BigDecimal

class Cliente(
    nomeCliente: String,
    cpfCliente: String,
    idadeCliente: Int,
    val dividasAbertas: Boolean,
    val parcelasAPagar : MutableList<BigDecimal>
) : Pessoa(
    nome = nomeCliente,
    cpf = cpfCliente,
    idade = idadeCliente){
}