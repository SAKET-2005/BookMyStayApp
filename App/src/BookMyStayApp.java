/*
================================================================================================================
MAIN CLASS - BookMyStayApp
================================================================================================================

Use Case 2: Displaying Room Types and Availability

Description:
This program demonstrates object-oriented modeling of hotel room types using abstraction, inheritance,
encapsulation, and polymorphism. The system defines an abstract Room class that represents common room
characteristics, while concrete classes (SingleRoom, DoubleRoom, and SuiteRoom) extend it to represent
specific room types.

Room objects are created when the application runs, and their availability is stored using simple
variables. The program then displays the room details along with their availability in the console.

@author SAKET-2005
@version 2.0
================================================================================================================
*/

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

public class BookMyStayApp
{
    public static void main(String args[])
    {
        System.out.println("Welcome to Hotel Booking Management System!");
        System.out.println("Version: 2.0");
        System.out.println("Author: SAKET-2005");
        System.out.println();

        Room single=new SingleRoom();
        Room doubleRoom=new DoubleRoom();
        Room suite=new SuiteRoom();

        int singleAvailability=5;
        int doubleAvailability=3;
        int suiteAvailability=2;

        single.displayRoomDetails();
        System.out.println("Available Rooms: "+singleAvailability);
        System.out.println();

        doubleRoom.displayRoomDetails();
        System.out.println("Available Rooms: "+doubleAvailability);
        System.out.println();

        suite.displayRoomDetails();
        System.out.println("Available Rooms: "+suiteAvailability);
    }
}