package sistema.pagamento

import repositorio.CRUDMovimentacao
import java.time.LocalDate
import java.time.LocalDateTime

fun pagar(){
    println("digite o contexto: ")
    val contexto = readln() //FAZER UM ENUM NO LUGAR DESSA VAL
    println("Digite um valor: ")
    val valor = readln().toBigDecimal()//precisa validar
    val data = LocalDate.now()

}