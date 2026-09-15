package validacao

import java.math.BigDecimal
import java.time.LocalDate

fun validarCpf(cpf: String): Boolean = Regex("^\\d{11}$").matches(cpf)
fun validarCnpj(cnpj: String): Boolean = Regex("^\\d{14}$").matches(cnpj)

fun lerTexto(mensagem: String, limite: Int = 100): String {
    while (true) {
        println(mensagem)
        val texto = readln().trim()
        require(texto != ":cancelar") { "Operação cancelada." }
        if (texto.isNotEmpty() && texto.length <= limite) return texto
        println("Preencha com 1 a $limite caracteres.")
    }
}

fun lerInteiro(mensagem: String, minimo: Int = 1, maximo: Int = Int.MAX_VALUE): Int {
    while (true) {
        val numero: Int? = lerTexto(mensagem).toIntOrNull()
        if (numero != null && numero in minimo..maximo) return numero
        println("Informe um inteiro entre $minimo e $maximo.")
    }
}

fun lerDinheiro(mensagem: String, permitirZero: Boolean = false): BigDecimal {
    while (true) {
        val texto = lerTexto(mensagem)
        try {
            val valor = texto.replace(',', '.').toBigDecimal().setScale(2, java.math.RoundingMode.UNNECESSARY)
            if (valor >= BigDecimal.ZERO && (permitirZero || valor > BigDecimal.ZERO) && valor <= "99999999.99".toBigDecimal()) return valor
        } catch (e: ArithmeticException) {
            println("Use no máximo duas casas decimais.")
        } catch (e: NumberFormatException) {
            println("Formato monetário inválido.")
        }
        println("Informe um valor válido, sem sinal negativo.")
    }
}

fun lerDimensao(mensagem: String): Double {
    while (true) {
        val valor = lerTexto(mensagem).replace(',', '.').toDoubleOrNull()
        if (valor != null && valor.isFinite() && valor > 0) return valor
        println("Informe uma dimensão positiva.")
    }
}

fun lerData(mensagem: String): LocalDate {
    while (true) {
        val texto = lerTexto(mensagem)
        try {
            val data = LocalDate.parse(texto)
            if (!data.isBefore(LocalDate.now())) return data
        } catch (e: java.time.format.DateTimeParseException) {
            println("Formato inválido.")
        }
        println("Use AAAA-MM-DD e uma data a partir de hoje.")
    }
}
