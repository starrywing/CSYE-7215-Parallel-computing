
public class DisplayMessage implements Runnable {
   private String message;
   private int counter = 0; 
   public DisplayMessage(String message) {
      this.message = message;
   }
   
   public void run() {
      while(counter<10) {
    	  counter++;
         System.out.println(message+"--- "+counter);
      }
   }
   
   public synchronized void yifan() {}
   
   public  void yifan2() {
	   synchronized(this) {}
   }
}