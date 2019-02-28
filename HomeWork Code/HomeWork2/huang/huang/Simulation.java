package huang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Class provided for ease of test. This will not be used in the project
 * evaluation, so feel free to modify it as you like.
 */
public class Simulation {
	public static void main(String[] args) {
		int nrSellers = 2;
		int nrBidders = 1;

		Thread[] sellerThreads = new Thread[nrSellers];
		Thread[] bidderThreads = new Thread[nrBidders];
		Seller[] sellers = new Seller[nrSellers];
		Bidder[] bidders = new Bidder[nrBidders];

		// Start the sellers
		for (int i = 0; i < nrSellers; ++i) {
			sellers[i] = new Seller(AuctionServer.getInstance(), "Seller" + i, 100, 150, i);
			sellerThreads[i] = new Thread(sellers[i]);
			sellerThreads[i].start();
		}

		// Start the buyers
		for (int i = 0; i < nrBidders; ++i) {
			bidders[i] = new Bidder(AuctionServer.getInstance(), "Buyer" + i, 1000, 20, 150, i);
			bidderThreads[i] = new Thread(bidders[i]);
			bidderThreads[i].start();
		}

		// Join on the sellers
		for (int i = 0; i < nrSellers; ++i) {
			try {
				sellerThreads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Join on the bidders
		for (int i = 0; i < nrSellers; ++i) {
			try {
				sellerThreads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// TODO: Add code as needed to debug
		int sum = 0;
		for (int i = 0; i < nrBidders; ++i) {
			sum += bidders[i].cashSpent();
			assertFalse("exceed the server items capacity!",
					bidders[i].mostItemsAvailable() > AuctionServer.serverCapacity);
		}
		assertFalse("the cashSpent dismatch with revenue !", sum != AuctionServer.getInstance().revenue());
		System.out.println(" total item sold = " + AuctionServer.getInstance().soldItemsCount());
		System.out.println(" the revenue = " + AuctionServer.getInstance().revenue());
		List<Item> submitedItemList = AuctionServer.getInstance().getAllItemsAndIds();
		//System.out.println(" submitedItemList.size() = " + submitedItemList.size());
		
		for (int i = 0; i < submitedItemList.size(); i++) {
			assertEquals("submit has synchornical problem!", submitedItemList.get(i).listingID(), i);
			assertFalse("submit Invalid!", submitedItemList.get(i).listingID() == -1);
		}
		
		
	}

	/**
	 * ----------------------------------------------------------------------------------------------------------
	 * ----------------------------------------------Test Cases start-------------------------------------------------
	 * ---------------------------------------------------------------------------------------------------------
	 */

	public void submitItemTest(int nrSellers) {
		Thread[] sellerThreads = new Thread[nrSellers];
		Seller[] sellers = new Seller[nrSellers];
		for (int i = 0; i < nrSellers; ++i) {
			sellers[i] = new Seller(AuctionServer.getInstance(), "Seller" + i, 100, 50, i);
			sellerThreads[i] = new Thread(sellers[i]);
			sellerThreads[i].start();
		}

		for (int i = 0; i < nrSellers; ++i) {
			try {
				sellerThreads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		List<Item> submitedItemList = AuctionServer.getInstance().getAllItemsAndIds();
		assertFalse("submit exceed server capacity!", submitedItemList.size() > AuctionServer.serverCapacity);
		for (int i = 0; i < submitedItemList.size(); i++) {
			assertEquals("submit has synchornical problem!", submitedItemList.get(i).listingID(), i);
			assertFalse("submit Invalid!", submitedItemList.get(i).listingID() == -1);
		}
	}


	@Test
	public void submitTest() {
		submitItemTest(50);
	}

	@Test
	public void getItemsTest() {
		AuctionServer as =new AuctionServer();
		as.submitItem("seller1", "item3", 10, 100);
		as.submitItem("seller2", "item2", 10, 50);
		as.submitItem("seller3", "item1", 10, 100);
		List<Item> itemList1 = as.getItems();
		
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Item> itemList2 = as.getItems();
		assertEquals(3, itemList1.size());
		assertEquals(2, itemList2.size());
	}
	
	
	@Test
	public void bidtest() {
		AuctionServer ac = new AuctionServer();
		ac.submitItem("seller1", "item3", 10, 100);
		ac.submitItem("seller2", "item2", 10, 50);
		ac.submitItem("seller3", "item1", 10, 150);
		ac.submitBid("bidder1", 0, 11);
		ac.submitBid("bidder2", 0, 12);
		ac.submitBid("bidder3", 0, 13);
		ac.submitBid("bidder3", 0, 14);
		ac.submitBid("bidder1", 0, 13);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ac.submitBid("bidder4", 0, 14);
		ac.submitBid("bidder1", 2, 13);
		assertEquals(13, ac.itemPrice(0));
		assertEquals(1, ac.checkBidStatus("bidder3", 0));
		assertEquals(2, ac.checkBidStatus("bidder1", 2));
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(1, ac.checkBidStatus("bidder1", 2));
	}
	
	@Test
	public  void mainTest() {
		int nrSellers = 1000;
		int nrBidders = 1000;

		Thread[] sellerThreads = new Thread[nrSellers];
		Thread[] bidderThreads = new Thread[nrBidders];
		Seller[] sellers = new Seller[nrSellers];
		Bidder[] bidders = new Bidder[nrBidders];
		AuctionServer as = new AuctionServer();
		// Start the sellers
		for (int i = 0; i < nrSellers; ++i) {
			sellers[i] = new Seller(as, "Seller" + i, 100, 150, i);
			sellerThreads[i] = new Thread(sellers[i]);
			sellerThreads[i].start();
		}

		// Start the buyers
		for (int i = 0; i < nrBidders; ++i) {
			bidders[i] = new Bidder(as, "Buyer" + i, 1000, 20, 150, i);
			bidderThreads[i] = new Thread(bidders[i]);
			bidderThreads[i].start();
		}

		// Join on the sellers
		for (int i = 0; i < nrSellers; ++i) {
			try {
				sellerThreads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Join on the bidders
		for (int i = 0; i < nrBidders; ++i) {
			try {
				bidderThreads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// TODO: Add code as needed to debug
		int sum = 0;
		for (int i = 0; i < nrBidders; ++i) {
			sum += bidders[i].cashSpent();
			assertFalse("exceed the server items capacity!",
					bidders[i].mostItemsAvailable() > AuctionServer.serverCapacity);
		}
		assertFalse("the cashSpent dismatch with revenue !", sum != as.revenue());
		System.out.println(" total item sold = " + as.soldItemsCount());
		List<Item> biddedList = as.getBiddedItemList();
		
		System.out.println(" the revenue = " + as.revenue());
		List<Item> submitedItemList = as.getAllItemsAndIds();
		System.out.println(" biddedList.size() = " + biddedList.size());
		
		for (int i = 0; i < submitedItemList.size(); i++) {
			assertEquals("submit has synchornical problem!", submitedItemList.get(i).listingID(), i);
			assertFalse("submit Invalid!", submitedItemList.get(i).listingID() == -1);
		}
		assertEquals("bid has synchornical problem!", biddedList.size(), as.soldItemsCount());
		
	}
	/**
	 * ----------------------------------------------------------------------------------------------------------
	 * ----------------------------------------------Test Cases end-------------------------------------------------
	 * ---------------------------------------------------------------------------------------------------------
	 */

}