

package ling.cmpe283project1;


public class Test3AvailabilityManager

{   


	
	public static void main(String[] args) throws Exception {
		
		AvailabilityManager.setAvailabilityManager();  //will set up VcenterManager 
		
		//we can use different thread for monitor and backup
		//monitor();
		//AvailabilityManager.backupVMPeriodically(60000);  //test success
		//AvailabilityManager.backupVhostPeriodically(60000); //test success
		testfailover();//need to test
		
	}
	
    
	public static void testfailover() throws Exception{
		//
		AvailabilityManager.failOver("T03-VM02-Lin-Ling");
	}


}
