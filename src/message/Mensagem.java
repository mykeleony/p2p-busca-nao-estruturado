package message;

public class Mensagem {
    private final String origem;
    private final int seqNo;
    private final int ttl;
    private final String operacao;
    private final String argumentos;

    public Mensagem(String origem, int seqNo, int ttl, String operacao, String argumentos) {
        this.origem = origem;
        this.seqNo = seqNo;
        this.ttl = ttl;
        this.operacao = operacao;
        this.argumentos = argumentos;
    }

    public Mensagem(String mensagem) {
        String[] partes = mensagem.trim().split(" ", 5); // Alterado para 5 partes

        this.origem = partes[0];
        this.seqNo = Integer.parseInt(partes[1]);
        this.ttl = Integer.parseInt(partes[2]);
        this.operacao = partes[3];
        this.argumentos = partes.length > 4 ? partes[4] : ""; // Garantir que argumentos existam
    }

    @Override
    public String toString() {
        return origem + " " + seqNo + " " + ttl + " " + operacao + " " + argumentos + "\n";
    }

    // Getters
    public String getOrigem() { return origem; }
    public int getSeqNo() { return seqNo; }
    public int getTtl() { return ttl; }
    public String getOperacao() { return operacao; }
    public String getArgumentos() { return argumentos; }
}
