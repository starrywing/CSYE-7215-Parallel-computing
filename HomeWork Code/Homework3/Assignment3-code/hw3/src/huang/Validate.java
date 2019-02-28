package huang;

import java.util.List;

import huang.SimulationEvent;

/**
 * Validates a simulation
 */
public class Validate {
	private static class InvalidSimulationException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = -9068776563862944136L;

		public InvalidSimulationException() {
		}
	};

	// Helper method for validating the simulation
	private static void check(boolean check,
			String message) throws InvalidSimulationException {
		if (!check) {
			System.err.println("SIMULATION INVALID : "+message);
			throw new Validate.InvalidSimulationException();
		}
	}

	/** 
	 * Validates the given list of events is a valid simulation.
	 * Returns true if the simulation is valid, false otherwise.
	 *
	 * @param events - a list of events generated by the simulation
	 *   in the order they were generated.
	 *
	 * @returns res - whether the simulation was valid or not
	 */
	public static boolean validateSimulation(List<SimulationEvent> events) {
		try {
			check(events.get(0).event == SimulationEvent.EventType.SimulationStarting,
					"Simulation didn't start with initiation event");
			check(events.get(events.size()-1).event == 
					SimulationEvent.EventType.SimulationEnded,
					"Simulation didn't end with termination event");

			/*
			 * In hw3 you will write validation code for things such as:
			 * 
			 * 1.Should not have more eaters than specified
			 * 
			 * 2.Should not have more cooks than specified
			 * 
			 * 3.The coffee shop capacity should not be exceeded
			 * 
			 * 4.The capacity of each machine should not be exceeded
			 * 
			 * 5.Eater should not receive order until cook completes it
			 * 
			 * 6.Eater should not leave coffee shop until order is received
			 * 
			 * 7.Eater should not place more than one order
			 * 
			 * 8.Cook should not work on order before it is placed
			 */

			check(customerNumCheck(events), "1.Have more eaters than specified");

			check(cookNumCheck(events), "2.Have more cooks than specified");

			check(capacityCheck(events), "3.Exceeding the table capacity of the coffee shop");

			check(machineCapacityCheck(events), "4.Exceeding the machine capacity");

			check(cookCompletesBeforeReceives(events), "5.Eater should not receive order until cook completes it");

			check(customerReceivedBeforeLeaving(events),
					"6.Eater should not leave coffee shop until order is received");

			check(customerPlacesOnlyOneOrder(events), "7.Eater should not place more than one order");

			check(cookCompletedBeforeRecieveOrder(events), "8.Cook should not work on order before it is placed");

			return true;
		} catch (InvalidSimulationException e) {
			return false;
		}
	}

	private static boolean cookNumCheck(List<SimulationEvent> events) {
		int numCooks = 0;
		for (SimulationEvent e : events) {
			if (e.event == SimulationEvent.EventType.CookStarting) {
				numCooks++;
			}
		}
		if (numCooks != events.get(0).simParams[1]) {
			return false;
		}
		return true;
	}

	private static boolean customerNumCheck(List<SimulationEvent> events) {
		int numCustomers = 0;
		for (SimulationEvent e : events) {
			if (e.event == SimulationEvent.EventType.CustomerEnteredCoffeeShop) {
				numCustomers++;
			}
		}
		if (numCustomers != events.get(0).simParams[0]) {
			return false;
		}
		return true;
	}

	private static boolean capacityCheck(List<SimulationEvent> events) {
		int currNumCustomers = 0;
		int numTables = events.get(0).simParams[2];
		// Max Customers Test
		for (SimulationEvent e : events) {
			if (e.event == SimulationEvent.EventType.CustomerEnteredCoffeeShop) {
				currNumCustomers++;
			}
			if (e.event == SimulationEvent.EventType.CustomerLeavingCoffeeShop) {
				currNumCustomers--;
			}
			if (currNumCustomers > numTables) {
				return false;
			}
		}
		return true;
	}

	private static boolean machineCapacityCheck(List<SimulationEvent> events) {
		int currNumBurgers = 0;
		int currNumFries = 0;
		int currNumCoffees = 0;
		int machineCapacity = events.get(0).simParams[3];
		// Machine Capacity Test
		for (SimulationEvent e : events) {
			if (e.event == SimulationEvent.EventType.MachineStartingFood) {
				if (e.machine.machineName.equals("Grill")) {
					currNumBurgers++;
				} else if (e.machine.machineName.equals("Fryer")) {
					currNumFries++;
				} else if (e.machine.machineName.equals("CoffeeMaker2000")) {
					currNumCoffees++;
				}
			}
			if (e.event == SimulationEvent.EventType.MachineDoneFood) {
				if (e.machine.machineName.equals("Grill")) {
					currNumBurgers--;
				} else if (e.machine.machineName.equals("Fryer")) {
					currNumFries--;
				} else if (e.machine.machineName.equals("CoffeeMaker2000")) {
					currNumCoffees--;
				}
			}
			if (currNumBurgers > machineCapacity) {
				return false;
			}
			if (currNumFries > machineCapacity) {
				return false;
			}
			if (currNumCoffees > machineCapacity) {
				return false;
			}
		}
		return true;
	}

	private static boolean cookCompletesBeforeReceives(List<SimulationEvent> events) {
		int numCustomers = events.get(0).simParams[0];
		if(numCustomers ==0) {
			return true;
		}
		boolean[] customerReceived = new boolean[numCustomers];
		boolean[] cookCompleted = new boolean[numCustomers];
		for (SimulationEvent e : events) {
			if (e.event == SimulationEvent.EventType.CookCompletedOrder) {
				int order = e.orderNumber;
				cookCompleted[order] = true;
			}
			if (e.event == SimulationEvent.EventType.CustomerReceivedOrder) {
				int order = e.orderNumber;
				customerReceived[order] = true;
				if (customerReceived[order] && cookCompleted[order]) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean customerReceivedBeforeLeaving(List<SimulationEvent> events) {
		int numCustomers = events.get(0).simParams[0];
		boolean[] customerReceived = new boolean[numCustomers];
		boolean[] customerLeft = new boolean[numCustomers];
		boolean result = true;
		for (SimulationEvent e : events) {
			if (e.event == SimulationEvent.EventType.CustomerReceivedOrder) {
				int id = e.customer.getId();
				customerReceived[id] = true;
			}
			if (e.event == SimulationEvent.EventType.CustomerLeavingCoffeeShop) {
				int id = e.customer.getId();
				customerLeft[id] = true;
				if (customerLeft[id] && !customerReceived[id]) {
					result = false;
				}
			}
		}
		return result;
	}

	private static boolean customerPlacesOnlyOneOrder(List<SimulationEvent> events) {
		int numOrders = 0;
		int numCustomers = events.get(0).simParams[0];
		for (SimulationEvent e : events) {
			if (e.event == SimulationEvent.EventType.CustomerPlacedOrder) {
				numOrders++;
			}
		}
		if (numOrders == numCustomers) {
			return true;
		}
		return false;
	}
	
	private static boolean cookCompletedBeforeRecieveOrder(List<SimulationEvent> events) {
		int numOrders = events.get(0).simParams[0];
		
		if(numOrders ==0) {
			return true;
		}
		boolean[] cookReceived = new boolean[numOrders];
		for (SimulationEvent event : events) {
            if (event.event == SimulationEvent.EventType.CookReceivedOrder) {
            	cookReceived[event.orderNumber] = true; 
            }
            if (event.event == SimulationEvent.EventType.CookStartedFood) {
                if (cookReceived[event.orderNumber]) {
                    return true;
                }
            }
        }
		return false;
	}
}