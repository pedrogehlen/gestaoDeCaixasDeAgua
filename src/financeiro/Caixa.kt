package financeiro

import java.math.BigDecimal

class Caixa (

    val saldo : BigDecimal //Não mexer via codigo
    //Somente no banco

){

    fun receita(valor : BigDecimal) : BigDecimal{
        return valor

    }

    fun despesa(valor : BigDecimal) : BigDecimal{
        return valor.multiply("-1".toBigDecimal())

    }


}