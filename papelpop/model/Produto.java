package br.com.papelpop.model;

/**
 * Representa a classe Produto e centraliza suas responsabilidades no sistema PapelPop.
 * Data de criacao: 09/04/2026
 * Autor: Wilton Almeida Oliveira
 */

public class Produto {

    private int idProduto;
    private String descricao;
    private double preco;
    private boolean ativo;

    // Ação: executa a rotina 'getIdProduto' desta classe.
    public int getIdProduto() {
        return idProduto;
    }

    // Ação: executa a rotina 'setIdProduto' desta classe.
    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    // Ação: executa a rotina 'getDescricao' desta classe.
    public String getDescricao() {
        return descricao;
    }

    // Ação: executa a rotina 'setDescricao' desta classe.
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    // Ação: executa a rotina 'getPreco' desta classe.
    public double getPreco() {
        return preco;
    }

    // Ação: executa a rotina 'setPreco' desta classe.
    public void setPreco(double preco) {
        this.preco = preco;
    }

    // Ação: executa a rotina 'isAtivo' desta classe.
    public boolean isAtivo() {
        return ativo;
    }

    // Ação: executa a rotina 'setAtivo' desta classe.
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
    
    @Override
    // Ação: executa a rotina 'toString' desta classe.
    public String toString() {
        return descricao;
    }

}
