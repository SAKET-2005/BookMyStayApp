/*
================================================================================================================
MAIN CLASS - BookMyStayApp
================================================================================================================

Use Case 3: Centralized Room Inventory Management

Description:
This program demonstrates centralized inventory management for hotel rooms using a HashMap.
Instead of storing room availability in scattered variables, the system introduces a dedicated
RoomInventory component responsible for maintaining and managing availability for all room types.

Room objects still model the characteristics of each room type using abstraction, inheritance,
encapsulation, and polymorphism. However, room availability is now stored in a single HashMap
that maps room types to their available counts. The inventory class provides controlled methods
to retrieve and update availability, ensuring consistent state management across the system.

This design introduces the concept of a single source of truth, improves scalability, and
separates room domain modeling from inventory management logic.

@author SAKET-2005
@version 3.0
================================================================================================================
*/

import java.util.HashMap;

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
        inventory.put("Suite Room",2);
    }

    public int getAvailability(String roomType)
    {
        return inventory.get(roomType);
    }

    public void updateAvailability(String roomType,int count)
    {
        inventory.put(roomType,count);
    }

    public void displayInventory()
    {
        for(String roomType:inventory.keySet())
        {
            System.out.println(roomType+" Available: "+inventory.get(roomType));
        }
    }
}

public class BookMyStayApp
{
    public static void main(String args[])
    {
        System.out.println("Welcome to Hotel Booking Management System!");
        System.out.println("Version: 3.0");
        System.out.println("Author: SAKET-2005");
        System.out.println();

        Room single=new SingleRoom();
        Room doubleRoom=new DoubleRoom();
        Room suite=new SuiteRoom();

        RoomInventory inventory=new RoomInventory();

        single.displayRoomDetails();
        System.out.println("Available Rooms: "+inventory.getAvailability(single.getRoomType()));
        System.out.println();

        doubleRoom.displayRoomDetails();
        System.out.println("Available Rooms: "+inventory.getAvailability(doubleRoom.getRoomType()));
        System.out.println();

        suite.displayRoomDetails();
        System.out.println("Available Rooms: "+inventory.getAvailability(suite.getRoomType()));
        System.out.println();

        System.out.println("Current Inventory State:");
        inventory.displayInventory();
    }
}