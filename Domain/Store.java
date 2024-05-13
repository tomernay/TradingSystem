package Domain;

public class Store {
  
    private int id;
    private String name;
    private Inventory inventory;

    // Constructor
    public Store(int id, String name, Inventory inventory) {
        this.id = id;
        this.name = name;
        this.inventory = inventory;
    }

    // Getter and setter for id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    // Getter and setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and setter for inventory
    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}
