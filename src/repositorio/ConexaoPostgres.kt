package repositorio

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

abstract class ConexaoPostgres (
    val user : String = "postgres",
    val senha : String = "postgres",
    val url : String ="jdbc:postgresql://localhost:5432/caixaDaAgua",
    var c : Connection? = null) {

    fun conectar() {
        try {
            //carregar o drive
            Class.forName("org.postgresql.Driver")

            // estabelecer conexão
            c = DriverManager.getConnection(url, user, senha)
            println("A conexão foi estabelecida!")

        } catch (e: SQLException) {
            println("Cara não deu boa: ${e.printStackTrace()}")

        }
    }
}

