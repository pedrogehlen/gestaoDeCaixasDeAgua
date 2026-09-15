package pessoas

import java.math.BigDecimal

open class Pessoa (
    val nome: String,
    val cpf: String,
    val idade: Int
)//Criação da classe-mãe via Construtor
{
    open fun receberConta(dinheiro : BigDecimal) : BigDecimal{
        return dinheiro
    }
}