/*
================================================================================================================
MAIN CLASS - BookMyStayApp
================================================================================================================

Use Case 4: Room Search with Read-Only Access

Description:
This program enables guests to view available room options without modifying system state.
A dedicated SearchService is introduced to handle read-only operations on room inventory.

The system retrieves availability from a centralized RoomInventory and uses Room objects
to display details such as pricing and size. Only room types with availability greater
than zero are shown to the guest.

This design ensures safe data access, prevents unintended modifications, and maintains
a clear separation between search logic and inventory management.

Key Concepts:
- Read-Only Access
- Defensive Programming
- Separation of Concerns
- Inventory as State Holder
- Domain Model Usage
- Validation Logic

@author SAKET-2005
@version 4.0
================================================================================================================
*/

import java.util.HashMap;
import java.util.ArrayList;

abstract class Room
{
    private int beds;
    private int size;
    private double price;

    public Room(int beds,int size,double price)
    {
        this.beds=beds;
        this.size=size;
        this.price=price;
    }

    public int getBeds()
    {
        return beds;
    }

    public int getSize()
    {
        return size;
    }

    public double getPrice()
    {
        return price;
    }

    public abstract String getRoomType();

    public void displayRoomDetails()
    {
        System.out.println("Room Type: "+getRoomType());
        System.out.println("Beds: "+beds);
        System.out.println("Size: "+size+" sq.ft");
        System.out.println("Price per night: "+price);
    }
}

class SingleRoom extends Room
{
    public SingleRoom()
    {
        super(1,200,1000);
    }

    public String getRoomType()
    {
        return "Single Room";
    }
}

class DoubleRoom extends Room
{
    public DoubleRoom()
    {
        super(2,350,1800);
    }

    public String getRoomType()
    {
        return "Double Room";
    }
}

class SuiteRoom extends Room
{
    public SuiteRoom()
    {
        super(3,600,3500);
    }

    public String getRoomType()
    {
        return "Suite Room";
    }
}

class RoomInventory
{
    private HashMap<String,Integer> inventory;

    public RoomInventory()
    {
        inventory=new HashMap<String,Integer>();
        inventory.put("Single Room",5);
        inventory.put("Double Room",3);
        inventory.put("Suite Room",0); // Example: unavailable room
    }

    public int getAvailability(String roomType)
    {
        return inventory.getOrDefault(roomType,0);
    }

    public void displayInventory()
    {
        for(String roomType:inventory.keySet())
        {
            System.out.println(roomType+" Available: "+inventory.get(roomType));
        }
    }
}

class SearchService
{
    public void searchAvailableRooms(ArrayList<Room> rooms, RoomInventory inventory)
    {
        System.out.println("Available Rooms:\n");

        for(Room room : rooms)
        {
            int available = inventory.getAvailability(room.getRoomType());

            // Defensive Programming: Only show valid available rooms
            if(available > 0)
            {
                room.displayRoomDetails();
                System.out.println("Available Rooms: "+available);
                System.out.println();
            }
        }
    }
}

public class BookMyStayApp
{
    public static void main(String args[])
    {
        System.out.println("Welcome to Hotel Booking Management System!");
        System.out.println("Version: 4.0");
        System.out.println("Author: SAKET-2005");
        System.out.println();

        // Room objects (Domain Model)
        ArrayList<Room> rooms = new ArrayList<Room>();
        rooms.add(new SingleRoom());
        rooms.add(new DoubleRoom());
        rooms.add(new SuiteRoom());

        // Inventory (State Holder)
        RoomInventory inventory = new RoomInventory();

        // Search Service (Read-Only Access)
        SearchService searchService = new SearchService();

        // Guest initiates search
        searchService.searchAvailableRooms(rooms, inventory);

        // Verify inventory remains unchanged
        System.out.println("Inventory State After Search (Unchanged):");
        inventory.displayInventory();
    }
}