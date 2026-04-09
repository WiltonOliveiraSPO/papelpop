package br.com.papelpop.model;

import java.time.LocalDate;

/**
 * Representa a classe Cliente e centraliza suas responsabilidades no sistema PapelPop.
 * Data de criacao: 09/04/2026
 * Autor: Wilton Almeida Oliveira
 */

public class Cliente {

    private int idCliente;
    private String nome;
    private String telefone;
    private String email;
    private String cpf;
    private LocalDate dataCadastro;

    // Ação: executa a rotina 'getIdCliente' desta classe.
    public int getIdCliente() {
        return idCliente;
    }

    // Ação: executa a rotina 'setIdCliente' desta classe.
    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    // Ação: executa a rotina 'getNome' desta classe.
    public String getNome() {
        return nome;
    }

    // Ação: executa a rotina 'setNome' desta classe.
    public void setNome(String nome) {
        this.nome = nome;
    }

    // Ação: executa a rotina 'getTelefone' desta classe.
    public String getTelefone() {
        return telefone;
    }

    // Ação: executa a rotina 'setTelefone' desta classe.
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    // Ação: executa a rotina 'getEmail' desta classe.
    public String getEmail() {
        return email;
    }

    // Ação: executa a rotina 'setEmail' desta classe.
    public void setEmail(String email) {
        this.email = email;
    }

    // Ação: executa a rotina 'getCpf' desta classe.
    public String getCpf() {
        return cpf;
    }

    // Ação: executa a rotina 'setCpf' desta classe.
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    // Ação: executa a rotina 'getDataCadastro' desta classe.
    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    // Ação: executa a rotina 'setDataCadastro' desta classe.
    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
    
    @Override
    // Ação: executa a rotina 'toString' desta classe.
    public String toString() {
        return nome;
    }

}
