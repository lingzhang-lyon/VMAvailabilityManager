package ling.cmpe283project1;

public class Test4AvailabilityManager {  //test threads
	
   public static void main(String[] args) throws Exception {
	    System.out.println("Main ThreadId: " + Thread.currentThread().getId());
		AvailabilityManager.setAvailabilityManager();
		
		new Thread(new BackupVmThread()).start();  
		new Thread(new BackupVhostThread()).start();
		new Thread(new MonitorThread()).start();
		
   }

}
