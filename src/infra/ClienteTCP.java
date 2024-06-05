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

            out.println(mensagem.toString());
            System.out.println("Encaminhando mensagem \"" + mensagem + "\" para " + endereco + ":" + porta);

            String resposta = in.readLine();

            if (resposta.equals(mensagem.getOperacao() + "_OK")) {
                System.out.println("Envio feito com sucesso: \"" + mensagem + "\"");
            } else {
                System.out.println("Erro ao enviar mensagem: \"" + mensagem + "\"");
            }
        } catch (IOException e) {
            System.out.println("Erro ao conectar com " + endereco + ":" + porta);
            e.printStackTrace();
        }
    }
}
