package repositorio

import pessoas.*
import enumeradores.*
import java.sql.PreparedStatement
import java.sql.ResultSet

class CRUDFuncionario : InterfaceJPA<Funcionario>, ConexaoPostgres() {
    override fun salvar(item: Funcionario) {
        var stmt: PreparedStatement? = null
        try {
            conectar()
            stmt = c!!.prepareStatement("INSERT INTO funcionario (nome, cpf, idade, salario, turno, habilidade, setor) VALUES (?, ?, ?, ?, ?, ?, ?)")
            stmt.setString(1, item.nome)
            stmt.setString(2, item.cpf)
            stmt.setInt(3, item.idade)
            stmt.setBigDecimal(4, item.salario)
            stmt.setString(5, item.turno.name)
            stmt.setString(6, item.habilidade.name)
            stmt.setString(7, item.setor.name)
            check(stmt.executeUpdate() == 1) { "Registro inexistente." }
        } finally {
            stmt?.close()
            c?.close()
        }
    }

    override fun editar(item: Funcionario, id: Int) {
        var stmt: PreparedStatement? = null
        try {
            conectar()
            stmt = c!!.prepareStatement("UPDATE funcionario SET nome = ?, cpf = ?, idade = ?, salario = ?, turno = ?, habilidade = ?, setor = ? WHERE id = ?")
            stmt.setString(1, item.nome)
            stmt.setString(2, item.cpf)
            stmt.setInt(3, item.idade)
            stmt.setBigDecimal(4, item.salario)
            stmt.setString(5, item.turno.name)
            stmt.setString(6, item.habilidade.name)
            stmt.setString(7, item.setor.name)
            stmt.setInt(8, id)
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
            stmt = c!!.prepareStatement("DELETE FROM funcionario WHERE id = ?")
            stmt.setInt(1, id)
            check(stmt.executeUpdate() == 1) { "Registro inexistente." }
        } finally {
            stmt?.close()
            c?.close()
        }
    }

    fun listarTodos(): MutableList<Funcionario> {
        val lista = mutableListOf<Funcionario>()
        var stmt: PreparedStatement? = null
        var rs: ResultSet? = null
        try {
            conectar()
            stmt = c!!.prepareStatement("SELECT * FROM funcionario ORDER BY id")
            rs = stmt.executeQuery()
            while (rs.next()) {
                lista.add(if (rs.getString("setor") == "INSTALACAO" && rs.getString("habilidade") == "INSTALACAO")
                    Instalador(rs.getString("nome"), rs.getString("cpf"), rs.getInt("idade"), rs.getBigDecimal("salario"), Turno.valueOf(rs.getString("turno")), rs.getInt("id"))
                else Funcionario(rs.getString("nome"), rs.getString("cpf"), rs.getInt("idade"), rs.getBigDecimal("salario"), Turno.valueOf(rs.getString("turno")), Habilidade.valueOf(rs.getString("habilidade")), Setor.valueOf(rs.getString("setor")), rs.getInt("id")))
            }
            return lista
        } finally {
            rs?.close()
            stmt?.close()
            c?.close()
        }
    }

    // O retorno pode ser nulo quando o ID não existe.
    fun buscarPorId(id: Int): Funcionario? = listarTodos().find { it.id == id }

    override fun listar() {
        val lista = listarTodos()
        if (lista.isEmpty()) println("Nenhum registro cadastrado.")
        lista.forEach { println("${it.id} - ${it.nome} | CPF: ${it.cpf} | Idade: ${it.idade} | ${it.setor} | ${it.turno} | ${it.habilidade} | Salário: R$ ${it.salario}") }
    }
}
