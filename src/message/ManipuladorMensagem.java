package message;

import infra.ClienteTCP;
import model.entities.NodePeer;
import model.entities.Node;

import java.net.*;
import java.io.*;
import java.util.Random;

public class ManipuladorMensagem implements Runnable {
    private final Socket socket;
    private final NodePeer nodePeer;

    public ManipuladorMensagem(Socket socket, NodePeer nodePeer) {
        this.socket = socket;
        this.nodePeer = nodePeer;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String linha;
            while ((linha = in.readLine()) != null) {
                Mensagem mensagem = new Mensagem(linha);
                String operacao = mensagem.getOperacao();

                if (!nodePeer.mensagemJaVista(mensagem.getOrigem(), mensagem.getSeqNo())) {
                    nodePeer.marcarMensagemVista(mensagem.getOrigem(), mensagem.getSeqNo());

                    switch (operacao) {
                        case "HELLO" -> {
                            System.out.println("Mensagem recebida: \"" + mensagem + "\"");
                            nodePeer.adicionarVizinho(mensagem.getOrigem().split(":")[0], Integer.parseInt(mensagem.getOrigem().split(":")[1]));
                            out.println("HELLO_OK");
                        }

                        case "SEARCH" -> processarMensagemBusca(mensagem);

                        case "VAL" -> {
                            System.out.println("Valor encontrado! Chave: " + mensagem.getArgumentos().split(" ")[2]
                                    + " valor: " + mensagem.getArgumentos().split(" ")[3]);
                            nodePeer.adicionarSaltos(mensagem.getArgumentos().split(" ")[0], Integer.parseInt(mensagem.getArgumentos().split(" ")[4]));
                        }
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
        String[] args = mensagem.getArgumentos().split(" ");
        String modo = args[0];
        int portaAnterior = Integer.parseInt(args[1]);
        String chave = args[2];
        int hopCount = Integer.parseInt(args[3]);

        if (nodePeer.getTabelaChaveValor().contemChave(chave)) {
            System.out.println("Chave encontrada!");
            String valor = nodePeer.getTabelaChaveValor().getValor(chave);
            Mensagem resposta = new Mensagem(nodePeer.getEndereco() + ":" + nodePeer.getPorta(), getNovaSeqNo(), 1, "VAL", modo + " " + chave + " " + valor + " " + hopCount);
            new Thread(new ClienteTCP(mensagem.getOrigem().split(":")[0], Integer.parseInt(mensagem.getOrigem().split(":")[1]), resposta)).start();
        } else if (mensagem.getTtl() > 0) {
            hopCount++;
            Mensagem novaMensagem = new Mensagem(mensagem.getOrigem(), mensagem.getSeqNo(), mensagem.getTtl() - 1, mensagem.getOperacao(), modo + " " + portaAnterior + " " + chave + " " + hopCount);

            switch (modo) {
                case "FL" -> {
                    Mensagem finalNovaMensagem = novaMensagem;
                    nodePeer.getTabelaVizinhos().getVizinhos().stream()
                            .filter(vizinho -> vizinho.getPorta() != portaAnterior)
                            .forEach(vizinho -> new Thread(new ClienteTCP(vizinho.getEndereco(), vizinho.getPorta(), finalNovaMensagem)).start());
                }

                case "RW" -> {
                    Node vizinho = nodePeer.getTabelaVizinhos().getVizinhos().get(new Random().nextInt(nodePeer.getTabelaVizinhos().getVizinhos().size()));
                    new Thread(new ClienteTCP(vizinho.getEndereco(), vizinho.getPorta(), novaMensagem)).start();
                }

                case "BP" -> {
                    if (!novaMensagem.getArgumentos().contains(nodePeer.getEndereco() + ":" + nodePeer.getPorta())) {
                        novaMensagem = new Mensagem(novaMensagem.getOrigem(), novaMensagem.getSeqNo(), novaMensagem.getTtl(), novaMensagem.getOperacao(), novaMensagem.getArgumentos() + " " + nodePeer.getEndereco() + ":" + nodePeer.getPorta());
                    }

                    Node vizinho = nodePeer.getTabelaVizinhos().getVizinhos().get(new Random().nextInt(nodePeer.getTabelaVizinhos().getVizinhos().size()));
                    new Thread(new ClienteTCP(vizinho.getEndereco(), vizinho.getPorta(), novaMensagem)).start();
                }
            }
        }
    }

    private int getNovaSeqNo() {
        return nodePeer.getNovaSeqNo();
    }
}
