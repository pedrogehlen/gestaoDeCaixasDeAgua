package repositorio

import financeiro.Movimentacao
import java.math.BigDecimal
import java.sql.SQLException
import java.time.LocalDate

class CRUDMovimentacao(): InterfaceJPA<Movimentacao>, ConexaoPostgres(){
    override fun salvar(item: Movimentacao) {

        try {
            conectar()
            //abre a conexão com o banco
            val sql = "INSERT INTO movimentacao " +
                    "(valor, data_movimentacao, descricao) "+
                    "VALUES (?, ?, ?) "

            val stmt = c!!.prepareStatement(sql)


            //Preparar as variáveis para o Banco
            stmt.setString(1, item.valor.toString())
            stmt.setDate(2, java.sql.Date.valueOf(item.dataMovimentacao))
            stmt.setString(3, item.contexto)

            stmt.executeUpdate()

            stmt.close() //encera o placeholder
            c!!.close() //encerra a conexão com o banco
        } catch (e : SQLException){
            println("Não salvou: ${e.printStackTrace()}")

        }
    }

    override fun listar() {
        try {
            conectar()//IMPORTANTE
            val stmt = c!!.createStatement()

            val sql = "SELECT * from movimentacao"
            //Esses metadados vem em forma de Lista, ResultSet
            val metadados = stmt.executeQuery(sql)

            val resultado = metadados.metaData //Metadados
            val tamanhoTabela = resultado.columnCount //Tamanho da Tabela em colunas

            while (metadados.next()) {
                for (i in 1..tamanhoTabela) {
                    //Nome da coluna
                    val nomeColuna = resultado.getColumnName(i)
                    //Dado que está nessa coluna
                    val valorColuna = metadados.getObject(i)
                    println("$nomeColuna -> $valorColuna")
                }//FIM FOR
                println("----------------------------------------------------")
            }//FIM WHILE

            stmt.close()
            c!!.close()
        } catch (e: SQLException) {
            println(e.printStackTrace())
        }
    }

    override fun editar(item: Movimentacao, id: Int) {
        TODO("Not yet implemented")
    }

    override fun excluir(id: Int) {
        TODO("Not yet implemented")
    }

}
