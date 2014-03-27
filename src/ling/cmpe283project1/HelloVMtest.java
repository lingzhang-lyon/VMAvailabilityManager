package ling.cmpe283project1;

import java.net.URL;
import java.util.ArrayList;

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
		//testSetPowerOn(mes); //test success
		
		//testGetVhostofVM(mes); //need to test //failed
		//testfindAllVMsinVcenter(); //test success
		//testfindAllVhostInVcenter();//test success
		//testFindFirstAvailableHost(); //test success
	
		//testRebootVhost(mes); //failed!!!!
		//testRemoveVhostFromVcenter();  //failed!!!!
		testAddBackupVhostToVcenter();
		
//        testRevertToSnapshot(mes);  //have problem
//        ManagedEntity[] mesnew = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
//		testSetPowerOn(mesnew); //have problem
        
		
        if (PingManager.pingByIP("130.65.132.159")) 
        	System.out.println("\nping host159 successful");
        if (PingManager.pingByIP("130.65.132.151")) 
        	System.out.println("\nping host151 successful");

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
     
     public static HostSystem testGetVhostofVM(ManagedEntity[] mes) throws Exception { //need to test
    	 VirtualMachine vm = (VirtualMachine) mes[0];

    	 String vhostname=vm.getGuest().hostName;
    	 VcenterManager.setVcenter();
    	 HostSystem parenthost = VhostManager.findVhostByName(vhostname);
    	 String hostip=parenthost.getConfig().getNetwork().getVnic()[0].getSpec().getIp().getIpAddress();
    	 System.out.println("\n VM "+ vm.getName() +" belong to "+ vm.getGuest().hostName);
    	 System.out.println("\n VM "+ vm.getName() +" belong to "+ hostip);
    	 return parenthost;
     }
	
    public static void testfindAllVMsinVcenter() throws Exception{
    	VcenterManager.setVcenter();
    	System.out.println("vCenter is : " + VcenterManager.theVcenter.getName());   	
    	VirtualMachine[] vms= VcenterManager.findAllVmsInVcenter();
    	for(VirtualMachine vm :vms){
    		System.out.println(vm.getName() + " hello!");
    	}	
    }
    
    public static void testFindFirstAvailableHost() throws Exception{
    	VcenterManager.setVcenter();
    	System.out.println("vCenter is : " + VcenterManager.theVcenter.getName());
    	HostSystem vhost= VcenterManager.findFirstAvailableVhost();
    	String ip=PingManager.getVhostIP(vhost);
    	System.out.println(vhost.getName() + ": hello! I am the first available vHost");
    	System.out.println("My IP is " + ip);
    }
    
    public static void testfindAllVhostInVcenter() throws Exception {
    	VcenterManager.setVcenter();
    	System.out.println("vCenter is : " + VcenterManager.theVcenter.getName());
    	ArrayList<HostSystem> testvHosts=VcenterManager.findandUpdateVhostsInVcenter();
    	for(HostSystem vhost : testvHosts){   		
    			System.out.println(vhost.getName() + " is alive, hello.");  		
    	}
    }
    
    public static void testRebootVhost() throws Exception {
    	VcenterManager.setVcenter();
    	System.out.println("vCenter is : " + VcenterManager.theVcenter.getName());
    	ArrayList<HostSystem> testvHosts=VcenterManager.findandUpdateVhostsInVcenter();
    	for(HostSystem vhost : testvHosts){
    		if (!PingManager.pingVhost(vhost)) VhostManager.rebootVhost(vhost);
    		else
    			System.out.println(vhost.getName() + " is alive, no need to reboot.");  		
    	}
    }
    
    public static void testRemoveVhostFromVcenter() throws Exception {
    	VcenterManager.setVcenter();
    	System.out.println("vCenter is : " + VcenterManager.theVcenter.getName());
    	ArrayList<HostSystem> testvHosts=VcenterManager.findandUpdateVhostsInVcenter(); //wrong, you will just get live vhost!
    	for(HostSystem vhost : testvHosts){
    		if (!PingManager.pingVhost(vhost)) VcenterManager.removeVhostFromVcenter(vhost);
    		else
    			System.out.println(vhost.getName() + " is alive, no need to remove.");  		
    	}
    }
    
    public static void testAddBackupVhostToVcenter() throws Exception{
    	VcenterManager.setVcenter();
    	VcenterManager.setBackupVhostConnects();
    	//before add
    	System.out.println("vCenter is : " + VcenterManager.theVcenter.getName());
    	ArrayList<HostSystem> testvHosts=VcenterManager.findandUpdateVhostsInVcenter();
    	for(HostSystem vhost : testvHosts){   		
    			System.out.println(vhost.getName() + " is alive, hello.");  		
    	}
    	//add
    	VcenterManager.addBackupVhostToVcenter(VcenterManager.backupVhostConnects);
    	
    	//after add
    	System.out.println("vCenter is : " + VcenterManager.theVcenter.getName());
    	ArrayList<HostSystem> newvHosts=VcenterManager.findandUpdateVhostsInVcenter();
    	for(HostSystem vhost : newvHosts){   		
    			System.out.println(vhost.getName() + " is alive, hello.");  		
    	}
    	
    
    }

}