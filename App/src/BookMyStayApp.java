/*
================================================================================================================
MAIN CLASS - BookMyStayApp
================================================================================================================

Use Case 7: Add-On Services for Reservations

Description:
This program extends the booking system to support optional add-on services without modifying
core booking or allocation logic.

Guests can select multiple services such as WiFi, Breakfast, or Parking for an existing
reservation. These services are stored separately using a mapping from reservation ID to
a list of selected services.

A one-to-many relationship is maintained where one reservation can have multiple services.
A combination of HashMap and List is used to efficiently manage and preserve service selections.

The AddOnServiceManager handles service association and calculates total additional cost.
Core booking, allocation, and inventory remain unchanged.

Key Concepts:
- Business Extensibility
- One-to-Many Relationship
- Map + List Combination
- Composition over Inheritance
- Separation of Core and Optional Features
- Cost Aggregation

@author SAKET-2005
@version 7.0
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
    private String reservationId;

    public Reservation(String guestName, String roomType, String reservationId)
    {
        this.guestName = guestName;
        this.roomType = roomType;
        this.reservationId = reservationId;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public String getReservationId() { return reservationId; }
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
        return queue.poll();
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
                System.out.println("Reservation ID: " + request.getReservationId());
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

class AddOnService
{
    private String serviceName;
    private double cost;

    public AddOnService(String serviceName, double cost)
    {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() { return serviceName; }
    public double getCost() { return cost; }
}

class AddOnServiceManager
{
    private HashMap<String, List<AddOnService>> serviceMap = new HashMap<>();

    public void addService(String reservationId, AddOnService service)
    {
        serviceMap.putIfAbsent(reservationId, new ArrayList<>());
        serviceMap.get(reservationId).add(service);
    }

    public void displayServices(String reservationId)
    {
        System.out.println("\nServices for Reservation: " + reservationId);

        List<AddOnService> services = serviceMap.get(reservationId);

        if(services == null || services.isEmpty())
        {
            System.out.println("No services selected.");
            return;
        }

        for(AddOnService s : services)
        {
            System.out.println(s.getServiceName() + " - " + s.getCost());
        }
    }

    public double calculateTotalCost(String reservationId)
    {
        double total = 0;
        List<AddOnService> services = serviceMap.get(reservationId);

        if(services != null)
        {
            for(AddOnService s : services)
            {
                total += s.getCost();
            }
        }

        return total;
    }
}

public class BookMyStayApp
{
    public static void main(String args[])
    {
        System.out.println("Welcome to Hotel Booking Management System!");
        System.out.println("Version: 7.0\n");

        RoomInventory inventory = new RoomInventory();
        BookingRequestQueue queue = new BookingRequestQueue();

        queue.addRequest(new Reservation("Alice","Single Room","R1"));
        queue.addRequest(new Reservation("Bob","Double Room","R2"));

        BookingService service = new BookingService();
        service.processBookings(queue, inventory);

        AddOnServiceManager addOnManager = new AddOnServiceManager();

        addOnManager.addService("R1", new AddOnService("Breakfast", 200));
        addOnManager.addService("R1", new AddOnService("WiFi", 100));
        addOnManager.addService("R2", new AddOnService("Parking", 150));

        addOnManager.displayServices("R1");
        System.out.println("Total Add-On Cost: " + addOnManager.calculateTotalCost("R1"));

        addOnManager.displayServices("R2");
        System.out.println("Total Add-On Cost: " + addOnManager.calculateTotalCost("R2"));

        System.out.println("\nRemaining Inventory:");
        inventory.displayInventory();
    }
}