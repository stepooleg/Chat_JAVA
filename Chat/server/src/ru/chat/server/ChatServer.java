package ru.chat.server;

import ru.chat.network.TCPConnection;
import ru.chat.network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnectionListener{

    public static void main(String[] args) {
        new ChatServer();

    }

    private  final ArrayList<TCPConnection> connections = new ArrayList<>();

    private ChatServer(){
        System.out.println("Server running...");
        try (ServerSocket serverSocket = new ServerSocket(8189)){
            while (true) {
                try {
                    new TCPConnection(this, serverSocket.accept()); //возвращение себя как слушателя и сокета

                }catch (IOException e){
                    System.out.println("TCPConnection exeption: " + e);
                }
            }

        } catch (IOException e) {
          throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendTOAllConnections("Client connection: " + tcpConnection );

    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String str) {
        sendTOAllConnections(str );

    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendTOAllConnections("Client connection: " + tcpConnection );

    }

    @Override
    public synchronized void onExeption(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exeption: " + e);

    }

    private void sendTOAllConnections(String str){//Отправка сообщения всем
        System.out.println(str);
        final  int cnt = connections.size();
        for (int i = 0; i < cnt; i++) connections.get(i).sendString(str);
    }
}
