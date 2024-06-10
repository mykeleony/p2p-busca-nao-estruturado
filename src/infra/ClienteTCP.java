package infra;

import message.Mensagem;

import java.net.*;
import java.io.*;

public class ClienteTCP implements Runnable {
    private final String endereco;
    private final int porta;
    private final Mensagem mensagem;

    public ClienteTCP(String endereco, int porta, Mensagem mensagem) {
        this.endereco = endereco;
        this.porta = porta;
        this.mensagem = mensagem;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(endereco, porta)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("\tEncaminhando mensagem \"" + mensagem + "\" para " + endereco + ":" + porta);
            out.println(mensagem.toString());

            String resposta = in.readLine();

            if (resposta != null && resposta.equals(mensagem.getOperacao() + "_OK")) {
                System.out.println("\tEnvio feito com sucesso: \"" + mensagem + "\"");
            } else {
                System.out.println("\tErro ao enviar mensagem: \"" + mensagem + "\"");
            }
        } catch (IOException e) {
            System.out.println("\tErro ao conectar!");
            e.printStackTrace();
        }
    }
}
