package huang;

import java.util.LinkedList;

/**
 * Given a <code>LinkedList</code>, this class will find the maximum over a
 * subset of its <code>Integers</code>.
 */
public class ParallelMaximizerWorker extends Thread {

	protected LinkedList<Integer> list;
	protected int partialMax = Integer.MIN_VALUE; // initialize to lowest value

	public ParallelMaximizerWorker(LinkedList<Integer> list) {
		this.list = new LinkedList<Integer>();
		for(int i : list) {
			this.list.add(i);
		}
	}

	public ParallelMaximizerWorker() {
		this.list = new LinkedList<Integer>();
	}

	/**
	 * Update <code>partialMax</code> until the list is exhausted.
	 */
	public void run() {
		while (true) {
			int number;
			// check if list is not empty and removes the head
			// synchronization needed to avoid atomicity violation
			synchronized (list) {
				if (list.isEmpty())
					return; // list is empty
				number = list.remove();
			}

			// update partialMax according to new value
			// TODO: IMPLEMENT CODE HERE
			partialMax = partialMax<number?number:partialMax;
			System.out.println(currentThread());
			System.out.println("after the guess partialMax = " + partialMax);
			System.out.println("list size = " + list.size());
		}
	}

	public int getPartialMax() {
		return partialMax;
	}

}
