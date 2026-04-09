package br.com.papelpop.model;

/**
 * Representa a classe Usuario e centraliza suas responsabilidades no sistema PapelPop.
 * Data de criacao: 09/04/2026
 * Autor: Wilton Almeida Oliveira
 */

public class Usuario {

    private int idUsuario;
    private String nome;
    private String login;
    private String senha;
    private boolean ativo;

    // Ação: executa a rotina 'getIdUsuario' desta classe.
    public int getIdUsuario() {
        return idUsuario;
    }

    // Ação: executa a rotina 'setIdUsuario' desta classe.
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    // Ação: executa a rotina 'getNome' desta classe.
    public String getNome() {
        return nome;
    }

    // Ação: executa a rotina 'setNome' desta classe.
    public void setNome(String nome) {
        this.nome = nome;
    }

    // Ação: executa a rotina 'getLogin' desta classe.
    public String getLogin() {
        return login;
    }

    // Ação: executa a rotina 'setLogin' desta classe.
    public void setLogin(String login) {
        this.login = login;
    }

    // Ação: executa a rotina 'getSenha' desta classe.
    public String getSenha() {
        return senha;
    }

    // Ação: executa a rotina 'setSenha' desta classe.
    public void setSenha(String senha) {
        this.senha = senha;
    }

    // Ação: executa a rotina 'isAtivo' desta classe.
    public boolean isAtivo() {
        return ativo;
    }

    // Ação: executa a rotina 'setAtivo' desta classe.
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
