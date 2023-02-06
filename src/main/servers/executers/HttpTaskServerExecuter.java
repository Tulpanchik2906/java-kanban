package main.servers.executers;

import main.servers.httptaskserver.HttpTaskServer;
import main.servers.kvserver.KVServer;

import java.io.IOException;

public class HttpTaskServerExecuter {
    public static void main(String[] args) throws IOException, InterruptedException {
        KVServer kvServer = new KVServer();
        kvServer.start();
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }
}
