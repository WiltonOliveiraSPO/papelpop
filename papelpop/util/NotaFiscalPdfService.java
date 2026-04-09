package br.com.papelpop.util;

import br.com.papelpop.model.Venda;
import br.com.papelpop.model.VendaItem;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa a classe NotaFiscalPdfService e centraliza suas responsabilidades no sistema PapelPop.
 * Data de criacao: 09/04/2026
 * Autor: Wilton Almeida Oliveira
 */
public class NotaFiscalPdfService {

    private static final float LARGURA_BOBINA_PONTOS = 226.77f; // 80mm
    private static final float MARGEM = 12f;
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DecimalFormat MOEDA = new DecimalFormat("0.00");

    // Ação: executa a rotina 'gerarAbrirNota' desta classe.
    public Path gerarAbrirNota(int idVenda, String nomeCliente, Venda venda) throws Exception {
        Path arquivo = gerarNotaPdf(idVenda, nomeCliente, venda);
        abrirArquivo(arquivo);
        return arquivo;
    }

    // Ação: executa a rotina 'gerarNotaPdf' desta classe.
    public Path gerarNotaPdf(int idVenda, String nomeCliente, Venda venda) throws Exception {
        float alturaCalculada = calcularAlturaPagina(venda);
        PDRectangle pagina = new PDRectangle(LARGURA_BOBINA_PONTOS, alturaCalculada);

        Path diretorio = Paths.get(System.getProperty("user.home"), "Documents", "papelpop", "notas");
        Files.createDirectories(diretorio);
        Path arquivo = diretorio.resolve("nota-venda-" + idVenda + ".pdf");

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(pagina);
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float y = pagina.getHeight() - MARGEM;
                y = escreverCabecalho(cs, y, idVenda, nomeCliente);
                y = escreverItens(cs, y, venda);
                y = escreverTotal(cs, y, venda);
                escreverQrCode(doc, cs, y, idVenda, venda);
            }

