package sistema

import sistema.caixadeagua.cadastrarNovaCaixa
import sistema.caixadeagua.listarCaixa
import sistema.caixadeagua.editarCaixa

fun menuInicial(){

    do{
        println(" 0 - CADASTRAR CAIXA DE ÁGUA")
        println("1 - EDITAR CAIXA DE ÁGUA")
        println("2 - LISTAR CAIXAS DE ÁGUA")
        println("3 - EXCLUIR CAIXA DE ÁGUA")
        println("4 - SAIR")
        println("5 - GESTÃO DE PESSOAS")
        println("6 - SERVIÇOS")
        println("7 - FINANCEIRO")


        val op : Int? = readln().toIntOrNull()?: 10

            //se for um digito será vdd)

        try {
            when (op) {
                0 -> cadastrarNovaCaixa()
                1 -> editarCaixa()
                2 -> listarCaixa()
                3 -> excluirCaixa()
                4 -> {
                    println("Adeus amigo")
                    break
                }

                5 -> menuPessoas()
                6 -> menuServico()
                7 -> menuFinanceiro()
                else -> println("Opção inválida!")
            }
        } catch (e: Exception) { mostrarErro(e) }


    }while(true) // fim do while

}//fim da função
