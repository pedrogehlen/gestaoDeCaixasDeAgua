package repositorio

import pessoas.Cliente
import java.sql.PreparedStatement
import java.sql.ResultSet

class CRUDCliente : InterfaceJPA<Cliente>, ConexaoPostgres() {
    override fun salvar(item: Cliente) {
        var stmt: PreparedStatement? = null
        try {
            conectar()
            // Os valores entram nos ? do PreparedStatement, separados do SQL.
            stmt = c!!.prepareStatement("INSERT INTO cliente (nome, cpf, idade) VALUES (?, ?, ?)")
            stmt.setString(1, item.nome)
            stmt.setString(2, item.cpf)
            stmt.setInt(3, item.idade)
            check(stmt.executeUpdate() == 1) { "Registro inexistente." }
        } finally {
            stmt?.close()
            c?.close()
        }
    }

    override fun editar(item: Cliente, id: Int) {
        var stmt: PreparedStatement? = null
        try {
            conectar()
            stmt = c!!.prepareStatement("UPDATE cliente SET nome = ?, cpf = ?, idade = ? WHERE id = ?")
            stmt.setString(1, item.nome)
            stmt.setString(2, item.cpf)
            stmt.setInt(3, item.idade)
            stmt.setInt(4, id)
            check(stmt.executeUpdate() == 1) { "Registro inexistente." }
        } finally {
            stmt?.close()
            c?.close()
        }
    }

    override fun excluir(id: Int) {
        var stmt: PreparedStatement? = null
        try {
            conectar()
            stmt = c!!.prepareStatement("DELETE FROM cliente WHERE id = ?")
            stmt.setInt(1, id)
            check(stmt.executeUpdate() == 1) { "Registro inexistente." }
        } finally {
            stmt?.close()
            c?.close()
        }
    }

    fun listarTodos(): MutableList<Cliente> {
        val lista = mutableListOf<Cliente>()
        var stmt: PreparedStatement? = null
        var rs: ResultSet? = null
        try {
            conectar()
            stmt = c!!.prepareStatement("SELECT * FROM cliente ORDER BY id")
            rs = stmt.executeQuery()
            while (rs.next()) {
                lista.add(Cliente(rs.getString("nome"), rs.getString("cpf"), rs.getInt("idade"), rs.getInt("id")))
            }
            return lista
        } finally {
            rs?.close()
            stmt?.close()
            c?.close()
        }
    }

    // O retorno pode ser nulo quando o ID não existe.
    fun buscarPorId(id: Int): Cliente? = listarTodos().find { it.id == id }

    override fun listar() {
        val lista = listarTodos()
        if (lista.isEmpty()) println("Nenhum registro cadastrado.")
        lista.forEach { println("${it.id} - ${it.nome} | CPF: ${it.cpf} | Idade: ${it.idade}") }
    }
}
