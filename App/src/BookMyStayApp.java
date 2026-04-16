/*
================================================================================================================
MAIN CLASS - BookMyStayApp
================================================================================================================

Use Case 11: Concurrent Booking with Thread Safety and Synchronization

Description:
This program demonstrates the impact of concurrent access on shared system resources and how
synchronization ensures correctness in a multi-threaded booking environment.

Multiple guest booking requests are processed simultaneously using threads. A shared booking queue
and shared room inventory are accessed by multiple threads, requiring strict synchronization to
prevent race conditions.

Critical sections are protected using synchronized blocks to ensure that only one thread can modify
inventory and perform allocation at a time. This guarantees consistent system state and prevents
double booking under concurrent execution.

The system prioritizes correctness over performance, ensuring thread-safe booking processing under load.

Key Concepts:
- Race Conditions
- Thread Safety
- Shared Mutable State
- Critical Sections
- Synchronized Access
- Concurrency vs Parallelism

@author SAKET-2005
@version 11.0
================================================================================================================
*/

import java.util.*;

class RoomInventory
{
    private final HashMap<String, Integer> inventory = new HashMap<>();

    public RoomInventory()
    {
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 2);
        inventory.put("Suite Room", 1);
    }

    public synchronized boolean allocate(String roomType)
    {
        int available = inventory.getOrDefault(roomType, 0);

        if(available > 0)
        {
            inventory.put(roomType, available - 1);
            return true;
        }

        return false;
    }

    public synchronized void displayInventory()
    {
        System.out.println("\nFinal Inventory State:");
        for(String type : inventory.keySet())
        {
            System.out.println(type + " Available: " + inventory.get(type));
        }
    }
}

class BookingRequest
{
    String guestName;
    String roomType;

    public BookingRequest(String guestName, String roomType)
    {
        this.guestName = guestName;
        this.roomType = roomType;
    }
}

class BookingQueue
{
    private final Queue<BookingRequest> queue = new LinkedList<>();

    public synchronized void addRequest(BookingRequest r)
    {
        queue.add(r);
    }

    public synchronized BookingRequest getNextRequest()
    {
        return queue.poll();
    }

    public synchronized boolean isEmpty()
    {
        return queue.isEmpty();
    }
}

class BookingWorker extends Thread
{
    private BookingQueue queue;
    private RoomInventory inventory;
    private Set<String> allocated = new HashSet<>();
    private static int idCounter = 1;

    public BookingWorker(BookingQueue queue, RoomInventory inventory)
    {
        this.queue = queue;
        this.inventory = inventory;
    }

    public void run()
    {
        while(true)
        {
            BookingRequest request;

            synchronized(queue)
            {
                if(queue.isEmpty())
                {
                    break;
                }
                request = queue.getNextRequest();
            }

            if(request == null)
            {
                continue;
            }

            boolean success;
            String roomId = null;

            synchronized(inventory)
            {
                success = inventory.allocate(request.roomType);

                if(success)
                {
                    roomId = request.roomType.substring(0,2).toUpperCase() + idCounter++;
                    allocated.add(roomId);
                }
            }

            if(success)
            {
                System.out.println(Thread.currentThread().getName()
                        + " CONFIRMED booking for " + request.guestName
                        + " | Room ID: " + roomId);
            }
            else
            {
                System.out.println(Thread.currentThread().getName()
                        + " FAILED booking for " + request.guestName
                        + " | No availability");
            }

            try
            {
                Thread.sleep(50);
            }
            catch(InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        }
    }
}

public class BookMyStayApp
{
    public static void main(String args[])
    {
        System.out.println("Welcome to Hotel Booking Management System!");
        System.out.println("Version: 11.0\n");

        RoomInventory inventory = new RoomInventory();
        BookingQueue queue = new BookingQueue();

        queue.addRequest(new BookingRequest("Alice", "Single Room"));
        queue.addRequest(new BookingRequest("Bob", "Single Room"));
        queue.addRequest(new BookingRequest("Charlie", "Single Room"));
        queue.addRequest(new BookingRequest("David", "Double Room"));
        queue.addRequest(new BookingRequest("Eve", "Suite Room"));
        queue.addRequest(new BookingRequest("Frank", "Double Room"));

        BookingWorker w1 = new BookingWorker(queue, inventory);
        BookingWorker w2 = new BookingWorker(queue, inventory);
        BookingWorker w3 = new BookingWorker(queue, inventory);

        w1.start();
        w2.start();
        w3.start();

        try
        {
            w1.join();
            w2.join();
            w3.join();
        }
        catch(InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }

        inventory.displayInventory();
    }
}