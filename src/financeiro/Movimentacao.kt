package financeiro

import enumeradores.TipoMovimentacao
import java.math.BigDecimal
import java.time.LocalDateTime

class Movimentacao(
    val valor: BigDecimal, val pagador: String, val recebedor: String, val descricao: String,
    val tipo: TipoMovimentacao, val responsavelId: Int,
    val dataHora: LocalDateTime = LocalDateTime.now(), val id: Int = 0
) {
    init {
        require(valor.compareTo(BigDecimal.ZERO) != 0) { "Movimentação não pode ter valor zero." }
        require(pagador.isNotBlank() && recebedor.isNotBlank() && descricao.isNotBlank())
        require(pagador.length <= 100 && recebedor.length <= 100 && descricao.length <= 200)
        require(responsavelId > 0)
        val entrada = tipo == TipoMovimentacao.VENDA_PRODUTO || tipo == TipoMovimentacao.VENDA_SERVICO
        require((valor > BigDecimal.ZERO) == entrada) { "Sinal incompatível com o tipo da movimentação." }
    }
}
