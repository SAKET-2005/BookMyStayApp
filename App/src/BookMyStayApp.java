/*
================================================================================================================
MAIN CLASS - BookMyStayApp
================================================================================================================

Use Case 8: Booking History and Reporting

Description:
This program introduces historical tracking of confirmed bookings to provide operational visibility
and support reporting. Each confirmed reservation is stored in a BookingHistory component.

A List is used to maintain bookings in insertion order, reflecting the sequence of confirmations.
This enables chronological tracking and audit capability.

A separate BookingReportService is responsible for generating reports from stored booking data.
This ensures a clean separation between data storage and reporting logic.

The system treats booking history as persistent information in memory, preparing for future
extension to file or database storage.

Key Concepts:
- Operational Visibility
- List Data Structure
- Ordered Storage
- Historical Tracking
- Reporting Readiness
- Separation of Storage and Reporting
- Persistence Mindset

@author SAKET-2005
@version 8.0
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

    public void display()
    {
        System.out.println("Reservation ID: " + reservationId +
                " | Guest: " + guestName +
                " | Room Type: " + roomType);
    }
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

class BookingHistory
{
    private List<Reservation> history = new ArrayList<>();

    public void addReservation(Reservation r)
    {
        history.add(r);
    }

    public List<Reservation> getAllReservations()
    {
        return history;
    }
}

class BookingService
{
    private Set<String> allocatedRoomIds = new HashSet<>();
    private HashMap<String, Set<String>> allocationMap = new HashMap<>();
    private int idCounter = 1;
    private BookingHistory history;

    public BookingService(BookingHistory history)
    {
        this.history = history;
    }

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

                history.addReservation(request);

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
}

class BookingReportService
{
    public void displayAllBookings(BookingHistory history)
    {
        System.out.println("\nBooking History:");

        for(Reservation r : history.getAllReservations())
        {
            r.display();
        }
    }

    public void generateSummary(BookingHistory history)
    {
        System.out.println("\nBooking Summary:");

        HashMap<String,Integer> summary = new HashMap<>();

        for(Reservation r : history.getAllReservations())
        {
            String type = r.getRoomType();
            summary.put(type, summary.getOrDefault(type,0)+1);
        }

        for(String type : summary.keySet())
        {
            System.out.println(type + " Bookings: " + summary.get(type));
        }
    }
}

public class BookMyStayApp
{
    public static void main(String args[])
    {
        System.out.println("Welcome to Hotel Booking Management System!");
        System.out.println("Version: 8.0\n");

        RoomInventory inventory = new RoomInventory();
        BookingRequestQueue queue = new BookingRequestQueue();
        BookingHistory history = new BookingHistory();

        queue.addRequest(new Reservation("Alice","Single Room","R1"));
        queue.addRequest(new Reservation("Bob","Double Room","R2"));
        queue.addRequest(new Reservation("Charlie","Suite Room","R3"));

        BookingService service = new BookingService(history);
        service.processBookings(queue, inventory);

        BookingReportService reportService = new BookingReportService();

        reportService.displayAllBookings(history);
        reportService.generateSummary(history);

        System.out.println("\nRemaining Inventory:");
        inventory.displayInventory();
    }
}