package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Immortan on 12/9/2017.
 */
public class SocketBundle extends Thread {

    private static final Logger LOGGER = Logger.getLogger(SocketBundle.class.getName());

    public static final int INLOBBY=0;
    public static final int IN_GAME=1;

    private int status;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private SocketMaster socketMaster;
    private boolean active;
    private boolean successfulPing = true;
    private String recentMessage = "";
    private String playerName;
    private Lobby lobby;
    private Game game;
    private int socketIdentifier;

    public SocketBundle(Socket socket, BufferedReader reader,
                        PrintWriter writer, Lobby lobby, SocketMaster socketMaster){

        this.lobby = lobby;
        this.socket = socket;
        this.in = reader;
        this.out = writer;
        this.socketMaster = socketMaster;

        setActive(true);

        start();
    }

    private void processMessage(String message){
        int type = Integer.valueOf(message.split(">",2)[0]);
        String text = message.split(">",2)[1];

        switch (type){
            case (Messagable.TEST):
                break;
            case (Messagable.NAME):
                socketMaster.sendMessage(type,text);
                playerName = text;
                socketIdentifier = lobby.addPlayer(playerName,this);
                socketMaster.sendMessage(Messagable.PLAYER_LIST,lobby.getConvertedPlayers());
                LOGGER.log(Level.INFO, text);
                break;
            case (Messagable.CHAT):
                socketMaster.sendMessage(type,text);
                LOGGER.log(Level.INFO, text);
                break;
            case (Messagable.FACTION_SELECT):
                lobby.getPlayer(socketIdentifier).setFaction(Integer.valueOf(text));
                socketMaster.sendMessage(Messagable.PLAYER_LIST,lobby.getConvertedPlayers());
                break;
            case (Messagable.READY):
                lobby.getPlayer(socketIdentifier).setReady(!lobby.getPlayer(socketIdentifier).isReady());
                socketMaster.sendMessage(Messagable.PLAYER_LIST,lobby.getConvertedPlayers());
                lobby.checkIfStart();
                LOGGER.log(Level.INFO,"Player "+ socketIdentifier +" ready:"+lobby.getPlayer(socketIdentifier).isReady());
                break;
            case (Messagable.WHO_ARE_WE):
                sendMessage(Messagable.YOU_ARE+">"+getSocketIdentifier()+">"+playerName+game.getPlayersAsString());
                break;
            //SHIP LIST INCOMING is factionNum > ship type 0 amount > ship type 1 amount >etc
            //SHIP LIST outgoing is playerNum > startX > startY > shipList^
            case (Messagable.SHIP_LIST):
                LOGGER.log(Level.INFO,socketIdentifier+" sent ship list");
                game.playerReady(socketIdentifier,message);
                break;
            case (Messagable.SHIP_ORDER):
                socketMaster.sendMessage(type,text);
                break;
        }
    }

    public void run(){
        while(active){
            try {
                String message =  in.readLine();
                if(!message.equals("")) {
                    setSuccessfulPing(true);
                    processMessage(message);
                }
            } catch (IOException e) {
                active = false;
                LOGGER.log(Level.INFO,"Connection failed with "+socket.getInetAddress());
                socketMaster.sendMessage(Messagable.DISCONNECT,playerName);
                lobby.removePlayer(socketIdentifier);
                if(!socketMaster.isGameStarted())socketMaster.sendMessage(Messagable.PLAYER_LIST,lobby.getConvertedPlayers());
            }
        }

        if(socket!=null){
            try {
                socket.close();
            } catch (IOException e) {
                LOGGER.log(Level.INFO,"Error closing connection");
                e.printStackTrace();
            }
        }
    }

    public void setGame(Game game){
        this.game = game;
    }

    public synchronized void sendMessage(String message){
        out.println(message);
    }

    public synchronized boolean getPingSuccessful(){
        return successfulPing;
    }

    public void startPing(){
        setSuccessfulPing(false);
        sendMessage("TESTING");
    }

    private synchronized void setSuccessfulPing(boolean b){
        successfulPing = b;
    }

    public synchronized void setActive(boolean b){
        active = b;
    }

    public Socket getSocket(){ return socket;}

    public synchronized String getRecentMessage(){
        return recentMessage;
    }

    public int getSocketIdentifier(){return socketIdentifier;}

    public synchronized boolean isActive(){return active;}
}
