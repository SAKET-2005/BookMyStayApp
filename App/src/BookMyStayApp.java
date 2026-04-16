/*
================================================================================================================
MAIN CLASS - BookMyStayApp
================================================================================================================

Use Case 9: Validation and Error Handling for Reliable Booking

Description:
This program strengthens system reliability by introducing structured validation and error handling.
All booking inputs and system constraints are validated before processing.

An InvalidBookingValidator ensures that room types are valid and availability constraints are respected.
Custom exceptions are used to represent invalid booking scenarios, making errors explicit and easier to manage.

The system follows a fail-fast approach where invalid inputs are detected early and prevented from affecting
system state. Errors are handled gracefully with meaningful messages, ensuring that the application continues
running safely.

Inventory updates and allocation only occur after successful validation, protecting system consistency.

Key Concepts:
- Input Validation
- Custom Exceptions
- Fail-Fast Design
- Guarding System State
- Graceful Failure Handling
- Correctness over Happy Path

@author SAKET-2005
@version 9.0
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

class InvalidBookingException extends Exception
{
    public InvalidBookingException(String message)
    {
        super(message);
    }
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

    public void decrement(String roomType) throws InvalidBookingException
    {
        int count = getAvailability(roomType);
        if(count <= 0)
        {
            throw new InvalidBookingException("No available rooms for type: " + roomType);
        }
        inventory.put(roomType, count - 1);
    }

    public boolean isValidRoomType(String roomType)
    {
        return inventory.containsKey(roomType);
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

class InvalidBookingValidator
{
    public void validate(Reservation r, RoomInventory inventory) throws InvalidBookingException
    {
        if(r.getGuestName() == null || r.getGuestName().isEmpty())
        {
            throw new InvalidBookingException("Guest name cannot be empty");
        }

        if(!inventory.isValidRoomType(r.getRoomType()))
        {
            throw new InvalidBookingException("Invalid room type: " + r.getRoomType());
        }

        if(inventory.getAvailability(r.getRoomType()) <= 0)
        {
            throw new InvalidBookingException("No availability for room type: " + r.getRoomType());
        }
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
    private int idCounter = 1;
    private BookingHistory history;
    private InvalidBookingValidator validator = new InvalidBookingValidator();

    public BookingService(BookingHistory history)
    {
        this.history = history;
    }

    public void processBookings(BookingRequestQueue queue, RoomInventory inventory)
    {
        while(!queue.isEmpty())
        {
            Reservation request = queue.getNextRequest();

            try
            {
                validator.validate(request, inventory);

                String roomId;
                do {
                    roomId = request.getRoomType().substring(0,2).toUpperCase() + idCounter++;
                } while(allocatedRoomIds.contains(roomId));

                allocatedRoomIds.add(roomId);

                inventory.decrement(request.getRoomType());

                history.addReservation(request);

                System.out.println("\nBooking CONFIRMED for " + request.getGuestName());
                System.out.println("Room ID: " + roomId);
                System.out.println("Reservation ID: " + request.getReservationId());
            }
            catch(InvalidBookingException e)
            {
                System.out.println("\nBooking FAILED for " + request.getGuestName());
                System.out.println("Reason: " + e.getMessage());
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
}

public class BookMyStayApp
{
    public static void main(String args[])
    {
        System.out.println("Welcome to Hotel Booking Management System!");
        System.out.println("Version: 9.0\n");

        RoomInventory inventory = new RoomInventory();
        BookingRequestQueue queue = new BookingRequestQueue();
        BookingHistory history = new BookingHistory();

        queue.addRequest(new Reservation("Alice","Single Room","R1"));
        queue.addRequest(new Reservation("","Double Room","R2"));
        queue.addRequest(new Reservation("Charlie","Invalid Room","R3"));
        queue.addRequest(new Reservation("David","Suite Room","R4"));
        queue.addRequest(new Reservation("Eve","Suite Room","R5"));

        BookingService service = new BookingService(history);
        service.processBookings(queue, inventory);

        BookingReportService reportService = new BookingReportService();
        reportService.displayAllBookings(history);

        System.out.println("\nRemaining Inventory:");
        inventory.displayInventory();
    }
}