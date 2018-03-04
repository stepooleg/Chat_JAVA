package ru.chat.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {

    private final Socket socket; //Сокет
    private final Thread rxThread; // Для многопоточности
    private final TCPConnectionListener eventListener; // Слушатель события
    private final BufferedReader in; //Считывание
    private final BufferedWriter out; //Запись

    public TCPConnection(TCPConnectionListener eventListener, String ipAdress, int port) throws IOException  {
        this(eventListener, new Socket(ipAdress, port));
    }

    public TCPConnection(TCPConnectionListener eventListener, Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        rxThread = new Thread(new Runnable() { //Анонимный класс
            @Override
            public void run() {
                try {
                    eventListener.onConnectionReady(TCPConnection.this); //Передали экземпляр обрамляющего класса
                    while (!rxThread.isInterrupted()){  //Запуск потока и кор прерывание
                        eventListener.onReceiveString(TCPConnection.this,in.readLine() );
                    }

                } catch (IOException e) {
                    eventListener.onExeption(TCPConnection.this, e);
                }finally {
                    eventListener.onDisconnect(TCPConnection.this);

                }
            }
        });
        rxThread.start();
    }

    public  synchronized void sendString(String str){
        try {
            out.write(str + "\r\n");
            out.flush(); //Сбрасывает буффер
        } catch (IOException e) {
            eventListener.onExeption(TCPConnection.this, e);
            disconnect();
        }

    }

    public synchronized void disconnect(){
        rxThread.interrupt(); //Флаг останова
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onExeption(TCPConnection.this, e);
        }

    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
