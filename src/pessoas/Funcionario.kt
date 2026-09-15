package pessoas

import enumeradores.*
import java.math.BigDecimal

open class Funcionario(
    nome: String, cpf: String, idade: Int,
    val salario: BigDecimal, val turno: Turno,
    val habilidade: Habilidade, val setor: Setor, val id: Int = 0
) : Pessoa(nome, cpf, idade) {
    init {
        require(validacao.validarCpf(cpf)) { "CPF deve conter 11 dígitos." }
        require(nome.isNotBlank() && nome.length <= 100 && idade >= 16) { "Funcionário precisa de nome e idade mínima de 16 anos." }
        require(salario >= BigDecimal.ZERO) { "Salário não pode ser negativo." }
    }

    // Pelo ponto de vista da empresa, pagar ao funcionário é uma saída.
    override fun receberConta(dinheiro: BigDecimal): BigDecimal = -dinheiro
}
