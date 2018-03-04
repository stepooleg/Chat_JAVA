package ru.chat.network;

public interface TCPConnectionListener {

    void onConnectionReady(TCPConnection tcpConnection); //Готовое соединение
    void onReceiveString(TCPConnection tcpConnection, String str); //Получили строку по соединению
    void onDisconnect(TCPConnection tcpConnection); //Разрыв
    void onExeption(TCPConnection tcpConnection, Exception e); //Ошибка
}
