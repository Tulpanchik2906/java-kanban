package main.servers.executers;

import main.servers.kvserver.KVServer;

import java.io.IOException;

public class KVServerExecuter {

    public static void main(String[] args) throws IOException, InterruptedException {
        KVServer kvServer = new KVServer();
        kvServer.start();
    }
}
