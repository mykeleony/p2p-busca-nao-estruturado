package message;

public class Mensagem {
    private String origem;
    private int seqNo;
    private int ttl;
    private String operacao;
    private String argumentos;

    public Mensagem(String origem, int seqNo, int ttl, String operacao, String argumentos) {
        this.origem = origem;
        this.seqNo = seqNo;
        this.ttl = ttl;
        this.operacao = operacao;
        this.argumentos = argumentos;
    }

    public Mensagem(String mensagem) {
        String[] partes = mensagem.split(" ", 4);

        this.origem = partes[0];
        this.seqNo = Integer.parseInt(partes[1]);
        this.ttl = Integer.parseInt(partes[2]);
        this.operacao = partes[3].split(" ")[0];
        this.argumentos = partes[3].substring(this.operacao.length()).trim();
    }

    public String toString() {
        return origem + " " + seqNo + " " + ttl + " " + operacao + " " + argumentos + "\n";
    }

    // Getters e Setters
    public String getOrigem() { return origem; }
    public int getSeqNo() { return seqNo; }
    public int getTtl() { return ttl; }
    public String getOperacao() { return operacao; }
    public String getArgumentos() { return argumentos; }
}

