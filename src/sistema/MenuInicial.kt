package sistema

import sistema.caixadeagua.cadastrarNovaCaixa
import sistema.caixadeagua.listarCaixa
import sistema.caixadeagua.editarCaixa

fun menuInicial(){

    do{
        println(" 0 - SAIR")
        println("1 - CADASTRAR CAIXA DE ÁGUA")
        println("2 - EDITAR CAXA DE ÁGUA")
        println("3 - LISTAR CAIXA DE ÁGUA")
        println("4 - EXCLUIR CAXA DE ÁGUA")


        val op : Int? = readln().toIntOrNull()?: 10

            //se for um digito será vdd)

            when (op) {
                0 -> cadastrarNovaCaixa()
                1 -> editarCaixa()
                2 -> listarCaixa()
                3 -> excluirCaixa()
                4 -> {
                    println("Adeus amigo")
                    break
                }

                else -> println("Opção invalida!")
            }


    }while(true) // fim do while

}//fim da função