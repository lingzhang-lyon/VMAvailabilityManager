package ling.cmpe283project1;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import com.vmware.vim25.HostVMotionCompatibility;
import com.vmware.vim25.VirtualMachineMovePriority;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.mo.ComputeResource;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

public class VhostManager {
	

	
     public static HostSystem findVhostByNameInVcenter(String vhostname) throws Exception{
    	 //this vhost name is like ip address
    	 if (VcenterManager.theVcenter== null)  throw new Exception("vCenter is not defined");
    	 Folder vHostFolder = VcenterManager.theVcenter.getHostFolder();
		 HostSystem vhost =
					(HostSystem) new InventoryNavigator(vHostFolder).searchManagedEntity("HostSystem", vhostname);
		 
	
		 return vhost;
    	 
     }
     
     public static VirtualMachine[] findAllVmsInVhost(HostSystem vhost) throws Exception{ //may not necessary 
    		//find all virtual machines in selected vHost
    	    //may need to figure out a way to find all vms when this vHost is down.
    	 VirtualMachine[] vms= vhost.getVms();  
		 return vms;
    }
     
     public static void migrateVmsToNewVhost(HostSystem oldvhost, HostSystem newvhost) //need to test VirtualMachinePowerState.poweredOff
    		 throws Exception{  
    	 VirtualMachine[] vms = oldvhost.getVms();  //find all vms in the old vHost;
    	 for(VirtualMachine vm: vms){
    		 ComputeResource cr = (ComputeResource) newvhost.getParent();
    		 //need to check two host system compatibility
    		    
    		 Task task = vm.migrateVM_Task(cr.getResourcePool(), newvhost, VirtualMachineMovePriority.highPriority, null);
    		 System.out.println("Migrating "+vm.getName() + " now......");
    		 if (task.waitForTask() == Task.SUCCESS) {
    			System.out.println("Migrate "+vm.getName() + " successfully");
    		 }
    		 else System.out.println("Migrate "+vm.getName() + " failed");
    	 }
    	 
     }
     

     public static VirtualMachine findVhostAsVMInAdminServerByName(String vhostname) throws Exception {
    	//initial the map to match the vhost name in admin server
    	 VcenterManager.setVhostNameIn14Map();
    	 
    	 // get the vhost name in admin server
    	 
    	String vhostnameIn14 = VcenterManager.vhostNameIn14Map.get(vhostname);
    	
 		URL url = new URL("https://130.65.132.14/sdk");
 		ServiceInstance si = new ServiceInstance(url, "administrator", "12!@qwQW", true);
 		Folder rootFolder = si.getRootFolder();
 		String name = rootFolder.getName();
 		System.out.println("root:" + name);
 		VirtualMachine vhostAsVm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine",vhostnameIn14);
 		
 		if(vhostAsVm==null) System.out.println(vhostname + " is null"); 
 		else System.out.println(vhostname + " founded in admin server"); 
		
		
 		return vhostAsVm;
     }
     
     public static void createVhostSnapshot(HostSystem vhost) throws Exception{ 
    	 //create a snapshot for selected vHost in the admin server (130.65.132.14) 
    	 //find vhost as Vm in admin server
    	 String vhostname=vhost.getName();
    	 VirtualMachine vhostAsVm=findVhostAsVMInAdminServerByName(vhostname);
 		
 		//start create snapshot
 		
 		String snapshotname = vhostAsVm.getName() + "_SnapShot";
 		
 		String description = "new snapshot of " + vhostAsVm.getName();
 		
 		Task task = vhostAsVm.createSnapshot_Task(snapshotname, description, false, false);
 		System.out.println("createing " + snapshotname + " now...");
 		if (task.waitForTask() == Task.SUCCESS)
 		System.out.println(snapshotname + " was created.");
 		else System.out.println("vhost" + vhost.getName() + " snapshot create failed.");
 		
 		//si.getServerConnection().logout();
 		
 		
     }
     
     
     public static void recoverVhostFromSnapshot(String vhostname) throws Exception{
    	 //recover vHost by snapshot
    	 
    	//find vhost as Vm in admin server 
    	VirtualMachine vhostAsVm=findVhostAsVMInAdminServerByName(vhostname);
 	
 		// revert vhost to snapshot
 		Task revertTask = vhostAsVm.revertToCurrentSnapshot_Task(null);
		  System.out.println("\ntrying to revert " + vhostAsVm.getName() + " to snapshot...." );
		  if (revertTask.waitForTask() == Task.SUCCESS) 
				System.out.println("vHost "+vhostAsVm.getName()+" has been reverted to recent snapshot.");			
		  else 
				System.out.println("fail to recover vHost "+vhostAsVm.getName());
		  VmManager.setPowerOn(vhostAsVm);
 				 
		 // si.getServerConnection().logout();
 		
     
     }

	public static boolean rebootVhost(HostSystem vhost) throws Exception{ //may not need
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


     
}
