package ling.cmpe283project1;

import java.net.URL;
import java.util.ArrayList;

import com.vmware.vim25.*;
import com.vmware.vim25.mo.*;

public class TestHelloVM {   
	
	public static void main(String[] args) throws Exception {   

		//testfindVmByNameInVcenter(); //test success
		//testSetVmPoweron();	//test success
		//testfindAllVMsinVcenter(); //test success
        //testPingVM();  //test success
        //testStatics(); //test success 
		
		//testfindAllVhostInVcenter();//test success
		//testFindFirstAvailableHost(); //test success
		//testfindVhostByName();//test success
		//testAddBackupVhostToVcenter(); //test success
		//testRemoveVhostFromVcenter();  //test success	
		//testMigrateToNewVhost(); //test success
		
		//testCreateVhostSnapshot(); //test success
		//testRevertVhostToSnapshot(); //test success
		//testCreateVMSnapshot(); //test success
        //testRevertToSnapshot();  //test success
	
		//testRebootVhost(); //failed!!!! //no need
		//testGetVhostofVmFromMap(); //sucess
		//testUpdateVmListForEachVhost();//success
		

		      
		
        if (PingManager.pingByIP("130.65.132.159")) 
        	System.out.println("\nping host159 successful");
        if (PingManager.pingByIP("130.65.132.151")) 
        	System.out.println("\nping host151 successful");
        if (PingManager.pingByIP("130.65.132.155")) 
        	System.out.println("\nping host155 successful");
        
	
	}
	
	public static void testfindVmByNameInVcenter() throws Exception{
    	VcenterManager.setVcenter();
     	System.out.println("vCenter is : " + VcenterManager.theVcenter.getName());     	
    	 VirtualMachine vm = VmManager.findVmByNameInVcenter("T03-VM02-Lin-Ling");
    	 VirtualMachinePowerState vmps = vm.getRuntime().getPowerState();
    	 System.out.println(vm.getName()+" is found, and it's status is " + vmps);
		 
     }
     
     public static void testSetVmPoweron() throws Exception{ //test restart when power off //success
    	VcenterManager.setVcenter();
     	System.out.println("vCenter is : " + VcenterManager.theVcenter.getName());     	
    	 VirtualMachine vm = VmManager.findVmByNameInVcenter("T03-VM01C-Win-Jiawei");
    	 VirtualMachinePowerState vmps = vm.getRuntime().getPowerState();
    	 System.out.println(vm.getName()+"'s status is " + vmps);
		 if (vmps==VirtualMachinePowerState.poweredOff ) {
			 VmManager.setPowerOn(vm);
		 }
     }
     
     public static void testfindAllVMsinVcenter() throws Exception{
    	VcenterManager.setVcenter();
    	System.out.println("vCenter is : " + VcenterManager.theVcenter.getName());   	
    	VirtualMachine[] vms= VcenterManager.findAllVmsInVcenter();
    	for(VirtualMachine vm :vms){
    		System.out.println(vm.getName() + " hello!");
    	}	
    }
    
    public static void testPingVM() throws Exception{
		VcenterManager.setVcenter();
		System.out.println("vCenter is : " + VcenterManager.theVcenter.getName());   	
		VirtualMachine[] vms= VcenterManager.findAllVmsInVcenter();
		for(VirtualMachine vm :vms){				
				System.out.println("\n" + vm.getName() + ": hello, my status is " + vm.getGuestHeartbeatStatus());
				if(PingManager.pingVM(vm)){  // test for ping function
					System.out.println("ping " + vm.getName() + " successful");
				}
				else System.out.println("ping " + vm.getName() + " failed");
		}
				
	}

