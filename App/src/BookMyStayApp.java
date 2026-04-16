/*
================================================================================================================
MAIN CLASS - BookMyStayApp
================================================================================================================

Use Case 5: Booking Request Queue (FIFO Handling)

Description:
This program introduces a queue-based booking request intake mechanism to handle multiple
guest requests fairly. Instead of immediately allocating rooms, booking requests are first
stored in a queue in the order they arrive.

A Reservation class represents a guest's booking intent, and a BookingRequestQueue manages
these requests using FIFO (First-Come-First-Served) ordering.

This ensures fairness, preserves request order, and decouples request intake from allocation.
No inventory updates or room assignments occur at this stage.

Key Concepts:
- Queue Data Structure
- FIFO Principle
- Fairness in Request Handling
- Decoupling Request Intake from Allocation
- No State Mutation (Inventory remains unchanged)

@author SAKET-2005
@version 5.0
================================================================================================================
*/

import java.util.*;


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

    public int getBeds() { return beds; }
    public int getSize() { return size; }
    public double getPrice() { return price; }

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
    public SingleRoom() { super(1,200,1000); }
    public String getRoomType() { return "Single Room"; }
}

class DoubleRoom extends Room
{
    public DoubleRoom() { super(2,350,1800); }
    public String getRoomType() { return "Double Room"; }
}

class SuiteRoom extends Room
{
    public SuiteRoom() { super(3,600,3500); }
    public String getRoomType() { return "Suite Room"; }
}


class RoomInventory
{
    private HashMap<String,Integer> inventory;

    public RoomInventory()
    {
        inventory=new HashMap<>();
        inventory.put("Single Room",5);
        inventory.put("Double Room",3);
        inventory.put("Suite Room",2);
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


class Reservation
{
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType)
    {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName()
    {
        return guestName;
    }

    public String getRoomType()
    {
        return roomType;
    }

    public void displayReservation()
    {
        System.out.println("Guest: " + guestName + " | Requested Room: " + roomType);
    }
}


class BookingRequestQueue
{
    private Queue<Reservation> queue;

    public BookingRequestQueue()
    {
        queue = new LinkedList<>();
    }

    // Add request (enqueue)
    public void addRequest(Reservation reservation)
    {
        queue.add(reservation);
        System.out.println("Request added for " + reservation.getGuestName());
    }

    // View all requests in order
    public void displayQueue()
    {
        System.out.println("\nBooking Request Queue (FIFO Order):\n");

        if(queue.isEmpty())
        {
            System.out.println("No pending requests.");
            return;
        }

        for(Reservation r : queue)
        {
            r.displayReservation();
        }
    }
}

public class BookMyStayApp
{
    public static void main(String args[])
    {
        System.out.println("Welcome to Hotel Booking Management System!");
        System.out.println("Version: 5.0");
        System.out.println("Author: SAKET-2005");
        System.out.println();

        // Inventory remains untouched
        RoomInventory inventory = new RoomInventory();

        // Booking Request Queue
        BookingRequestQueue requestQueue = new BookingRequestQueue();

        // Simulating guest booking requests
        Reservation r1 = new Reservation("Alice", "Single Room");
        Reservation r2 = new Reservation("Bob", "Double Room");
        Reservation r3 = new Reservation("Charlie", "Suite Room");

        // Guest submits requests (FIFO order maintained)
        requestQueue.addRequest(r1);
        requestQueue.addRequest(r2);
        requestQueue.addRequest(r3);

        // Display queue (arrival order)
        requestQueue.displayQueue();

        // Verify inventory is unchanged
        System.out.println("\nInventory State (Unchanged):");
        inventory.displayInventory();
    }
}