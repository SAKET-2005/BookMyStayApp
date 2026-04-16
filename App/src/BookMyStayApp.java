/*
================================================================================================================
MAIN CLASS - BookMyStayApp
================================================================================================================

Use Case 10: Safe Booking Cancellation with State Rollback

Description:
This program introduces safe cancellation of confirmed bookings by correctly reversing previously
applied system state changes.

A CancellationService is responsible for validating cancellation requests and performing controlled
rollback operations. When a booking is cancelled, the system restores inventory, removes the booking
from active records, and tracks released room IDs using a Stack to support LIFO-based rollback behavior.

Only valid, previously confirmed reservations can be cancelled. Duplicate or invalid cancellation
requests are rejected safely.

The system ensures strict ordering of rollback operations to maintain consistency and prevent partial
state corruption.

Key Concepts:
- State Reversal
- Stack Data Structure (LIFO Rollback)
- Controlled Mutation Order
- Inventory Restoration
- Cancellation Validation
- System Consistency and Recovery

@author SAKET-2005
@version 10.0
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
        inventory = new HashMap<>();
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 2);
        inventory.put("Suite Room", 1);
    }

    public int getAvailability(String roomType)
    {
        return inventory.getOrDefault(roomType, 0);
    }

    public void decrement(String roomType)
    {
        inventory.put(roomType, getAvailability(roomType) - 1);
    }

    public void increment(String roomType)
    {
        inventory.put(roomType, getAvailability(roomType) + 1);
    }

    public void displayInventory()
    {
        for(String type : inventory.keySet())
        {
            System.out.println(type + " Available: " + inventory.get(type));
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
        System.out.println(reservationId + " | " + guestName + " | " + roomType);
    }
}

class BookingHistory
{
    private List<Reservation> activeBookings = new ArrayList<>();

    public void add(Reservation r)
    {
        activeBookings.add(r);
    }

    public boolean remove(String reservationId)
    {
        return activeBookings.removeIf(r -> r.getReservationId().equals(reservationId));
    }

    public Reservation find(String reservationId)
    {
        for(Reservation r : activeBookings)
        {
            if(r.getReservationId().equals(reservationId))
            {
                return r;
            }
        }
        return null;
    }

    public void display()
    {
        System.out.println("\nActive Bookings:");
        for(Reservation r : activeBookings)
        {
            r.display();
        }
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

class BookingService
{
    private Set<String> allocatedRoomIds = new HashSet<>();
    private int idCounter = 1;

    public void processBookings(BookingRequestQueue queue, RoomInventory inventory, BookingHistory history)
    {
        while(!queue.isEmpty())
        {
            Reservation r = queue.getNextRequest();

            String roomId;
            do
            {
                roomId = r.getRoomType().substring(0,2).toUpperCase() + idCounter++;
            }
            while(allocatedRoomIds.contains(roomId));

            allocatedRoomIds.add(roomId);

            inventory.decrement(r.getRoomType());
            history.add(r);

            System.out.println("\nBooking CONFIRMED: " + r.getReservationId());
            System.out.println("Room ID: " + roomId);
        }
    }
}

class CancellationService
{
    private Stack<String> releasedRoomStack = new Stack<>();

    public void cancelBooking(String reservationId,
                              BookingHistory history,
                              RoomInventory inventory)
    {
        Reservation r = history.find(reservationId);

        if(r == null)
        {
            System.out.println("\nCancellation FAILED: Invalid or already cancelled booking " + reservationId);
            return;
        }

        history.remove(reservationId);

        inventory.increment(r.getRoomType());

        String releasedRoomId = "RL-" + reservationId;
        releasedRoomStack.push(releasedRoomId);

        System.out.println("\nCancellation SUCCESSFUL: " + reservationId);
        System.out.println("Room released: " + releasedRoomId);
    }

    public void showReleasedRooms()
    {
        System.out.println("\nRecently Released Rooms (LIFO):");

        if(releasedRoomStack.isEmpty())
        {
            System.out.println("No cancellations yet.");
            return;
        }

        for(int i = releasedRoomStack.size() - 1; i >= 0; i--)
        {
            System.out.println(releasedRoomStack.get(i));
        }
    }
}

public class BookMyStayApp
{
    public static void main(String args[])
    {
        System.out.println("Welcome to Hotel Booking Management System!");
        System.out.println("Version: 10.0\n");

        RoomInventory inventory = new RoomInventory();
        BookingHistory history = new BookingHistory();
        BookingRequestQueue queue = new BookingRequestQueue();

        queue.addRequest(new Reservation("Alice", "Single Room", "R1"));
        queue.addRequest(new Reservation("Bob", "Double Room", "R2"));
        queue.addRequest(new Reservation("Charlie", "Suite Room", "R3"));

        BookingService bookingService = new BookingService();
        bookingService.processBookings(queue, inventory, history);

        history.display();

        CancellationService cancelService = new CancellationService();

        cancelService.cancelBooking("R2", history, inventory);
        cancelService.cancelBooking("R99", history, inventory);

        history.display();
        inventory.displayInventory();

        cancelService.showReleasedRooms();
    }
}