package repositorio

import java.rmi.server.ObjID
//T é uma classe generica
//A interface e um contrato
//as funções são clausulas
//classes q herdarem interface
//tem que implementar as funcoes
interface InterfaceJPA<T> {


    //item é parametro generico
    fun salvar(item: T)
    fun listar()
    fun editar(item: T, id: Int)
    fun excluir(id: Int)
}