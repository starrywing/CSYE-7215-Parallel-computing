package huang;

import java.util.LinkedList;
import java.util.List;
/**
 * Cooks are simulation actors that have at least one field, a name.
 * When running, a cook attempts to retrieve outstanding orders placed
 * by Eaters and process them.
 */
public class Cook implements Runnable {
	private final String name;
	private Customer currCustomer;
	public List<Food> finishedFood = new LinkedList<Food>();
	/**
	 * You can feel free modify this constructor.  It must
	 * take at least the name, but may take other parameters
	 * if you would find adding them useful. 
	 *
	 * @param: the name of the cook
	 */
	public Cook(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	/**
	 * This method executes as follows.  The cook tries to retrieve
	 * orders placed by Customers.  For each order, a List<Food>, the
	 * cook submits each Food item in the List to an appropriate
	 * Machine, by calling makeFood().  Once all machines have
	 * produced the desired Food, the order is complete, and the Customer
	 * is notified.  The cook can then go to process the next order.
	 * If during its execution the cook is interrupted (i.e., some
	 * other thread calls the interrupt() method on it, which could
	 * raise InterruptedException if the cook is blocking), then it
	 * terminates.
	 */
	public void run() {
		Simulation.logEvent(SimulationEvent.cookStarting(this));
		try {
			while(true) {
				//YOUR CODE GOES HERE...

				//get the order synchronized the list to 
				//lock the access to the list
				synchronized(Simulation.orderList){

					while(Simulation.orderList.isEmpty()){
						//wait for the order coming
						Simulation.orderList.wait();
					}
					
					currCustomer = Simulation.orderList.poll();
					Simulation.logEvent(SimulationEvent.cookReceivedOrder(this, currCustomer.getOrder(), currCustomer.getOrderNum()));
					Simulation.orderList.notifyAll();
				}
				//sends food to machine
				for(Food food : currCustomer.getOrder()) {
					switch(food.toString()){
					case "burger" :
						Simulation.logEvent(SimulationEvent.cookStartedFood(this, food, currCustomer.getOrderNum()));
						Simulation.grill.makeFood(this,currCustomer.getOrderNum());
						break;
					case "fries" :
						Simulation.logEvent(SimulationEvent.cookStartedFood(this, food, currCustomer.getOrderNum()));
						Simulation.frier.makeFood(this,currCustomer.getOrderNum());
						break;
					case "coffee" : 
						Simulation.logEvent(SimulationEvent.cookStartedFood(this, food, currCustomer.getOrderNum()));
						Simulation.star.makeFood(this,currCustomer.getOrderNum());
						break;
					}
				}
				
				//wait for all the food in the same order being done
				synchronized(finishedFood){
					while(!(finishedFood.size() == currCustomer.getOrder().size())){
						finishedFood.wait();
					}
					finishedFood.notifyAll();
				}
				Simulation.logEvent(SimulationEvent.cookCompletedOrder(this, currCustomer.getOrderNum()));
				
				//notify the customer to pick the order
				synchronized(Simulation.orderLock){
					currCustomer.orderComplete();
					//notify the customer to pick current order
					Simulation.orderLock.notifyAll();
				}
				finishedFood = new LinkedList<Food>();
			}
		}
		catch(InterruptedException e) {
			// This code assumes the provided code in the Simulation class
			// that interrupts each cook thread when all customers are done.
			// You might need to change this if you change how things are
			// done in the Simulation class.
			Simulation.logEvent(SimulationEvent.cookEnding(this));
		}
	}
}