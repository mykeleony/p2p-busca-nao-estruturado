package app;

import model.entities.NodePeer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Uso: java Main <endereco>:<porta> <arquivo_topologia> <arquivo_chaves_valores>");
            return;
        }

        String[] enderecoPorta = args[0].split(":");
        String endereco = enderecoPorta[0];
        int porta = Integer.parseInt(enderecoPorta[1]);
        String arquivoTopologia = args[1];
        String arquivoChavesValores = args[2];

        NodePeer nodePeer = new NodePeer(endereco, porta);

        System.out.println("Servidor criado: " + endereco + ":" + porta);

        carregarVizinhos(nodePeer, arquivoTopologia);
        carregarChavesValores(nodePeer, arquivoChavesValores);

        try {
            nodePeer.iniciar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void carregarVizinhos(NodePeer nodePeer, String arquivoVizinhos) {
        try (BufferedReader br = new BufferedReader(new FileReader(arquivoVizinhos))) {
            String linha;

            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(":");
                System.out.println("Tentando adicionar vizinho " + partes[0] + ":" + partes[1]);
                nodePeer.adicionarVizinho(partes[0], Integer.parseInt(partes[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void carregarChavesValores(NodePeer nodePeer, String arquivoChavesValores) {
        try (BufferedReader br = new BufferedReader(new FileReader(arquivoChavesValores))) {
            String linha;

            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(" ");
                System.out.println("Adicionando par (" + partes[0] + ", " + partes[1] + ") na tabela local");
                nodePeer.adicionarParChaveValor(partes[0], partes[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
