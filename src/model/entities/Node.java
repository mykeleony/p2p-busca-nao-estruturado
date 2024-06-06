package model.entities;

public class Node {
    private final String endereco;
    private final int porta;

    public Node(String endereco, int porta) {
        this.endereco = endereco;
        this.porta = porta;
    }

    public String getEndereco() { return endereco; }
    public int getPorta() { return porta; }

    public String toString() {
        return endereco + " " + porta;
    }
}
