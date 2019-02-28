package huang;

import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

/**
 * Simulation is the main class used to run the simulation. You may add any
 * fields (static or instance) or any methods you wish.
 */
public class Simulation {
	// List to track simulation events during simulation
	public static List<SimulationEvent> events;

	public static Comparator<Customer> comparator = new Comparator<Customer>() {

		@Override
		public int compare(Customer c1, Customer c2) {
			return (c1.getPriority() - c2.getPriority());
		}
	};

	public static Queue<Customer> orderList = new PriorityQueue<Customer>(comparator);
	public static LinkedList<Customer> customerList = new LinkedList<>();
	public static int currCapacity;
	public static Machine grill;
	public static Machine frier;
	public static Machine star;
	
	public static Object tableLock = new Object();
	public static Object orderLock = new Object();
	

	/**
	 * Used by other classes in the simulation to log events
	 * 
	 * @param event
	 */
	public static void logEvent(SimulationEvent event) {
		events.add(event);
		System.out.println(event);
	}

	/**
	 * Function responsible for performing the simulation. Returns a List of
	 * SimulationEvent objects, constructed any way you see fit. This List will be
	 * validated by a call to Validate.validateSimulation. This method is called
	 * from Simulation.main(). We should be able to test your code by only calling
	 * runSimulation.
	 * 
	 * Parameters:
	 * 
	 * @param numCustomers    the number of customers wanting to enter the coffee
	 *                        shop
	 * @param numCooks        the number of cooks in the simulation
	 * @param numTables       the number of tables in the coffe shop (i.e. coffee
	 *                        shop capacity)
	 * @param machineCapacity the capacity of all machines in the coffee shop
	 * @param randomOrders    a flag say whether or not to give each customer a
	 *                        random order
	 *
	 */
	public static List<SimulationEvent> runSimulation(int numCustomers, int numCooks, int numTables,
			int machineCapacity, boolean randomOrders) {

		// This method's signature MUST NOT CHANGE.

		// We are providing this events list object for you.
		// It is the ONLY PLACE where a concurrent collection object is
		// allowed to be used.
		events = Collections.synchronizedList(new ArrayList<SimulationEvent>());
		// Start the simulation
		logEvent(SimulationEvent.startSimulation(numCustomers, numCooks, numTables, machineCapacity));

		// Set things up you might need

		// Start up machines
		grill = new Machine("Grill", FoodType.burger, machineCapacity);
		frier = new Machine("Frier", FoodType.fries, machineCapacity);
		star = new Machine("Star", FoodType.coffee, machineCapacity);

		logEvent(SimulationEvent.machineStarting(grill, FoodType.burger, machineCapacity));
		logEvent(SimulationEvent.machineStarting(star, FoodType.coffee, machineCapacity));
		logEvent(SimulationEvent.machineStarting(frier, FoodType.fries, machineCapacity));

		// Let cooks in
		Thread[] cooks = new Thread[numCooks];
		for (int i = 0; i < numCooks; i++) {
			cooks[i] = new Thread(new Cook("Cook " + (i + 1)));
		}
		
		for (int index = 0; index < numCooks; index++) {

		}
		for (int i = 0; i < numCooks; i++) {
			cooks[i].start();
		}

		// create the customers.
		Thread[] customers = new Thread[numCustomers];
		LinkedList<Food> order;

		// if choose the random Orders
		if (!randomOrders) {
			order = new LinkedList<Food>();
			order.add(FoodType.burger);
			order.add(FoodType.fries);
			order.add(FoodType.fries);
			order.add(FoodType.coffee);
			for (int i = 0; i < customers.length; i++) {
				Customer customer = new Customer(i, order, 2);
				customers[i] = new Thread(customer);
				customerList.add(customer);
			}
		} else {
			System.out.println("Random ordering on!!!");
			for (int i = 0; i < customers.length; i++) {
				Random rnd = new Random();
				int burgerCount = rnd.nextInt(3);
				int friesCount = rnd.nextInt(3);
				int coffeeCount = rnd.nextInt(3);
				int priority = rnd.nextInt(3) + 1;
				order = new LinkedList<Food>();
				for (int b = 0; b < burgerCount; b++) {
					order.add(FoodType.burger);
				}
				for (int f = 0; f < friesCount; f++) {
					order.add(FoodType.fries);
				}
				for (int c = 0; c < coffeeCount; c++) {
					order.add(FoodType.coffee);
				}
				Customer customer = new Customer(i, order, priority);
				customers[i] = new Thread(customer);
				customerList.add(customer);
			}
		}

		// Now "let the customers know the shop is open" by
		// starting them running in their own thread.
		for (int i = 0; i < customers.length; i++) {
			customers[i].start();
			// NOTE: Starting the customer does NOT mean they get to go
			// right into the shop. There has to be a table for
			// them. The Customer class' run method has many jobs
			// to do - one of these is waiting for an available
			// table...
		}

		try {
			// Wait for customers to finish
			// -- you need to add some code here...
			// waits for the customer threads to end
			for (int i = 0; i < customers.length; i++) {
				customers[i].join();
			}
//			
//			
//			
//
//			// Then send cooks home...
//			// The easiest way to do this might be the following, where
//			// we interrupt their threads.  There are other approaches
//			// though, so you can change this if you want to.
			for (int i = 0; i < cooks.length; i++) {
				cooks[i].interrupt();
			}
			for (int i = 0; i < cooks.length; i++) {
				cooks[i].join();
			}

		} catch (InterruptedException e) {
			System.out.println("Simulation thread interrupted.");
		}

		// Shut down machines
		logEvent(SimulationEvent.machineEnding(grill));
		logEvent(SimulationEvent.machineEnding(frier));
		logEvent(SimulationEvent.machineEnding(star));

		// Done with simulation
		logEvent(SimulationEvent.endSimulation());

		return events;
	}

