package ling.cmpe283project1;

import java.util.ArrayList;

import com.vmware.vim25.VirtualMachineMovePriority;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

public class VhostManager {
	
     public static boolean rebootVhost(HostSystem vhost) throws Exception{ //need to test
    	 //restart selected vHost
    	System.out.println("try to reboot" + vhost.getName() + " now....");
    	Task task= vhost.rebootHost(true);
    	if (task.waitForTask() == Task.SUCCESS) {
  		  System.out.println(vhost.getName() + "is rebooted");
  		  return true;
    	}
  		else {
  			System.out.println(vhost.getName() + " failed to reboot"); 
  			return false;
  		}
     }
     
     public static HostSystem findVhostByName(String vhostname) throws Exception{
		 Folder vHostFolder = VcenterManager.theVcenter.getHostFolder();
		 HostSystem vhost =
					(HostSystem) new InventoryNavigator(vHostFolder).searchManagedEntity("HostSystem", vhostname);
		 if (vhost == null)  throw new Exception("host is null");
	
		 return vhost;
    	 
     }
     
     public static void createNewVhost(){
    	 
     }
     
     public static VirtualMachine[] findAllVmsInVhost(HostSystem vhost) throws Exception{  //may not need
    		//find all virtual machines in selected vHost
    	    //may need to figure out a way to find all vms when this vHost is down.
    	       VirtualMachine[] vms = vhost.getVms();  			
    			return vms;
    }
     
     public static void migrateVmsToNewVhost(HostSystem oldvhost, HostSystem newvhost) //need to test
    		 throws Exception{  
    	 VirtualMachine[] vms = oldvhost.getVms();  //find all vms in the old vHost; //may not work
    	 for(VirtualMachine vm: vms){
    		 Task task = vm.migrateVM_Task(null, newvhost, VirtualMachineMovePriority.highPriority, null);
    		 if (task.waitForTask() == Task.SUCCESS) {
    			System.out.println("Migrate "+vm.getName() + " successfully");
    		 }
    		 else System.out.println("Migrate "+vm.getName() + " failed");
    	 }
    	 
     }
     

     
     
     //optional
     public static void createSnapshot(HostSystem vhost){
    	 //create a snapshot for selected vHost in the admin server (130.65.132.14) ??
     }
     
     //optional
     public static void recoverVhostFromSnapshot(HostSystem oldvhost, HostSystem newvhost){
    	 //recover vHost by snapshot???
     
     }
     
}