            doc.save(arquivo.toFile());
        }

        return arquivo;
    }

    // Ação: executa a rotina 'escreverCabecalho' desta classe.
    private float escreverCabecalho(PDPageContentStream cs, float y, int idVenda, String nomeCliente) throws IOException {
        y = escreverCentralizado(cs, y, "PAPELPOP PAPELARIA LTDA", PDType1Font.HELVETICA_BOLD, 11);
        y = escreverCentralizado(cs, y, "CNPJ: 00.000.000/0001-00", PDType1Font.HELVETICA, 8);
        y = escreverCentralizado(cs, y, "NF-e (simulada para testes)", PDType1Font.HELVETICA, 8);
        y -= 6;

        y = escreverLinha(cs, y, "Venda: " + idVenda, PDType1Font.HELVETICA, 8);
        y = escreverLinha(cs, y, "Data: " + DATA_HORA.format(LocalDateTime.now()), PDType1Font.HELVETICA, 8);
        y = escreverLinha(cs, y, "Cliente: " + nomeCliente, PDType1Font.HELVETICA, 8);
        y -= 4;
        y = escreverLinha(cs, y, "----------------------------------", PDType1Font.HELVETICA, 8);
        return y;
    }

    // Ação: executa a rotina 'escreverItens' desta classe.
    private float escreverItens(PDPageContentStream cs, float y, Venda venda) throws IOException {
        y = escreverLinha(cs, y, "Item", PDType1Font.HELVETICA_BOLD, 8);
        y = escreverLinha(cs, y, "Qtd x Vlr = Subtotal", PDType1Font.HELVETICA_BOLD, 8);
        y = escreverLinha(cs, y, "----------------------------------", PDType1Font.HELVETICA, 8);

        for (VendaItem item : venda.getItens()) {
            y = escreverLinha(cs, y, item.getDescricaoProduto(), PDType1Font.HELVETICA, 8);
            String linhaValores = item.getQuantidade()
                    + " x R$ " + MOEDA.format(item.getPrecoUnit())
                    + " = R$ " + MOEDA.format(item.getSubtotal());
            y = escreverLinha(cs, y, linhaValores, PDType1Font.HELVETICA, 8);
            y -= 2;
        }
        y = escreverLinha(cs, y, "----------------------------------", PDType1Font.HELVETICA, 8);
        return y;
    }

    // Ação: executa a rotina 'escreverTotal' desta classe.
    private float escreverTotal(PDPageContentStream cs, float y, Venda venda) throws IOException {
        y = escreverLinha(cs, y, "TOTAL: R$ " + MOEDA.format(venda.getTotal()), PDType1Font.HELVETICA_BOLD, 10);
        y -= 6;
        y = escreverLinha(cs, y, "Obrigado pela preferencia!", PDType1Font.HELVETICA, 8);
        return y - 4;
    }

    // Ação: executa a rotina 'escreverQrCode' desta classe.
    private void escreverQrCode(PDDocument doc, PDPageContentStream cs, float y, int idVenda, Venda venda)
            throws IOException, WriterException {
        String urlNfe = montarUrlNfe(idVenda, venda);
        BufferedImage qrImage = gerarImagemQrCode(urlNfe, 110, 110);
        PDImageXObject qrXObject = LosslessFactory.createFromImage(doc, qrImage);

        float xQr = (LARGURA_BOBINA_PONTOS - 90f) / 2f;
        float yQr = Math.max(MARGEM, y - 90f);
        cs.drawImage(qrXObject, xQr, yQr, 90f, 90f);
    }

    // Ação: executa a rotina 'gerarImagemQrCode' desta classe.
    private BufferedImage gerarImagemQrCode(String conteudo, int largura, int altura) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(conteudo, BarcodeFormat.QR_CODE, largura, altura);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    // Ação: executa a rotina 'montarUrlNfe' desta classe.
    private String montarUrlNfe(int idVenda, Venda venda) {
        String total = MOEDA.format(venda.getTotal()).replace(",", ".");
        return "https://www.sefazexemplo.gov.br/nfe/consulta?"
                + "chNFe=PAPELPOP" + idVenda
                + "&total=" + total
                + "&tpAmb=2";
    }

    // Ação: executa a rotina 'calcularAlturaPagina' desta classe.
    private float calcularAlturaPagina(Venda venda) {
        int linhasFixas = 14;
        int linhasPorItem = 3;
        int totalLinhas = linhasFixas + (venda.getItens().size() * linhasPorItem);
        return Math.max(320f, (totalLinhas * 10f) + 120f);
    }

    // Ação: executa a rotina 'escreverLinha' desta classe.
    private float escreverLinha(PDPageContentStream cs, float y, String texto, PDType1Font fonte, float tamanho)
            throws IOException {
        cs.beginText();
        cs.setFont(fonte, tamanho);
        cs.newLineAtOffset(MARGEM, y);
        cs.showText(normalizarTexto(texto));
        cs.endText();
        return y - (tamanho + 2f);
    }

    // Ação: executa a rotina 'escreverCentralizado' desta classe.
    private float escreverCentralizado(PDPageContentStream cs, float y, String texto, PDType1Font fonte, float tamanho)
            throws IOException {
        String textoNormalizado = normalizarTexto(texto);
        float larguraTexto = fonte.getStringWidth(textoNormalizado) / 1000f * tamanho;
        float x = (LARGURA_BOBINA_PONTOS - larguraTexto) / 2f;

        cs.beginText();
        cs.setFont(fonte, tamanho);
        cs.newLineAtOffset(x, y);
        cs.showText(textoNormalizado);
        cs.endText();
        return y - (tamanho + 2f);
    }

    // Ação: executa a rotina 'normalizarTexto' desta classe.
    private String normalizarTexto(String texto) {
        if (texto == null) {
            return "";
        }
        String normalizado = texto
                .replace("ç", "c")
                .replace("Ç", "C")
                .replace("ã", "a")
                .replace("Ã", "A")
                .replace("á", "a")
                .replace("Á", "A")
                .replace("é", "e")
                .replace("É", "E")
                .replace("í", "i")
                .replace("Í", "I")
                .replace("ó", "o")
                .replace("Ó", "O")
                .replace("ú", "u")
                .replace("Ú", "U");
        return normalizado;
    }

    // Ação: executa a rotina 'abrirArquivo' desta classe.
    private void abrirArquivo(Path arquivo) throws IOException {
        if (!Desktop.isDesktopSupported()) {
            throw new IOException("Desktop nao suportado para abrir PDF automaticamente.");
        }
        Desktop.getDesktop().open(arquivo.toFile());
    }
}
