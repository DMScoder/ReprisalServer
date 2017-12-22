package Server;

/**
 * Created by Immortan on 12/10/2017.
 */
public class Player {

    private boolean ready;
    private String name;
    private int ID;
    private int faction = -1;
    private SocketBundle socketBundle;
    private int startX, startY;

    public Player(String name, int ID, SocketBundle socketBundle){
        this.name = name;
        this.ID=ID;
        this.socketBundle = socketBundle;
    }

    public void setFaction(int faction){
        this.faction =faction;
    }

    public String getName(){
        return name;
    }

    public int getFaction(){
        return faction;
    }

    public void setReady(boolean b){
        ready = b;
    }

    public boolean isReady(){
        return ready;
    }

    public SocketBundle getSocketBundle(){return socketBundle;}

    public float getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        this.name = name;
    }
}
