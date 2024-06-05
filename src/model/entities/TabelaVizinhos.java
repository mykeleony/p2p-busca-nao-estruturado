package model.entities;

import java.util.*;

public class TabelaVizinhos {
    private final List<Node> vizinhos;

    public TabelaVizinhos() {
        this.vizinhos = new ArrayList<>();
    }

    public void adicionarVizinho(String endereco, int porta) {
        vizinhos.add(new Node(endereco, porta));
    }

    public List<Node> getVizinhos() {
        return vizinhos;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Há ").append(vizinhos.size()).append(" vizinhos na tabela:\n");

        for (int i = 0; i < vizinhos.size(); i++) {
            sb.append("[").append(i).append("] ").append(vizinhos.get(i)).append("\n");
        }

        return sb.toString();
    }
}

