package model.entities;

import infra.ClienteTCP;
import infra.ServidorTCP;
import message.Mensagem;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NodePeer {
    private final String endereco;
    private final int porta;
    private final TabelaVizinhos tabelaVizinhos;
    private final TabelaChaveValor tabelaChaveValor;
    private final Random r = new Random();
    private int ttlPadrao = 100;
    private final Map<String, Set<Integer>> mensagensVistas = new ConcurrentHashMap<>();
    private final Map<String, List<Integer>> saltosPorMetodo = new ConcurrentHashMap<>();
    private int seqNo = 0;

    public NodePeer(String endereco, int porta) {
        this.endereco = endereco;
        this.porta = porta;
        this.tabelaVizinhos = new TabelaVizinhos();
        this.tabelaChaveValor = new TabelaChaveValor();
    }

    public void iniciar() throws IOException {
        // Inicia o servidor TCP
        new Thread(new ServidorTCP(this)).start();

        // Menu interativo
        Scanner scanner = new Scanner(System.in);
        int opcao = -1;

        while (opcao != 9) {
            exibirMenu();
            opcao = scanner.nextInt();
            scanner.nextLine(); // Consumir nova linha

            switch (opcao) {
                case 0 -> listarVizinhos();
                case 1 -> enviarHello();
                case 2 -> realizarBuscaFlooding(scanner);
                case 3 -> realizarBuscaRandomWalk(scanner);
                case 4 -> realizarBuscaProfundidade(scanner);
                case 5 -> exibirEstatisticas();
                case 6 -> alterarTTLPadrao(scanner);
                case 9 -> sair();

                default -> System.out.println("Opção inválida.");
            }
        }

        scanner.close();
    }

    private void exibirMenu() {
        System.out.println("""
                Escolha o comando
                    [0] Listar vizinhos
                    [1] HELLO
                    [2] SEARCH (flooding)
                    [3] SEARCH (random walk)
                    [4] SEARCH (busca em profundidade)
                    [5] Estatísticas
                    [6] Alterar valor padrão de TTL
                    [9] Sair
                """);
    }

    private void listarVizinhos() {
        System.out.println(tabelaVizinhos);
    }

    private void enviarHello() {
        System.out.println("Escolha o vizinho:");
        System.out.println(tabelaVizinhos);

        Scanner scanner = new Scanner(System.in);

        int indice = scanner.nextInt();
        Node vizinho = tabelaVizinhos.getVizinhos().get(indice);
        Mensagem mensagem = new Mensagem(endereco + ":" + porta, getNovaSeqNo(), 1, "HELLO", "");

        new Thread(new ClienteTCP(vizinho.endereco(), vizinho.porta(), mensagem)).start();
    }

    private void realizarBuscaFlooding(Scanner scanner) {
        System.out.println("Digite a chave a ser buscada:");
        String chave = scanner.nextLine();

        if (tabelaChaveValor.contemChave(chave)) {
            System.out.println("\tChave encontrada na tabela local!");
            System.out.println("\tchave: " + chave + " valor: " + tabelaChaveValor.getValor(chave));
        } else {
            Mensagem mensagem = new Mensagem(endereco + ":" + porta, getNovaSeqNo(), ttlPadrao, "SEARCH FL " + porta + " " + chave + " 1", "");
            tabelaVizinhos.getVizinhos().forEach(vizinho -> new Thread(new ClienteTCP(vizinho.endereco(), vizinho.porta(), mensagem)).start());
        }
    }

    private void realizarBuscaRandomWalk(Scanner scanner) {
        System.out.println("Digite a chave a ser buscada:");
        String chave = scanner.nextLine();

        if (tabelaChaveValor.contemChave(chave)) {
            System.out.println("\tChave encontrada na tabela local!");
            System.out.println("\tchave: " + chave + " valor: " + tabelaChaveValor.getValor(chave));
        } else {
            Node vizinho = tabelaVizinhos.getVizinhos().get(r.nextInt(tabelaVizinhos.getVizinhos().size()));

            Mensagem mensagem = new Mensagem(endereco + ":" + porta, getNovaSeqNo(), ttlPadrao, "SEARCH RW " + porta + " " + chave + " 1", "");
            new Thread(new ClienteTCP(vizinho.endereco(), vizinho.porta(), mensagem)).start();
        }
    }

    private void realizarBuscaProfundidade(Scanner scanner) {
        System.out.println("Digite a chave a ser buscada:");
        String chave = scanner.nextLine();

        if (tabelaChaveValor.contemChave(chave)) {
            System.out.println("\tChave encontrada na tabela local!");
            System.out.println("\tchave: " + chave + " valor: " + tabelaChaveValor.getValor(chave));
        } else {
            Node vizinho = tabelaVizinhos.getVizinhos().get(r.nextInt(tabelaVizinhos.getVizinhos().size()));

            Mensagem mensagem = new Mensagem(endereco + ":" + porta, getNovaSeqNo(), ttlPadrao, "SEARCH BP " + porta + " " + chave + " 1", "");
            new Thread(new ClienteTCP(vizinho.endereco(), vizinho.porta(), mensagem)).start();
        }
    }

    private void exibirEstatisticas() {
        System.out.println(String.format("""
                Estatísticas
                    Total de mensagens de flooding vistas: %d
                    Total de mensagens de random walk vistas: %d
                    Total de mensagens de busca em profundidade vistas: %d
                    Média de saltos até encontrar destino por flooding: %.2f
                    Média de saltos até encontrar destino por random walk: %.2f
                    Média de saltos até encontrar destino por busca em profundidade: %.2f
                """,
                contarMensagensPorTipo("FL"),
                contarMensagensPorTipo("RW"),
                contarMensagensPorTipo("BP"),
                calcularMediaSaltos("FL"),
                calcularMediaSaltos("RW"),
                calcularMediaSaltos("BP")
        ));
    }

    private void alterarTTLPadrao(Scanner scanner) {
        System.out.println("Digite novo valor de TTL:");

        ttlPadrao = scanner.nextInt();

        System.out.println("Valor TTL alterado para " + ttlPadrao);
    }

    private void sair() {
        tabelaVizinhos.getVizinhos().forEach(vizinho -> {
            Mensagem mensagem = new Mensagem(endereco + ":" + porta, getNovaSeqNo(), 1, "BYE", "");
            new Thread(new ClienteTCP(vizinho.endereco(), vizinho.porta(), mensagem)).start();
        });

        System.out.println("Saindo...");
        System.exit(0);
    }

    public int getNovaSeqNo() {
        return seqNo++;
    }

    public void adicionarVizinho(String endereco, int porta) {
        tabelaVizinhos.adicionarVizinho(endereco, porta);
    }

    public void removerVizinho(String endereco, int porta) {
        tabelaVizinhos.removerVizinho(endereco, porta);
    }

    public void adicionarParChaveValor(String chave, String valor) {
        tabelaChaveValor.adicionarPar(chave, valor);
    }

    public boolean mensagemJaVista(String origem, int seqNo) {
        return mensagensVistas.getOrDefault(origem, new HashSet<>()).contains(seqNo);
    }

    public void marcarMensagemVista(String origem, int seqNo) {
        mensagensVistas.computeIfAbsent(origem, k -> new HashSet<>()).add(seqNo);
    }

    public void adicionarSaltos(String metodo, int saltos) {
        saltosPorMetodo.computeIfAbsent(metodo, k -> new ArrayList<>()).add(saltos);
    }

    private int contarMensagensPorTipo(String tipo) {
        return saltosPorMetodo.getOrDefault(tipo, Collections.emptyList()).size();
    }

    private double calcularMediaSaltos(String metodo) {
        List<Integer> saltos = saltosPorMetodo.getOrDefault(metodo, Collections.emptyList());
        return saltos.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

    public int getPorta() {
        return porta;
    }

    public String getEndereco() {
        return endereco;
    }

    public TabelaChaveValor getTabelaChaveValor() {
        return tabelaChaveValor;
    }

    public TabelaVizinhos getTabelaVizinhos() {
        return tabelaVizinhos;
    }
}
