package model.entities;

import java.util.*;

public class TabelaChaveValor {
    private Map<String, String> tabela;

    public TabelaChaveValor() {
        this.tabela = new HashMap<>();
    }

    public void adicionarPar(String chave, String valor) {
        tabela.put(chave, valor);
    }

    public boolean contemChave(String chave) {
        return tabela.containsKey(chave);
    }

    public String getValor(String chave) {
        return tabela.get(chave);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        tabela.forEach((chave, valor) -> sb.append("chave: ").append(chave).append(" valor: ").append(valor).append("\n"));
        return sb.toString();
    }
}
