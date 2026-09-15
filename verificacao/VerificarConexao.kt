import repositorio.ConexaoPostgres
import java.sql.SQLException

fun main() {
    // Porta sem servidor: simula indisponibilidade sem parar o PostgreSQL real.
    val conexao = object : ConexaoPostgres(url = "jdbc:postgresql://localhost:1/indisponivel?connectTimeout=2") {}
    var falhou = false
    try {
        conexao.conectar()
    } catch (e: SQLException) {
        falhou = true
        check(conexao.c == null)
    }
    check(falhou) { "A conexão indisponível deveria falhar." }
    println("OK: conexão indisponível informa falha e não deixa conexão inválida.")
}