	public static void testStatics() throws Exception{		
		VcenterManager.setVcenter();
		System.out.println("vCenter is : " + VcenterManager.theVcenter.getName());   	
		VirtualMachine[] vms= VcenterManager.findAllVmsInVcenter();
		for(VirtualMachine vm :vms){
			System.out.println("");
			VmManager.printStatics(vm);
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
    	HostSystem[] testvHosts=VcenterManager.findandUpdateVhostsInVcenter();
    	for(HostSystem vhost : testvHosts){   		
    			System.out.println(vhost.getName() + " is alive, hello.");  		
    	}
    }
    
    public static void testfindVhostByNameInVcenter() throws Exception {
    	VcenterManager.setVcenter();
    	System.out.println("vCenter is : " + VcenterManager.theVcenter.getName());
    	HostSystem testvhost = VhostManager.findVhostByNameInVcenter("130.65.132.155");
    	System.out.println("vHost " + testvhost.getName() + "has been found");
    	
    }
    
    //	public static void testRebootVhost() throws Exception {  //not needed
//    	VcenterManager.setVcenter();
//    	System.out.println("vCenter is : " + VcenterManager.theVcenter.getName());
//    	ArrayList<HostSystem> testvHosts=VcenterManager.findandUpdateVhostsInVcenter();
//    	for(HostSystem vhost : testvHosts){
//    		if (!PingManager.pingVhost(vhost)) VhostManager.rebootVhost(vhost);
//    		else
//    			System.out.println(vhost.getName() + " is alive, no need to reboot.");  		
//    	}
//    }
    
    public static void testRemoveVhostFromVcenter() throws Exception { //success
    	VcenterManager.setVcenter();
    	System.out.println("vCenter is : " + VcenterManager.theVcenter.getName());
    	//before remove
    	HostSystem[] testvhosts=VcenterManager.findandUpdateVhostsInVcenter();
    	for(HostSystem vhost : testvhosts){   		
			System.out.println(vhost.getName() + " is alive, hello.");  		
    	}
    	//remove
    	HostSystem testvhost= VhostManager.findVhostByNameInVcenter("130.65.132.155");
    	VcenterManager.removeVhostFromVcenter(testvhost);
    	//after remove
    	HostSystem[] aftertestvhosts=VcenterManager.findandUpdateVhostsInVcenter();
    	for(HostSystem vhost : aftertestvhosts){   		
			System.out.println(vhost.getName() + " is alive, hello.");  		
    	}
    	
    		
    }
    
    public static void testAddBackupVhostToVcenter() throws Exception{
    	VcenterManager.setVcenter();
    	VcenterManager.setBackupVhostConnects();
    	//before add
    	System.out.println("vCenter is : " + VcenterManager.theVcenter.getName());
    	HostSystem[] testvHosts=VcenterManager.findandUpdateVhostsInVcenter();
    	for(HostSystem vhost : testvHosts){   		
    			System.out.println(vhost.getName() + " is alive, hello.");  		
    	}
    	//add
    	VcenterManager.addBackupVhostToVcenter(VcenterManager.backupVhostConnects);
    	
    	//after add
    	System.out.println("vCenter is : " + VcenterManager.theVcenter.getName());
    	HostSystem[] newvHosts=VcenterManager.findandUpdateVhostsInVcenter();
    	for(HostSystem vhost : newvHosts){   		
    			System.out.println(vhost.getName() + " is alive, hello.");  		
    	}
    	
    
    }

	public static void testCreateVMSnapshot() throws Exception{		
    	VcenterManager.setVcenter();
    	System.out.println("vCenter is : " + VcenterManager.theVcenter.getName());   	
    	VirtualMachine[] vms= VcenterManager.findAllVmsInVcenter();
    	for(VirtualMachine vm :vms){
				System.out.println("\n" + vm.getName() + ": hello, my status is " + vm.getGuestHeartbeatStatus());				
				VmManager.createVmSnapshot(vm);				
		}
				
	 }

	public static void testRevertVMToSnapshot() throws Exception {
		VcenterManager.setVcenter();
     	System.out.println("vCenter is : " + VcenterManager.theVcenter.getName());     	
    	 VirtualMachine vm = VmManager.findVmByNameInVcenter("T03-VM02-Lin-Ling");	  
		  VmManager.revertToSnapshotAndPoweron(vm);
	 }
	
	public static void testMigrateToNewVhost() throws Exception {
		VcenterManager.setVcenter();
		HostSystem oldvhost=VhostManager.findVhostByNameInVcenter("130.65.132.159");
		HostSystem newvhost=VhostManager.findVhostByNameInVcenter("130.65.132.151");		
		VhostManager.migrateVmsToNewVhost(oldvhost,newvhost);
	}
	
	public static void testCreateVhostSnapshot() throws Exception{
		VcenterManager.setVcenter();
		HostSystem vhost=VhostManager.findVhostByNameInVcenter("130.65.132.155");
		VhostManager.createVhostSnapshot(vhost);
		
	}
	
	public static void testRevertVhostToSnapshot() throws Exception{
		VcenterManager.setVcenter();
		HostSystem vhost=VhostManager.findVhostByNameInVcenter("130.65.132.159");
		VhostManager.recoverVhostFromSnapshot(vhost);
		
	}

	public static void testUpdateVmListForEachVhost() throws Exception{
		VcenterManager.setVcenter();
	 	System.out.println("vCenter is : " + VcenterManager.theVcenter.getName());
	 	VcenterManager.updateVmNameToVhostNameMap();
	 	System.out.println(VcenterManager.vmNameToVhostNameMap);
	 	 
	}
	
	public static void testGetVhostofVmFromMap() throws Exception { //success
		 VcenterManager.setVcenter();
	 	 System.out.println("vCenter is : " + VcenterManager.theVcenter.getName()); 
	 	 VcenterManager.updateVmNameToVhostNameMap();
		 VirtualMachine vm = VmManager.findVmByNameInVcenter("T03-VM02-Lin-Lan");
		 String vmname=vm.getName();	 
		 String vhostname=VmManager.findVhostNameByVmName(vmname);
		 System.out.println("vHost is "+ vhostname);
		 HostSystem parenthost = VhostManager.findVhostByNameInVcenter(vhostname);
		 String hostip=parenthost.getConfig().getNetwork().getVnic()[0].getSpec().getIp().getIpAddress();
		 System.out.println("VM "+ vm.getName() +" belong to "+ hostip);

	 }
	
	
	

}