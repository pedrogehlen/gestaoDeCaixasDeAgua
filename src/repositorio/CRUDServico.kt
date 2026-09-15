package repositorio

import produto.Servico
import enumeradores.StatusServico
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Date

class CRUDServico : InterfaceJPA<Servico>, ConexaoPostgres() {
    override fun salvar(item: Servico) {
        require(item.funcionarios.isNotEmpty()) { "Associe pelo menos um funcionário." }
        require(item.status == StatusServico.AGENDADO)
        var stmt: PreparedStatement? = null
        var equipe: PreparedStatement? = null
        var rs: ResultSet? = null
        try {
            conectar()
            c!!.autoCommit = false
            stmt = c!!.prepareStatement("INSERT INTO servico (cliente_id, caixa_id, data_instalacao, preco, status) VALUES (?, ?, ?, ?, ?) RETURNING id")
            stmt.setInt(1, item.clienteId)
            stmt.setInt(2, item.caixaId)
            stmt.setDate(3, Date.valueOf(item.dataInstalacao))
            stmt.setBigDecimal(4, item.preco)
            stmt.setString(5, item.status.name)
            rs = stmt.executeQuery()
            rs.next()
            val id = rs.getInt(1)
            // A tabela intermediária permite vários funcionários por serviço.
            equipe = c!!.prepareStatement("INSERT INTO servico_funcionario (servico_id, funcionario_id) SELECT ?, id FROM funcionario WHERE id = ? AND setor = 'INSTALACAO' AND habilidade = 'INSTALACAO'")
            for (funcionario in item.funcionarios.distinct()) {
                equipe.setInt(1, id)
                equipe.setInt(2, funcionario)
                check(equipe.executeUpdate() == 1) { "Funcionário inexistente ou não habilitado para instalação." }
            }
            c!!.commit()
        } catch (e: Exception) {
            c?.rollback()
            throw e
        } finally {
            rs?.close()
            equipe?.close()
            stmt?.close()
            c?.close()
        }
    }

    fun buscarPorId(id: Int): Servico? {
        var stmt: PreparedStatement? = null
        var rs: ResultSet? = null
        try {
            conectar()
            stmt = c!!.prepareStatement("SELECT * FROM servico WHERE id = ?")
            stmt.setInt(1, id)
            rs = stmt.executeQuery()
            if (!rs.next()) return null
            return Servico(rs.getInt("cliente_id"), rs.getInt("caixa_id"), rs.getDate("data_instalacao").toLocalDate(),
                rs.getBigDecimal("preco"), StatusServico.valueOf(rs.getString("status")), rs.getInt("id"))
        } finally {
            rs?.close()
            stmt?.close()
            c?.close()
        }
    }

    override fun listar() {
        var stmt: PreparedStatement? = null
        var rs: ResultSet? = null
        try {
            conectar()
            stmt = c!!.prepareStatement("SELECT s.*, cl.nome AS cliente, ca.modelo, COALESCE(string_agg(f.nome || ' (ID ' || f.id || ')', ', ' ORDER BY f.id), 'Sem equipe') AS equipe FROM servico s JOIN cliente cl ON cl.id = s.cliente_id JOIN caixa_da_agua ca ON ca.id = s.caixa_id LEFT JOIN servico_funcionario sf ON sf.servico_id = s.id LEFT JOIN funcionario f ON f.id = sf.funcionario_id GROUP BY s.id, cl.nome, ca.modelo ORDER BY s.id")
            rs = stmt.executeQuery()
            var encontrou = false
            while (rs.next()) {
                encontrou = true
                println("${rs.getInt("id")} | Cliente: ${rs.getString("cliente")} | Produto: ${rs.getString("modelo")} | ${rs.getDate("data_instalacao")} | R$ ${rs.getBigDecimal("preco")} | ${rs.getString("status")} | Equipe: ${rs.getString("equipe")}")
            }
            if (!encontrou) println("Nenhum serviço cadastrado.")
        } finally {
            rs?.close()
            stmt?.close()
            c?.close()
        }
    }

    override fun editar(item: Servico, id: Int) {
        require(item.status == StatusServico.AGENDADO) { "Use concluir ou cancelar para mudar o status." }
        var stmt: PreparedStatement? = null
        try {
            conectar()
            stmt = c!!.prepareStatement("UPDATE servico SET data_instalacao = ?, preco = ? WHERE id = ? AND status = 'AGENDADO'")
            stmt.setDate(1, Date.valueOf(item.dataInstalacao))
            stmt.setBigDecimal(2, item.preco)
            stmt.setInt(3, id)
            check(stmt.executeUpdate() == 1) { "Serviço inexistente ou finalizado." }
        } finally {
            stmt?.close()
            c?.close()
        }
    }

    fun cancelar(id: Int) {
        var stmt: PreparedStatement? = null
        try {
            conectar()
            stmt = c!!.prepareStatement("UPDATE servico SET status = 'CANCELADO' WHERE id = ? AND status = 'AGENDADO'")
            stmt.setInt(1, id)
            check(stmt.executeUpdate() == 1) { "Serviço inexistente ou já finalizado." }
        } finally {
            stmt?.close()
            c?.close()
        }
    }

    override fun excluir(id: Int) {
        throw IllegalArgumentException("Use cancelar para preservar o histórico do serviço.")
    }
}
