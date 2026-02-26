package br.com.papelpop.view;

import javax.swing.*;

import br.com.papelpop.util.ConexaoSQLite;
import br.com.papelpop.util.IconeSistema;

import java.awt.*;
import java.sql.*;

public class FrmLogin extends JFrame {

    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private JLabel lblMensagem;

    public FrmLogin() {

        setTitle("PapelPop - Login");
        IconeSistema.aplicarIcone(this);
        setSize(400, 430);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        criarInterface();
    }

    private void criarInterface() {

        JPanel painel = new JPanel(new BorderLayout());

        // ======================
        // LOGOMARCA
        // ======================

        JLabel lblLogo = new JLabel();
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        lblLogo.setIcon(redimensionarLogo("C:/papelpop/icons/papelpop.jpg"));

        painel.add(lblLogo, BorderLayout.NORTH);

        // ======================
        // CENTRO
        // ======================

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Login
        gbc.gridx = 0;
        gbc.gridy = 0;
        centro.add(new JLabel("Usuário:"), gbc);

        gbc.gridy = 1;
        txtLogin = new JTextField();
        centro.add(txtLogin, gbc);

        // Senha
        gbc.gridy = 2;
        centro.add(new JLabel("Senha:"), gbc);

        gbc.gridy = 3;
        txtSenha = new JPasswordField();
        centro.add(txtSenha, gbc);

        // Botões
        gbc.gridy = 4;

        JPanel painelBotoes = new JPanel(new FlowLayout());

        JButton btnEntrar = new JButton("Entrar");
        JButton btnSair = new JButton("Sair");

        painelBotoes.add(btnEntrar);
        painelBotoes.add(btnSair);

        centro.add(painelBotoes, gbc);

        // Mensagem erro
        gbc.gridy = 5;
        lblMensagem = new JLabel(" ");
        lblMensagem.setForeground(Color.RED);
        lblMensagem.setHorizontalAlignment(SwingConstants.CENTER);
        centro.add(lblMensagem, gbc);

        painel.add(centro, BorderLayout.CENTER);

        add(painel);

        // ======================
        // AÇÕES
        // ======================

        btnEntrar.addActionListener(e -> autenticar());
        btnSair.addActionListener(e -> System.exit(0));

        getRootPane().setDefaultButton(btnEntrar);
    }

    // ======================
    // AUTENTICAÇÃO
    // ======================

    private void autenticar() {

        String login = txtLogin.getText().trim();
        String senha = new String(txtSenha.getPassword());

        if (login.isEmpty() || senha.isEmpty()) {
            lblMensagem.setText("Informe usuário e senha.");
            return;
        }

        String sql = "SELECT nome FROM usuarios "
                   + "WHERE login = ? AND senha = ? AND ativo = 1";

        try (Connection con = ConexaoSQLite.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, login);
            ps.setString(2, senha);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String nomeUsuario = rs.getString("nome");

                dispose();

                FrmMenuPrincipal principal = new FrmMenuPrincipal();
                principal.setVisible(true);

                JOptionPane.showMessageDialog(
                        principal,
                        "Bem-vindo, " + nomeUsuario + "!"
                );

            } else {

                lblMensagem.setText("Usuário ou senha inválidos.");
                txtSenha.setText("");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ======================
    // REDIMENSIONAR LOGO (50%)
    // ======================

    private ImageIcon redimensionarLogo(String caminho) {

        ImageIcon icon = new ImageIcon(caminho);
        Image img = icon.getImage();

        int largura = icon.getIconWidth() / 2;
        int altura = icon.getIconHeight() / 2;

        Image imgRedimensionada = img.getScaledInstance(
                largura,
                altura,
                Image.SCALE_SMOOTH
        );

        return new ImageIcon(imgRedimensionada);
    }
}