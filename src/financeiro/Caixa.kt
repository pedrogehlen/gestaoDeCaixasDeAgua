package financeiro

import java.math.BigDecimal

class Caixa {
    fun receita(valor: BigDecimal): BigDecimal {
        require(valor > BigDecimal.ZERO) { "Informe um valor positivo." }
        return valor
    }

    fun despesa(valor: BigDecimal): BigDecimal {
        require(valor > BigDecimal.ZERO) { "Informe um valor positivo." }
        return valor.multiply("-1".toBigDecimal())
    }
}
