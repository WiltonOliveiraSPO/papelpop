package br.com.papelpop.model;

public class VendaItem {

    private int idProduto;
    private String descricaoProduto;
    private int quantidade;
    private double precoUnit;
    private double subtotal;

    public int getIdProduto() { return idProduto; }
    public void setIdProduto(int idProduto) { this.idProduto = idProduto; }

    public String getDescricaoProduto() { return descricaoProduto; }
    public void setDescricaoProduto(String descricaoProduto) { this.descricaoProduto = descricaoProduto; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getPrecoUnit() { return precoUnit; }
    public void setPrecoUnit(double precoUnit) { this.precoUnit = precoUnit; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}
