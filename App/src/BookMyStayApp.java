/*
================================================================================================================
MAIN CLASS - BookMyStayApp
================================================================================================================

Use Case 6: Safe Room Allocation & Booking Confirmation

Description:
This program processes booking requests from a queue and safely assigns rooms while preventing
double-booking. Each request is handled in FIFO order.

A BookingService performs allocation by:
- Checking availability
- Generating unique room IDs
- Storing assigned room IDs in a Set (ensures uniqueness)
- Updating inventory immediately after allocation

A HashMap maps room types to assigned room IDs, enabling grouped tracking.

This ensures atomic allocation, inventory consistency, and prevents duplicate room assignments.

Key Concepts:
- FIFO Queue Processing
- Set for Uniqueness
- HashMap for Grouped Allocation Tracking
- Atomic Operations
- Inventory Synchronization

@author SAKET-2005
@version 6.0
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

    public abstract String getRoomType();
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
        inventory.put("Single Room",2);
        inventory.put("Double Room",2);
        inventory.put("Suite Room",1);
    }

    public int getAvailability(String roomType)
    {
        return inventory.getOrDefault(roomType,0);
    }

    public void decrement(String roomType)
    {
        int count = getAvailability(roomType);
        if(count > 0)
        {
            inventory.put(roomType, count - 1);
        }
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

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}


class BookingRequestQueue
{
    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r)
    {
        queue.add(r);
    }

    public Reservation getNextRequest()
    {
        return queue.poll(); // FIFO
    }

    public boolean isEmpty()
    {
        return queue.isEmpty();
    }
}

class BookingService
{
    private Set<String> allocatedRoomIds = new HashSet<>();
    private HashMap<String, Set<String>> allocationMap = new HashMap<>();
    private int idCounter = 1;

    public void processBookings(BookingRequestQueue queue, RoomInventory inventory)
    {
        while(!queue.isEmpty())
        {
            Reservation request = queue.getNextRequest();
            String roomType = request.getRoomType();

            System.out.println("\nProcessing request for: " + request.getGuestName());

            int available = inventory.getAvailability(roomType);

            if(available > 0)
            {

                String roomId;
                do {
                    roomId = roomType.substring(0,2).toUpperCase() + idCounter++;
                } while(allocatedRoomIds.contains(roomId));


                allocatedRoomIds.add(roomId);

                allocationMap.putIfAbsent(roomType, new HashSet<>());
                allocationMap.get(roomType).add(roomId);

                inventory.decrement(roomType);

                System.out.println("Booking CONFIRMED");
                System.out.println("Room ID: " + roomId);
            }
            else
            {
                System.out.println("Booking FAILED - No rooms available");
            }
        }
    }

    public void displayAllocations()
    {
        System.out.println("\nFinal Room Allocations:");

        for(String type : allocationMap.keySet())
        {
            System.out.println(type + " -> " + allocationMap.get(type));
        }
    }
}


public class BookMyStayApp
{
    public static void main(String args[])
    {
        System.out.println("Welcome to Hotel Booking Management System!");
        System.out.println("Version: 6.0\n");

        RoomInventory inventory = new RoomInventory();
        BookingRequestQueue queue = new BookingRequestQueue();


        queue.addRequest(new Reservation("Alice","Single Room"));
        queue.addRequest(new Reservation("Bob","Single Room"));
        queue.addRequest(new Reservation("Charlie","Single Room")); // should fail
        queue.addRequest(new Reservation("David","Suite Room"));

        BookingService service = new BookingService();


        service.processBookings(queue, inventory);

        service.displayAllocations();

        System.out.println("\nRemaining Inventory:");
        inventory.displayInventory();
    }
}