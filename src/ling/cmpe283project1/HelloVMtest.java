package ling.cmpe283project1;

import java.net.URL;

import com.vmware.vim25.*;
import com.vmware.vim25.mo.*;

public class HelloVMtest {   
	//private static final String USERNAME = "administrator";
	//private static final String PASSWORD = "12!@qwQW";

	
	public static void main(String[] args) throws Exception {   
		
		long start = System.currentTimeMillis();
		URL url = new URL("https://130.65.132.159/sdk");
		ServiceInstance si = new ServiceInstance(url, "root", "12!@qwQW", true);
		long end = System.currentTimeMillis();
		System.out.println("time taken:" + (end-start));
		Folder rootFolder = si.getRootFolder();
		String name = rootFolder.getName();
		System.out.println("root:" + name);
		ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
		if(mes==null || mes.length ==0)
		{
			return;
		}
		

		
        //testPingVM(mes);  //test success
        //testStatics(mes); //test success
        //testSnapshot(mes); //test success
		testSetPowerOn(mes); //test success
		
		testGetVhostofVM(mes); //need to test
        testRevertToSnapshot(mes);  //have problem
        ManagedEntity[] mesnew = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
		testSetPowerOn(mesnew); //have problem
        
		
        if (PingManager.pingByIP("130.65.132.159")) 
        	System.out.println("\nping host successful");

		si.getServerConnection().logout();
		
	}
	
	public static void testPingVM(ManagedEntity[] mes) throws Exception{
		
		for(int i=0; i<mes.length; i++){
				VirtualMachine vm = (VirtualMachine) mes[i]; 
				System.out.println("\n" + vm.getName() + ": hello, my status is " + vm.getGuestHeartbeatStatus());
				if(PingManager.pingVM(vm)){  // test for ping function
					System.out.println("ping " + vm.getName() + " successful");
				}
				else System.out.println("ping " + vm.getName() + " failed");
		}
				
	}
	
	public static void testStatics(ManagedEntity[] mes) throws Exception{
		for(int i=0; i<mes.length; i++){
			VirtualMachine vm = (VirtualMachine) mes[i]; 
			System.out.println("");
			VmManager.printStatics(vm);
		}
	}
	
     public static void testSnapshot(ManagedEntity[] mes) throws Exception{		
		for(int i=0; i<mes.length; i++){
				VirtualMachine vm = (VirtualMachine) mes[i]; 
				System.out.println("\n" + vm.getName() + ": hello, my status is " + vm.getGuestHeartbeatStatus());
				
				VmManager.createSnapshot(vm);				
		}
				
	 }
     
     public static void testRevertToSnapshot(ManagedEntity[] mes) throws Exception {
    	  VirtualMachine vm = (VirtualMachine) mes[0];
    	  System.out.println("\ntry to revert to snapshot" + vm.getName() );
    	  vm.revertToCurrentSnapshot_Task(null);
    	  VmManager.setPowerOn(vm);
     }
     
     public static void testSetPowerOn(ManagedEntity[] mes) throws Exception{	 
    	 VirtualMachine vm = (VirtualMachine) mes[0];
    	 VirtualMachinePowerState vmps = vm.getRuntime().getPowerState();
		 if (vmps==VirtualMachinePowerState.poweredOff ) {
			 VmManager.setPowerOn(vm);
		 }
     }
     
     public static void testGetVhostofVM(ManagedEntity[] mes) throws Exception { //need to test
    	 VirtualMachine vm = (VirtualMachine) mes[0];
    	 System.out.println("\n VM "+ vm.getName() +" belong to "+vm.getParent().getName());
     }
	


}