package huang;

import java.util.Date;
import java.util.List;

/**
 * Customers are simulation actors that have two fields: a name, and a list
 * of Food items that constitute the Customer's order.  When running, an
 * customer attempts to enter the coffee shop (only successful if the
 * coffee shop has a free table), place its order, and then leave the 
 * coffee shop when the order is complete.
 */
public class Customer implements Runnable {
	// JUST ONE SET OF IDEAS ON HOW TO SET THINGS UP...
	private final String name;
	private final int id;
	private final List<Food> order;
	private final int orderNum;
	private final int priority;
	private static int runningCounter = 0;
	private boolean isOrderCompleted;
	private Date orderStart;
	private long duration;

	/**
	 * You can feel free modify this constructor. It must take at least the name and
	 * order but may take other parameters if you would find adding them useful.
	 */
	public Customer(int id, List<Food> order, int priority) {
		this.id =id;
		this.name = "Customer "+id;
		this.order = order;
		this.orderNum = runningCounter++;
		this.priority = priority;
		isOrderCompleted = false;
	}

	public String toString() {
		return name;
	}

	public List<Food> getOrder() {
		return this.order;
	}

	public int getPriority() {
		return this.priority;
	}

	public int getOrderNum() {
		return this.orderNum;
	}
	
	public int getId() {
		return this.id;
	}
	
	public long getduration() {
		return this.duration;
	}
	
	public void orderComplete() {
		isOrderCompleted = true;
	}

	/**
	 * This method defines what an Customer does: The customer attempts to enter the
	 * coffee shop (only successful when the coffee shop has a free table), place
	 * its order, and then leave the coffee shop when the order is complete.
	 */
	public void run() {
		// YOUR CODE GOES HERE...
		Simulation.logEvent(SimulationEvent.customerStarting(this));
		synchronized (Simulation.tableLock) {
			
			while (!(Simulation.currCapacity >0)) {
				try {
					Simulation.tableLock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			Simulation.currCapacity--;
			Simulation.logEvent(SimulationEvent.customerEnteredCoffeeShop(this));
			Simulation.tableLock.notifyAll();
		}
		
		
		//Placed Order
		synchronized (Simulation.orderList) {
			Simulation.orderList.add(this);
			orderStart = new Date();
			Simulation.logEvent(SimulationEvent.customerPlacedOrder(this, this.order, this.orderNum));
			Simulation.orderList.notifyAll();
		}

		synchronized(Simulation.orderLock){
			while(!isOrderCompleted){
				try {
					Simulation.orderLock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Date orderEnd = new Date();
			duration = orderEnd.getTime()-orderStart.getTime();
			Simulation.logEvent(SimulationEvent.customerReceivedOrder(this, this.order, this.orderNum));
			Simulation.orderLock.notifyAll();
		}
		
		synchronized (Simulation.tableLock) {
			Simulation.currCapacity++;
			Simulation.logEvent(SimulationEvent.customerLeavingCoffeeShop(this));
			Simulation.tableLock.notifyAll();
		}
		
	}
}