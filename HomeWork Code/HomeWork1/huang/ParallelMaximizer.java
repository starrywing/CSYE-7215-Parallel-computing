package huang;

import java.util.*;

/**
 * This class runs <code>numThreads</code> instances of
 * <code>ParallelMaximizerWorker</code> in parallel to find the maximum
 * <code>Integer</code> in a <code>LinkedList</code>.
 */
public class ParallelMaximizer {

	int numThreads;
	ArrayList<ParallelMaximizerWorker> workers; // = new ArrayList<ParallelMaximizerWorker>(numThreads);

	public ParallelMaximizer(int numThreads) {
		this.numThreads = numThreads;
		workers = new ArrayList<ParallelMaximizerWorker>();
	}

	public static void main(String[] args) {
		int numThreads = 5; // number of threads for the maximizer
		int numElements = 10; // number of integers in the list
		// create a Maximizer with numThreads threads
		ParallelMaximizer maximizer = new ParallelMaximizer(numThreads);
		LinkedList<Integer> list = new LinkedList<Integer>();

		// populate the list
		// TODO: change this implementation to test accordingly
		Random rand = new Random();
		// populate list with random elements
		for (int i = 0; i < numElements; i++) {
			int next = rand.nextInt();
			//System.out.println("added number "+i+" = "+next);
			list.add(next);
		}
		// run the maximizer
		try {
			System.out.println(maximizer.max(list));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Finds the maximum by using <code>numThreads</code> instances of
	 * <code>ParallelMaximizerWorker</code> to find partial maximums and then
	 * combining the results.
	 * 
	 * @param list <code>LinkedList</code> containing <code>Integers</code>
	 * @return Maximum element in the <code>LinkedList</code>
	 * @throws InterruptedException
	 */
	public int max(LinkedList<Integer> list) throws InterruptedException {
		int max = Integer.MIN_VALUE; // initialize max as lowest value

		// run numThreads instances of ParallelMaximizerWorker
		for (int i = 0; i < numThreads; i++) {
			workers.add(i, new ParallelMaximizerWorker(list));
			workers.get(i).run();
		}
		// wait for threads to finish
		System.out.println(workers.size());
		for (int i = 0; i < workers.size(); i++) {
			workers.get(i).join();
		}

		// take the highest of the partial maximums
		// TODO: IMPLEMENT CODE HERE
		for(ParallelMaximizerWorker worker:workers) {
			System.out.println(worker.getPartialMax());
			max = max<worker.getPartialMax()?worker.getPartialMax():max;
		}
		for (int i = 0; i < numThreads; i++) {
			max = max<workers.get(i).getPartialMax()?workers.get(i).getPartialMax():max;
		}
		return max;
	}

}
