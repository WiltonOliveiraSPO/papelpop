package br.com.papelpop.model;

/**
 * Representa a classe Estoque e centraliza suas responsabilidades no sistema PapelPop.
 * Data de criacao: 09/04/2026
 * Autor: Wilton Almeida Oliveira
 */

public class Estoque {

    private int idProduto;
    private String descricaoProduto;
    private int quantidade;

    // Ação: executa a rotina 'getIdProduto' desta classe.
    public int getIdProduto() {
        return idProduto;
    }

    // Ação: executa a rotina 'setIdProduto' desta classe.
    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    // Ação: executa a rotina 'getDescricaoProduto' desta classe.
    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    // Ação: executa a rotina 'setDescricaoProduto' desta classe.
    public void setDescricaoProduto(String descricaoProduto) {
        this.descricaoProduto = descricaoProduto;
    }

    // Ação: executa a rotina 'getQuantidade' desta classe.
    public int getQuantidade() {
        return quantidade;
    }

    // Ação: executa a rotina 'setQuantidade' desta classe.
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}
