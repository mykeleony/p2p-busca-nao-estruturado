package model.entities;

public record Node(String endereco, int porta) {

    @Override
    public String toString() {
        return endereco + " " + porta;
    }

    @Override
    public String endereco() {
        return endereco;
    }

    @Override
    public int porta() {
        return porta;
    }
}
