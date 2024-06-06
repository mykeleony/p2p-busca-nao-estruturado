package app;

import model.entities.NodePeer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Uso: java Main <endereco>:<porta> [vizinhos.txt [lista_chave_valor.txt]]");
            return;
        }

        String[] enderecoPorta = args[0].split(":");
        String endereco = enderecoPorta[0];
        int porta = Integer.parseInt(enderecoPorta[1]);

        NodePeer NodePeer = new NodePeer(endereco, porta);

        if (args.length > 1) {
            carregarVizinhos(NodePeer, args[1]);
        }

        if (args.length > 2) {
            carregarChavesValores(NodePeer, args[2]);
        }

        try {
            NodePeer.iniciar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void carregarVizinhos(NodePeer NodePeer, String arquivoVizinhos) {
        try (BufferedReader br = new BufferedReader(new FileReader(arquivoVizinhos))) {
            String linha;

            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(":");
                NodePeer.adicionarVizinho(partes[0], Integer.parseInt(partes[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void carregarChavesValores(NodePeer NodePeer, String arquivoChavesValores) {
        try (BufferedReader br = new BufferedReader(new FileReader(arquivoChavesValores))) {
            String linha;

            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(" ");
                NodePeer.adicionarParChaveValor(partes[0], partes[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
