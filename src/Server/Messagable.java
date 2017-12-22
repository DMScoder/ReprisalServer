package Server;

/**
 * Created by Immortan on 12/9/2017.
 */
public interface Messagable {
    int NAME = 0; //Join lobby new player object
    int CHAT = 1; //Simple chat message
    int READY = 2; //While in lobby. When all players ready we begin
    int TEST = 3; //For determining if someone is offline. Will delete this person
    int DISCONNECT = 4; //Person has left the lobby
    int FACTION_SELECT = 5; //Choose faction in menu
    int SHIP_LIST = 6;
    int PLAYER_LIST = 7; //List of all players
    int START = 8; //Starts the game
    int WHO_ARE_WE = 9; //For player ID and name
    int YOU_ARE = 10; //Returns player ID and name
    int SHIP_ORDER = 11; //11_<ORDER_TYPE>_<PLAYER_ID>_<SHIP_ID>_<LOCATION_X>_<LOCATION_Y>
    int PROJECTILE_LAUNCH = 12; //12_<PROJECTILE_TYPE>_<PLAYER_ID>_<LOCATION_X>_<LOCATION_Y>_<ANGLE>
    int COLLISION = 13; //Instead of single frame, draw line from previous to current and check if line intersects
    int NEW_ENTITY = 14;
    int MAP_DECREASING = 15;

    void sendMessage(int type, String string);
    void messageReceived(int type, String string);
}
