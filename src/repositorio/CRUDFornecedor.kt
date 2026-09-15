package repositorio

import pessoas.Fornecedor
import java.sql.PreparedStatement
import java.sql.ResultSet

class CRUDFornecedor : InterfaceJPA<Fornecedor>, ConexaoPostgres() {
    override fun salvar(item: Fornecedor) {
        var stmt: PreparedStatement? = null
        try {
            conectar()
            stmt = c!!.prepareStatement("INSERT INTO fornecedor (razao_social, cnpj) VALUES (?, ?)")
            stmt.setString(1, item.razaoSocial)
            stmt.setString(2, item.cnpj)
            check(stmt.executeUpdate() == 1) { "Registro inexistente." }
        } finally {
            stmt?.close()
            c?.close()
        }
    }

    override fun editar(item: Fornecedor, id: Int) {
        var stmt: PreparedStatement? = null
        try {
            conectar()
            stmt = c!!.prepareStatement("UPDATE fornecedor SET razao_social = ?, cnpj = ? WHERE id = ?")
            stmt.setString(1, item.razaoSocial)
            stmt.setString(2, item.cnpj)
            stmt.setInt(3, id)
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
            stmt = c!!.prepareStatement("DELETE FROM fornecedor WHERE id = ?")
            stmt.setInt(1, id)
            check(stmt.executeUpdate() == 1) { "Registro inexistente." }
        } finally {
            stmt?.close()
            c?.close()
        }
    }

    fun listarTodos(): MutableList<Fornecedor> {
        val lista = mutableListOf<Fornecedor>()
        var stmt: PreparedStatement? = null
        var rs: ResultSet? = null
        try {
            conectar()
            stmt = c!!.prepareStatement("SELECT * FROM fornecedor ORDER BY id")
            rs = stmt.executeQuery()
            while (rs.next()) {
                lista.add(Fornecedor(rs.getString("razao_social"), rs.getString("cnpj"), rs.getInt("id")))
            }
            return lista
        } finally {
            rs?.close()
            stmt?.close()
            c?.close()
        }
    }

    // O retorno pode ser nulo quando o ID não existe.
    fun buscarPorId(id: Int): Fornecedor? = listarTodos().find { it.id == id }

    override fun listar() {
        val lista = listarTodos()
        if (lista.isEmpty()) println("Nenhum registro cadastrado.")
        lista.forEach { println("${it.id} - ${it.razaoSocial} | CNPJ: ${it.cnpj}") }
    }
}
