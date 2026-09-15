package repositorio

import financeiro.Movimentacao
import enumeradores.TipoMovimentacao
import java.math.BigDecimal
import java.sql.PreparedStatement
import java.sql.Timestamp
import java.sql.ResultSet

class CRUDMovimentacao : InterfaceJPA<Movimentacao>, ConexaoPostgres() {
    // Usa a conexão já aberta para manter estoque/status e receita na mesma transação.
    private fun inserir(item: Movimentacao) {
        var stmt: PreparedStatement? = null
        try {
            stmt = c!!.prepareStatement("INSERT INTO movimentacao (valor, pagador, recebedor, data_hora, descricao, tipo, responsavel_id) VALUES (?, ?, ?, ?, ?, ?, ?)")
            stmt.setBigDecimal(1, item.valor)
            stmt.setString(2, item.pagador)
            stmt.setString(3, item.recebedor)
            stmt.setTimestamp(4, Timestamp.valueOf(item.dataHora))
            stmt.setString(5, item.descricao)
            stmt.setString(6, item.tipo.name)
            stmt.setInt(7, item.responsavelId)
            stmt.executeUpdate()
        } finally {
            stmt?.close()
        }
    }

    override fun salvar(item: Movimentacao) {
        require(item.tipo == TipoMovimentacao.PAGAMENTO_SALARIO || item.tipo == TipoMovimentacao.PAGAMENTO_DESPESA) {
            "Vendas e compras devem passar pelos fluxos de produto ou serviço."
        }
        try {
            conectar()
            inserir(item)
        } finally {
            c?.close()
        }
    }

    fun registrarProduto(item: Movimentacao, produtoId: Int, quantidade: Int, fornecedorId: Int? = null) {
        require(quantidade > 0) { "Quantidade deve ser positiva." }
        require(item.tipo == TipoMovimentacao.VENDA_PRODUTO || item.tipo == TipoMovimentacao.COMPRA_PRODUTO)
        val venda = item.tipo == TipoMovimentacao.VENDA_PRODUTO
        require(venda || fornecedorId != null) { "Selecione o fornecedor da compra." }
        var stmt: PreparedStatement? = null
        try {
            conectar()
            c!!.autoCommit = false
            val sql = if (venda)
                "UPDATE caixa_da_agua SET quantidade_estoque = quantidade_estoque - ? WHERE id = ? AND quantidade_estoque >= ?"
            else
                "UPDATE caixa_da_agua SET quantidade_estoque = quantidade_estoque + ?, fornecedor_id = ? WHERE id = ?"
            stmt = c!!.prepareStatement(sql)
            stmt.setInt(1, quantidade)
            if (venda) {
                stmt.setInt(2, produtoId)
                stmt.setInt(3, quantidade)
            } else {
                stmt.setInt(2, fornecedorId!!)
                stmt.setInt(3, produtoId)
            }
            check(stmt.executeUpdate() == 1) { "Produto inexistente ou estoque insuficiente." }
            inserir(item)
            c!!.commit()
        } catch (e: Exception) {
            c?.rollback()
            throw e
        } finally {
            stmt?.close()
            c?.close()
        }
    }

    fun concluirServico(servicoId: Int, item: Movimentacao) {
        require(item.tipo == TipoMovimentacao.VENDA_SERVICO)
        var stmt: PreparedStatement? = null
        try {
            conectar()
            c!!.autoCommit = false
            stmt = c!!.prepareStatement("UPDATE servico SET status = 'CONCLUIDO' WHERE id = ? AND status = 'AGENDADO'")
            stmt.setInt(1, servicoId)
            check(stmt.executeUpdate() == 1) { "Serviço inexistente ou já finalizado." }
            inserir(item)
            c!!.commit()
        } catch (e: Exception) {
            c?.rollback()
            throw e
        } finally {
            stmt?.close()
            c?.close()
        }
    }

    fun calcularSaldo(): BigDecimal {
        var stmt: PreparedStatement? = null
        var rs: ResultSet? = null
        try {
            conectar()
            stmt = c!!.prepareStatement("SELECT COALESCE(SUM(valor), 0) FROM movimentacao")
            rs = stmt.executeQuery()
            rs.next()
            return rs.getBigDecimal(1)
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
            stmt = c!!.prepareStatement("SELECT m.*, f.nome AS responsavel FROM movimentacao m JOIN funcionario f ON f.id = m.responsavel_id ORDER BY m.id")
            rs = stmt.executeQuery()
            var encontrou = false
            while (rs.next()) {
                encontrou = true
                println("${rs.getInt("id")} | R$ ${rs.getBigDecimal("valor")} | ${rs.getString("pagador")} -> ${rs.getString("recebedor")} | ${rs.getTimestamp("data_hora")} | ${rs.getString("tipo")} | ${rs.getString("descricao")} | Responsável: ${rs.getString("responsavel")} (ID ${rs.getInt("responsavel_id")})")
            }
            if (!encontrou) println("Nenhuma movimentação registrada.")
        } finally {
            rs?.close()
            stmt?.close()
            c?.close()
        }
    }

    override fun editar(item: Movimentacao, id: Int) {
        throw IllegalArgumentException("Movimentações não podem ser editadas: isso desassociaria o financeiro do estoque/serviço.")
    }

    override fun excluir(id: Int) {
        throw IllegalArgumentException("Movimentações não podem ser excluídas: o histórico financeiro deve ser preservado.")
    }
}
