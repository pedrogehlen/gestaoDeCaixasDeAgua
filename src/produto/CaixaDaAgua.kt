package produto

import enumeradores.Cor
import enumeradores.Material
import java.math.BigDecimal

class CaixaDaAgua(
    val marca: String, val modelo: String, val dimensao: MutableList<Double>,
    val cor: Cor, val material: Material, val formato: String, var preco: BigDecimal,
    val id: Int = 0, quantidadeEstoque: Int = 0, val fornecedorId: Int? = null
) {
    // Só os métodos abaixo alteram o estoque em memória.
    var quantidadeEstoque: Int = quantidadeEstoque
        private set

    init {
        require(marca.isNotBlank() && modelo.isNotBlank() && formato.isNotBlank())
        require(dimensao.size == 3 && dimensao.all { it.isFinite() && it > 0 }) { "Informe três dimensões positivas." }
        require(preco >= BigDecimal.ZERO && quantidadeEstoque >= 0)
    }

    fun adicionarEstoque(quantidade: Int) {
        require(quantidade > 0 && quantidade <= Int.MAX_VALUE - quantidadeEstoque) { "Quantidade inválida." }
        quantidadeEstoque += quantidade
    }

    fun removerEstoque(quantidade: Int): Boolean {
        if (quantidade <= 0 || quantidade > quantidadeEstoque) return false
        quantidadeEstoque -= quantidade
        return true
    }
}
