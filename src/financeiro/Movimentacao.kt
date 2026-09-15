package financeiro

import java.math.BigDecimal
import java.time.LocalDate

class Movimentacao(
    val valor : BigDecimal,
    val dataMovimentacao : LocalDate,
    val contexto : String,
    //val pessoa : Pessoa //Precisa fazer depois
) {
    fun movimentar(valor : BigDecimal, data: LocalDate){
        //Salvar no banco
    }


}