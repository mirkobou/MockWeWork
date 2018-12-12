package ie.mirko;

public class Rooms {
    private String room_name;
    private int room_capacity;
    private String alcohol;
    private String extras;

    Rooms() {
    }

    Rooms(String rm, int cap, String fea, String al) {
        this.alcohol = al;
        this.extras = fea;
        this.room_capacity = cap;
        this.room_name = rm;
    }

    public String getRoom() {
        return room_name;
    }

    public int getCapacity() {
        return room_capacity;
    }

    public String getFeature() {
        return extras;
    }

    public String getAlcohol() {
        return alcohol;
    }

    public void SetRoom(String rm) {
        this.room_name = rm;
    }

    public void SetCapacity(int cap) {
        this.room_capacity = cap;
    }

    public void SetFeature(String fea) {
        this.extras = fea;
    }

    public void setAlcohol(String al) {
        this.alcohol = al;
    }
}