	/**
	 * Entry point for the simulation.
	 *
	 * @param args the command-line arguments for the simulation. There should be
	 *             exactly four arguments: the first is the number of customers, the
	 *             second is the number of cooks, the third is the number of tables
	 *             in the coffee shop, and the fourth is the number of items each
	 *             cooking machine can make at the same time.
	 */
	public static void main(String args[]) throws InterruptedException {
		// Parameters to the simulation
		/*
		 * if (args.length != 4) { System.err.
		 * println("usage: java Simulation <#customers> <#cooks> <#tables> <capacity> <randomorders"
		 * ); System.exit(1); } int numCustomers = new Integer(args[0]).intValue(); int
		 * numCooks = new Integer(args[1]).intValue(); int numTables = new
		 * Integer(args[2]).intValue(); int machineCapacity = new
		 * Integer(args[3]).intValue(); boolean randomOrders = new Boolean(args[4]);
		 */
		int numCustomers = 100;
		int numCooks = 2;
		int numTables = 10;
		int machineCapacity = 10;
		boolean randomOrders = true;

		currCapacity = numTables;
		// Run the simulation and then
		// feed the result into the method to validate simulation.
		System.out.println("Did it work? " + Validate
				.validateSimulation(runSimulation(numCustomers, numCooks, numTables, machineCapacity, randomOrders)));
		logAveWaitingTime();
	}

	public static void logAveWaitingTime() {
		long highWaiting = 0;
		long mediumWaiting = 0;
		long lowWaiting = 0;
		int numHigh = 0;
		int numMedium = 0;
		int numLow = 0;
		for (Customer c : customerList) {
			switch (c.getPriority()) {
			case 1:
				numHigh++;
				highWaiting += c.getduration();
				break;
			case 2:
				numMedium++;
				mediumWaiting += c.getduration();
				break;
			case 3:
				numLow++;
				lowWaiting += c.getduration();
				break;
			default:
				break;
			}
		}
		if (numHigh != 0) {
			System.out.println("High priority ave waiting time:" + (highWaiting / numHigh));
		}
		if (numMedium != 0) {
			System.out.println("Medium priority ave waiting time:" + (mediumWaiting / numMedium));
		}
		if (numLow != 0) {
			System.out.println("Low priority ave waiting time:" + (lowWaiting / numLow));
		}
	}

}
