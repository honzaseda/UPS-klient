package Pexeso.TCPClient;

/**
 * Created by seda on 03/11/16.
 */
public class ClientInfo {
    private String name;
    private int activeRoom;

    public ClientInfo(String name, int activeRoom) {
        this.name = name;
        this.activeRoom = activeRoom;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActiveRoom(int id) {
        this.activeRoom = id;
    }

    public String getName() {
        return this.name;
    }

    public int getActiveRoom() {
        return this.activeRoom;
    }
}
