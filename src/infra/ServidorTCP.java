package infra;

import message.ManipuladorMensagem;
import model.entities.NodePeer;

import java.net.*;
import java.io.*;

public class ServidorTCP implements Runnable {
    private NodePeer nodePeer;

    public ServidorTCP(NodePeer nodePeer) {
        this.nodePeer = nodePeer;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(nodePeer.getPorta())) {
            System.out.println("Servidor iniciado no endereço " + nodePeer.getEndereco() + " na porta " + nodePeer.getPorta());

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ManipuladorMensagem(socket, nodePeer)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
