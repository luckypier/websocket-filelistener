package com.xyz.websocket;

/**
 * $Id$
 *
 * @author precuay
 * @date 31/03/15
 */
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/websocket/{room}")
public class FileListenerWebSocket {

    Boolean activeWatchLoop=false;
    Boolean activeWatchInitialize=false;
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    MainWatch mainWatch;


    private static Set<Session> clients =
            Collections.synchronizedSet(new HashSet<Session>());

    public FileListenerWebSocket() {

        try {
            if(activeWatchInitialize==false){
                System.out.println("CONSTRUCTOR");
                mainWatch = new MainWatch(Paths.get(System.getProperty("user.home")+"/AAAdeleteme/target/"));
                activeWatchInitialize=true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message, Session session)
            throws IOException, InterruptedException {
        System.out.println("ONMESSAGE");
        if(activeWatchLoop==false){

            activeWatchLoop=true;
            while (true){
                System.out.println("WHILE");
                String fileName = mainWatch.watchOnce();
                System.out.println("FILE "+fileName);
                for (Session client : session.getOpenSessions()) {
                    if (client.isOpen()){
                        client.getBasicRemote().sendText(
                                dateFormat.format(new Date())+" "+
                                        session.getUserProperties().get("room")+
                                        ": "+message + ">>>" +fileName
                        );
                    }
                }
            }
        }

    }

    @OnOpen
    public void onOpen (Session session, @PathParam("room") final String room) {
        // Add session to the connected sessions set
        //clients.add(session);

        session.getUserProperties().put("room", room);
    }

    @OnClose
    public void onClose (Session session) {
        // Remove session from the connected sessions set
        //clients.remove(session);
    }

}
