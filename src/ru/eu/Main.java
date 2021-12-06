package ru.eu;

public class Main {

    public static void main(String[] args) {
        try {
            new EchoServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
