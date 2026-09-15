package sistema

import java.sql.SQLException

fun mostrarErro(e: Exception) {
    if (e is SQLException) {
        val mensagem = when (e.sqlState) {
            "23505" -> "CPF, CNPJ ou associação já cadastrados."
            "23503" -> "Registro relacionado inexistente ou utilizado em outro cadastro."
            "23514", "22003", "22001" -> "Valor fora dos limites permitidos pelo banco."
            else -> "Não foi possível concluir a operação no banco: ${e.message}"
        }
        println(mensagem)
    } else if (e is ClassNotFoundException) {
        println("Adicione o driver PostgreSQL às bibliotecas do projeto.")
    } else {
        println(e.message ?: "Não foi possível concluir a operação.")
    }
}
