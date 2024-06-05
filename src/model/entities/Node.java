package model.entities;

class Node {
    private String endereco;
    private int porta;

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
