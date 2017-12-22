package Server;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Immortan on 12/9/2017.
 */
public class Game {

    private ArrayList<Player> players;
    private ArrayList<String> shipLists = new ArrayList<>();
    private Random random;

    static int MAP_WIDTH = 1000000;
    static int MAP_HEIGHT = 1000000;

    public Game(ArrayList<Player> players){
        this.players = players;

        for(int i=0;i<players.size();i++)
            players.get(i).setReady(false);

        random = new Random();
        startupProcedure();
    }

    public void playerReady(int playerID, String shipList){
        for(int i=0;i<players.size();i++){
            if(players.get(i).getID()==playerID){
                shipLists.add(playerID+">"+getStartCoord(playerID)+">"+shipList.split(">",2)[1]);
                players.get(i).setReady(true);
            }
        }

        boolean allReady = true;
        for(int i=0;i<players.size();i++){
            if(!players.get(i).isReady()){
                allReady = false;
            }
        }

        if(allReady){
            for(int i=0;i<shipLists.size();i++)
                sendMessageToAll(Messagable.SHIP_LIST,shipLists.get(i));
        }
    }

    private void startupProcedure(){
        sendMessageToAll(Messagable.START,""+players.size());

        for(int i=0;i<players.size();i++){
            players.get(i).getSocketBundle().setGame(this);

            boolean locationOkay = false;
            int testX=0, testY=0;
            while(!locationOkay){
                locationOkay = true;
                testX = random.nextInt(MAP_WIDTH-1000)-MAP_WIDTH/2;
                testY = random.nextInt(MAP_HEIGHT)-MAP_HEIGHT/2;

                for(int j=0;j<i;j++){
                    if(!checkDistance(players.get(j),testX,testY)){
                        locationOkay = false;
                    }
                }
            }

            players.get(i).setStartX(testX);
            players.get(i).setStartY(testY);
        }
    }

    public void spawn(){

        //Set potential x y coordinates
        int x = random.nextInt(MAP_WIDTH);
        int y = random.nextInt(MAP_HEIGHT);
        int angle;

        //Determine if x or y
        if(random.nextBoolean()){
            if(random.nextBoolean()) {
                x = -MAP_WIDTH/2+10;
                angle = random.nextInt(179);
            }
            else {
                x = MAP_WIDTH/2-10;
                angle = random.nextInt(178) + 181;
            }
        }
        else{
            if(random.nextBoolean()) {
                y = 10-MAP_HEIGHT/2;
                angle = random.nextInt(179) - 90;
                if(angle<0)
                    angle+=360;
            }
            else {
                y = MAP_HEIGHT/2 - 10;
                angle = random.nextInt(179) + 91;
            }
        }

        sendMessageToAll(Messagable.NEW_ENTITY,6+">"+x+">"+y+">"+angle+">"+(random.nextInt(700)+100));
    }

    private boolean checkDistance(Player player, int testX, int testY){
        if(Math.abs(player.getStartX()-testX)<10000)
            return false;
        if(Math.abs(player.getStartY()-testY)<10000)
            return false;
        return true;
    }

    public String getStartCoord(int playerID){
        Player player = null;
        for(int i=0;i<players.size();i++){
            if(playerID==players.get(i).getID())
                player = players.get(i);
        }

        return (int)player.getStartX()+">"+(int)player.getStartY();
    }

    public String getPlayersAsString(){
        String string = "";

        for(int i=0;i<players.size();i++){
            string+=">"+players.get(i).getID()+">"+players.get(i).getName();
        }

        return string;
    }

    public void sendMessageToAll(int type, String string) {
        for(int i=0;i<players.size();i++){
            players.get(i).getSocketBundle().sendMessage(type+">"+string);
        }
    }
}
