package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Immortan on 12/9/2017.
 */
public class SocketMaster implements Runnable, Messagable {

    private static final Logger LOGGER = Logger.getLogger(SocketMaster.class.getName());


    private ArrayList<SocketBundle> socketArrayList;

    private static final int PORT = 60000;
    private static final String GREETING = "WITNESS ME!";
    private static final String CORRECT_RESPONSE = "WITNESSED!";
    private long lastPing;
    private final long PING_FREQUENCY = 10000;
    private boolean gameStarted = false;
    private boolean START;
    private Game game;
    private ArrayList<Player> players;

    public SocketMaster(){
        Lobby lobby = new Lobby(this);

        try{
            ServerSocket listener = new ServerSocket(PORT);
            socketArrayList = new ArrayList<>(2);
            Thread pingThread = new Thread(this);

            lastPing = System.currentTimeMillis();

            pingThread.start();

            LOGGER.log(Level.INFO,"BATTLECRUISER OPERATIONAL");

            while(!isGameStarted()){
                Socket socket = listener.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(),true);

                if(reader.readLine().equals(GREETING))
                    writer.println(CORRECT_RESPONSE);
                else {
                    socket.close();
                    continue;
                }

                LOGGER.log(Level.INFO,"Connection established to "+socket.getInetAddress());
                getSocketArrayList().add(new SocketBundle(socket,reader,writer,lobby,this));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startNewGame(ArrayList<Player> players){
        setGameStarted(true);
        game = new Game(players);
    }

    private synchronized ArrayList<SocketBundle> getSocketArrayList(){return socketArrayList;}

    public synchronized void removeSocketBundle(SocketBundle socketBundle) {
        socketArrayList.remove(socketBundle);
    }

    //Sockets have ten seconds to respond before being terminated
    @Override
    public void run() {
        while(true){
            if(START&&!gameStarted) {
                startNewGame(players);
            }

            if(game!=null){
                game.spawn();
            }

            if(System.currentTimeMillis() - lastPing > PING_FREQUENCY){
                lastPing = System.currentTimeMillis();
                ArrayList<SocketBundle> failedList = new ArrayList<>(0);

                //Check if last ping was successful
                for(int i=0;i<getSocketArrayList().size();i++){
                    if(!getSocketArrayList().get(i).getPingSuccessful())
                        failedList.add(getSocketArrayList().get(i));
                }

                //Remove if not successful
                for(int i=0;i<failedList.size();i++){
                    LOGGER.log(Level.INFO,failedList.get(i).getSocket().getInetAddress()+"Failed ping");
                    removeSocketBundle(failedList.get(i));
                }

                //Start next round of pings
                for(int i=0;i<getSocketArrayList().size();i++){
                    getSocketArrayList().get(i).startPing();
                }
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sendMessage(int type, String string) {
        for(int i=0;i<socketArrayList.size();i++){
            socketArrayList.get(i).sendMessage(type+">"+string);
        }
    }

    @Override
    public void messageReceived(int type, String string) {

    }

    public void setStart(ArrayList<Player> players){
        START = true;
        this.players = players;
    }

    private synchronized void setGameStarted(boolean b){
        gameStarted = b;
    }

    public synchronized boolean isGameStarted(){
        return gameStarted;
    }
}
