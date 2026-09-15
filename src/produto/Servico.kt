package produto

import enumeradores.StatusServico
import java.math.BigDecimal
import java.time.LocalDate

class Servico(
    val clienteId: Int, val caixaId: Int, val dataInstalacao: LocalDate,
    val preco: BigDecimal, val status: StatusServico = StatusServico.AGENDADO, val id: Int = 0,
    val funcionarios: MutableList<Int> = mutableListOf()
) {
    init {
        require(clienteId > 0 && caixaId > 0)
        require(preco > BigDecimal.ZERO) { "O serviço deve ter preço positivo." }
    }
}
