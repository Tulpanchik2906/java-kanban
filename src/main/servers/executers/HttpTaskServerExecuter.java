package main.servers.executers;

import main.servers.httptaskserver.HttpTaskServer;

import java.io.IOException;

public class HttpTaskServerExecuter {
    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.startHttpTaskServer();
    }
}
