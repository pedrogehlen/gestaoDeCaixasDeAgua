package repositorio

import produto.CaixaDaAgua
import java.sql.SQLException

class CRUDCaixaDAgua () : InterfaceJPA<CaixaDaAgua>, ConexaoPostgres(){


   override fun salvar(item: CaixaDaAgua){
        println("Salvando...")
        try {
            conectar() //abre a conexão com o banco
            val sql = "INSERT INTO caixa_da_agua " +
                    "(marca, modelo, dimensao, cor, " +
                    "material, formato, preco) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)"

            val stmt = c!!.prepareStatement(sql)

            //Preparar Lista para Double Precision
            val doublePrecision = c!!.createArrayOf("float8", item.dimensao.toTypedArray())
            //O typedArray() converte um Array para um tipo de dado legível para o Postgres

            //Preparar as variáveis para o Banco
            stmt.setString(1, item.marca)
            stmt.setString(2, item.modelo)
            stmt.setArray(3, doublePrecision)
            stmt.setString(4, item.cor.name)
            stmt.setString(5, item.material.name)
            stmt.setString(6, item.formato)
            stmt.setString(7, item.preco.toString())

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

            val sql = "SELECT * from caixa_da_agua"
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

   override fun editar (item: CaixaDaAgua, id: Int){
        try {
            conectar()
            val sql = "UPDATE caixa_da_agua SET " +
                    " preco = ?, marca = ?, modelo = ?, formato = ? WHERE id = ?"

            val stmt = c!!.prepareStatement(sql)

            stmt.setString(1, item.preco.toString())
            stmt.setString(2, item.marca.toString())
            stmt.setString(3, item.modelo.toString())
            stmt.setString(4, item.formato.toString())
            stmt.setInt(5,id)
            stmt.executeUpdate()
            stmt.close()
            c!!.close()

        } catch (e: SQLException){
            println(e.printStackTrace())
        }

    }

   override fun excluir(id: Int){
        try {
            conectar()
            val sql = "DELETE FROM caixa_da_agua WHERE id = ?"
            val stmt = c!!.prepareStatement(sql)
            stmt.setInt(1,id)
            stmt.executeUpdate()
            stmt.close()

            c!!.close()

        } catch (e: SQLException){
            println(e.printStackTrace())
        }

    }



}// fim da classe