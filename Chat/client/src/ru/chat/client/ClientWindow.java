package ru.chat.client;

import ru.chat.network.TCPConnection;
import ru.chat.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener{

    private static final String IP_ADDR = "192.168.1.35";
    private static final int PORT = 8189;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() { //В потоке ЕДТ
            @Override
            public void run() {
                new ClientWindow();
            }
        });

    }

    private final JTextArea log  = new JTextArea();
    private final JTextField fieldNickname = new JTextField("Alex");
    private final JTextField fieldInput = new JTextField();

    private TCPConnection connection;


    private ClientWindow(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH,HEIGHT);
        setLocationRelativeTo(null); //окно по середине
        setAlwaysOnTop(true);
        log.setEditable(false);
        log.setLineWrap(true); //автоматический перенос слов
        add(log, BorderLayout.CENTER);
        fieldInput.addActionListener(this);
        add(fieldInput,BorderLayout.SOUTH);
        add(fieldNickname,BorderLayout.NORTH);

        setVisible(true);
        try {
            connection = new TCPConnection(this, IP_ADDR, PORT);
        } catch (IOException e) {
            printMsg("Connection exeption: " + e);

        }


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String str = fieldInput.getText();
        if(str.equals("")) return;
        fieldInput.setText(null);
        connection.sendString(fieldNickname.getText() + ": " + str);

    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMsg("Connection ready....");

    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String str) {
        printMsg(str);

    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMsg("Connection close");

    }

    @Override
    public void onExeption(TCPConnection tcpConnection, Exception e) {
        printMsg("Connection exeption: " + e);

    }

    private synchronized void printMsg(String str){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(str + "\n");
                log.setCaretPosition(log.getDocument().getLength()); //гарантированный автоскролл
            }
        });
    }
}
