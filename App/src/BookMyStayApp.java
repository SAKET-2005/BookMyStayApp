/*
================================================================================================================
MAIN CLASS - BookMyStayApp
================================================================================================================

Use Case 12: Persistence and System Recovery

Description:
This program introduces persistence and recovery mechanisms to ensure that critical system state
survives application restarts.

A PersistenceService is responsible for saving and restoring system state, including room inventory
and booking history. The system serializes in-memory state into a file during shutdown and restores
it during startup using deserialization.

This ensures that the application behaves as a stateful system where data is not lost between runs.
The system also handles missing or corrupted persistence files gracefully by initializing a fresh
state when recovery is not possible.

Key Concepts:
- Stateful Applications
- Persistence
- Serialization
- Deserialization
- Inventory Snapshot
- Failure Tolerance
- Preparation for Database Integration

@author SAKET-2005
@version 12.0
================================================================================================================
*/

import java.io.*;
import java.util.*;

class RoomInventory implements Serializable
{
    private static final long serialVersionUID = 1L;

    private HashMap<String, Integer> inventory = new HashMap<>();

    public RoomInventory()
    {
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 2);
        inventory.put("Suite Room", 1);
    }

    public int getAvailability(String roomType)
    {
        return inventory.getOrDefault(roomType, 0);
    }

    public boolean allocate(String roomType)
    {
        int available = getAvailability(roomType);

        if(available > 0)
        {
            inventory.put(roomType, available - 1);
            return true;
        }

        return false;
    }

    public void restore(String roomType, int count)
    {
        inventory.put(roomType, count);
    }

    public Map<String, Integer> getSnapshot()
    {
        return inventory;
    }

    public void displayInventory()
    {
        System.out.println("\nInventory State:");
        for(String type : inventory.keySet())
        {
            System.out.println(type + " Available: " + inventory.get(type));
        }
    }
}

class Reservation implements Serializable
{
    private static final long serialVersionUID = 1L;

    String reservationId;
    String guestName;
    String roomType;

    public Reservation(String reservationId, String guestName, String roomType)
    {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public void display()
    {
        System.out.println(reservationId + " | " + guestName + " | " + roomType);
    }
}

class BookingHistory implements Serializable
{
    private static final long serialVersionUID = 1L;

    private List<Reservation> history = new ArrayList<>();

    public void add(Reservation r)
    {
        history.add(r);
    }

    public List<Reservation> getAll()
    {
        return history;
    }

    public void display()
    {
        System.out.println("\nBooking History:");
        for(Reservation r : history)
        {
            r.display();
        }
    }
}

class PersistenceService
{
    private static final String FILE_NAME = "booking_state.dat";

    public void save(RoomInventory inventory, BookingHistory history)
    {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME)))
        {
            oos.writeObject(inventory);
            oos.writeObject(history);

            System.out.println("\nState saved successfully.");
        }
        catch(IOException e)
        {
            System.out.println("\nError while saving state: " + e.getMessage());
        }
    }

    public Object[] load()
    {
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME)))
        {
            RoomInventory inventory = (RoomInventory) ois.readObject();
            BookingHistory history = (BookingHistory) ois.readObject();

            System.out.println("\nState restored successfully.");

            return new Object[]{inventory, history};
        }
        catch(Exception e)
        {
            System.out.println("\nNo valid saved state found. Starting fresh system.");
            return null;
        }
    }
}

class BookingService
{
    public void processBooking(String id, String name, String roomType,
                               RoomInventory inventory,
                               BookingHistory history)
    {
        if(inventory.allocate(roomType))
        {
            Reservation r = new Reservation(id, name, roomType);
            history.add(r);

            System.out.println("\nBooking CONFIRMED: " + id);
        }
        else
        {
            System.out.println("\nBooking FAILED: No availability for " + roomType);
        }
    }
}

public class BookMyStayApp
{
    public static void main(String args[])
    {
        System.out.println("Welcome to Hotel Booking Management System!");
        System.out.println("Version: 12.0\n");

        PersistenceService persistence = new PersistenceService();

        RoomInventory inventory;
        BookingHistory history;

        Object[] state = persistence.load();

        if(state != null)
        {
            inventory = (RoomInventory) state[0];
            history = (BookingHistory) state[1];
        }
        else
        {
            inventory = new RoomInventory();
            history = new BookingHistory();
        }

        BookingService service = new BookingService();

        service.processBooking("R1", "Alice", "Single Room", inventory, history);
        service.processBooking("R2", "Bob", "Double Room", inventory, history);
        service.processBooking("R3", "Charlie", "Suite Room", inventory, history);

        inventory.displayInventory();
        history.display();

        persistence.save(inventory, history);
    }
}