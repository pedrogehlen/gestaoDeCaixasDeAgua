package repositorio

import produto.CaixaDaAgua
import enumeradores.Cor
import enumeradores.Material
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types

class CRUDCaixaDAgua : InterfaceJPA<CaixaDaAgua>, ConexaoPostgres() {
    override fun salvar(item: CaixaDaAgua) {
        var stmt: PreparedStatement? = null
        var dimensao: java.sql.Array? = null
        try {
            conectar()
            val sql = "INSERT INTO caixa_da_agua (marca, modelo, dimensao, cor, material, formato, preco, quantidade_estoque, fornecedor_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            stmt = c!!.prepareStatement(sql)
            dimensao = c!!.createArrayOf("float8", item.dimensao.toTypedArray())
            stmt.setString(1, item.marca)
            stmt.setString(2, item.modelo)
            stmt.setArray(3, dimensao)
            stmt.setString(4, item.cor.name)
            stmt.setString(5, item.material.name)
            stmt.setString(6, item.formato)
            stmt.setBigDecimal(7, item.preco)
            stmt.setInt(8, item.quantidadeEstoque)
            if (item.fornecedorId == null) stmt.setNull(9, Types.INTEGER)
            else stmt.setInt(9, item.fornecedorId)
            stmt.executeUpdate()
        } finally {
            dimensao?.free()
            stmt?.close()
            c?.close()
        }
    }

    override fun editar(item: CaixaDaAgua, id: Int) {
        var stmt: PreparedStatement? = null
        var dimensao: java.sql.Array? = null
        try {
            conectar()
            val sql = "UPDATE caixa_da_agua SET preco = ?, marca = ?, modelo = ?, formato = ?, dimensao = ?, cor = ?, material = ?, fornecedor_id = ? WHERE id = ?"
            stmt = c!!.prepareStatement(sql)
            stmt.setBigDecimal(1, item.preco)
            stmt.setString(2, item.marca)
            stmt.setString(3, item.modelo)
            stmt.setString(4, item.formato)
            dimensao = c!!.createArrayOf("float8", item.dimensao.toTypedArray())
            stmt.setArray(5, dimensao)
            stmt.setString(6, item.cor.name)
            stmt.setString(7, item.material.name)
            if (item.fornecedorId == null) stmt.setNull(8, Types.INTEGER)
            else stmt.setInt(8, item.fornecedorId)
            stmt.setInt(9, id)
            check(stmt.executeUpdate() == 1) { "Produto inexistente." }
        } finally {
            dimensao?.free()
            stmt?.close()
            c?.close()
        }
    }

    override fun excluir(id: Int) {
        var stmt: PreparedStatement? = null
        try {
            conectar()
            stmt = c!!.prepareStatement("DELETE FROM caixa_da_agua WHERE id = ?")
            stmt.setInt(1, id)
            check(stmt.executeUpdate() == 1) { "Produto inexistente." }
        } finally {
            stmt?.close()
            c?.close()
        }
    }

    fun listarTodos(): MutableList<CaixaDaAgua> {
        val lista = mutableListOf<CaixaDaAgua>()
        var stmt: PreparedStatement? = null
        var rs: ResultSet? = null
        try {
            conectar()
            stmt = c!!.prepareStatement("SELECT * FROM caixa_da_agua ORDER BY id")
            rs = stmt.executeQuery()
            while (rs.next()) {
                val array = rs.getArray("dimensao")
                val dimensoes = (array.array as Array<*>).map { (it as Number).toDouble() }.toMutableList()
                array.free()
                val fornecedor = rs.getInt("fornecedor_id")
                val fornecedorId: Int? = if (rs.wasNull()) null else fornecedor
                lista.add(CaixaDaAgua(rs.getString("marca"), rs.getString("modelo"), dimensoes,
                    Cor.valueOf(rs.getString("cor")), Material.valueOf(rs.getString("material")),
                    rs.getString("formato"), rs.getBigDecimal("preco"), rs.getInt("id"),
                    rs.getInt("quantidade_estoque"), fornecedorId))
            }
            return lista
        } finally {
            rs?.close()
            stmt?.close()
            c?.close()
        }
    }

    fun buscarPorId(id: Int): CaixaDaAgua? = listarTodos().find { it.id == id }

    override fun listar() {
        val lista = listarTodos()
        if (lista.isEmpty()) println("Nenhuma caixa cadastrada.")
        lista.forEach {
            println("${it.id} - ${it.marca} / ${it.modelo} | ${it.dimensao} | ${it.cor} | ${it.material} | ${it.formato} | R$ ${it.preco} | Estoque: ${it.quantidadeEstoque} | Fornecedor: ${it.fornecedorId ?: "Não informado"}")
        }
    }
}
