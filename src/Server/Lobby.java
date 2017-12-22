package Server;

import java.util.ArrayList;

/**
 * Created by Immortan on 12/9/2017.
 */
public class Lobby implements Messagable {

    private SocketMaster socketMaster;

    private Player players[] = new Player[100];
    //private ArrayList<Player> players = new ArrayList<Player>();

    public Lobby(SocketMaster socketMaster){
        this.socketMaster = socketMaster;
    }

    public int addPlayer(String name,SocketBundle socketBundle){
        for(int i=0;i<players.length;i++){
            if(players[i]==null) {
                players[i] = new Player(name,i,socketBundle);
                return i;
            }
        }

        return -1;
    }

    public Player getPlayer(int ID){
        return players[ID];
    }

    public void checkIfStart(){
        ArrayList<Player> readyPlayers = new ArrayList<>();

        for(int i=0;i<players.length;i++){
            if(players[i]!=null&&players[i].isReady()) {
                readyPlayers.add(players[i]);
            }
            if(players[i]!=null&&!players[i].isReady()){
                return;
            }
        }

        if(readyPlayers.size()>=1){
            socketMaster.setStart(readyPlayers);
        }
    }

    public String getConvertedPlayers(){
        String convertedList="";
        for(int i=0;i<players.length;i++){
            if(players[i]!=null)
                convertedList+=players[i].getName()+">"+players[i].getFaction()+">"+players[i].isReady()+">";
        }

        return convertedList;
    }

    public void removePlayer(int ID){
        players[ID] = null;
    }

    @Override
    public void sendMessage(int type, String string) {

    }

    @Override
    public void messageReceived(int type, String string) {

    }
}
