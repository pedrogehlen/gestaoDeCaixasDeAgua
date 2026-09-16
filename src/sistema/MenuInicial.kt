package sistema


fun menuInicial(){

    do{
        println("0 - GESTÃO DE CAIXAS-D'ÁGUA")
        println("1 - GESTÃO DE PESSOAS")
        println("2 - SERVIÇOS")
        println("3 - FINANCEIRO")
        println("4 - SAIR")


        val op : Int? = readln().toIntOrNull()?: 10

            //se for um digito será vdd)

        try {
            when (op) {
                0 -> menuProduto()
                1 -> menuPessoas()
                2 -> menuServico()
                3 -> menuFinanceiro()
                4 -> {
                    println("Adeus amigo")
                    break
                }

                else -> println("Opção inválida!")
            }
        } catch (e: Exception) { mostrarErro(e) }


    }while(true) // fim do while

}//fim da função
