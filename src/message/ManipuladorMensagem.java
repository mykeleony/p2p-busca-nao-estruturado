package message;

import model.entities.NodePeer;

import java.net.*;
import java.io.*;

public class ManipuladorMensagem implements Runnable {
    private Socket socket;
    private NodePeer noPeer;

    public ManipuladorMensagem(Socket socket, NodePeer noPeer) {
        this.socket = socket;
        this.noPeer = noPeer;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String linha;
            while ((linha = in.readLine()) != null) {
                Mensagem mensagem = new Mensagem(linha);
                String operacao = mensagem.getOperacao();

                if (!noPeer.mensagemJaVista(mensagem.getOrigem(), mensagem.getSeqNo())) {
                    noPeer.marcarMensagemVista(mensagem.getOrigem(), mensagem.getSeqNo());

                    switch (operacao) {
                        case "HELLO" -> {
                            System.out.println("Mensagem recebida: \"" + mensagem + "\"");
                            noPeer.adicionarVizinho(mensagem.getOrigem().split(":")[0], Integer.parseInt(mensagem.getOrigem().split(":")[1]));
                            out.println("HELLO_OK");
                        }
                        case "SEARCH" -> processarMensagemBusca(mensagem);

                        // Adicionar casos para outras operações conforme necessário
                        default -> System.out.println("Operação desconhecida: \"" + operacao + "\"");
                    }
                } else {
                    System.out.println("Mensagem repetida: \"" + mensagem + "\"");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processarMensagemBusca(Mensagem mensagem) {
        // Implementar lógica de processamento de mensagem de busca
    }
}
