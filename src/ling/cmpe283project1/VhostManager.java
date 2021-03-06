package ling.cmpe283project1;

import java.net.URL;

import com.vmware.vim25.VirtualMachineMovePriority;
import com.vmware.vim25.mo.ComputeResource;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

public class VhostManager {
	

	
     public static HostSystem findVhostByNameInVcenter(String vhostname) throws Exception{
    	 //this vhost name is like ip address
    	 //System.out.println("finding vhost " + vhostname + " now...");
    	 if (VcenterManager.theVcenter== null)  throw new Exception("vCenter is not defined");
    	 Folder vHostFolder = VcenterManager.theVcenter.getHostFolder();
		 HostSystem vhost =
					(HostSystem) new InventoryNavigator(vHostFolder).searchManagedEntity("HostSystem", vhostname);
		 
	     //if(vhost!=null) System.out.println( vhostname + " is found ");
	     //else System.out.println( vhostname + " is not found ");
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
    	
         System.out.println("finding vHost as Vm in admin server 14 now...");
    	 //initial the map to match the vhost name in admin server
    	 AvailabilityManager.setVhostNameInAdminMap();
    	 
    	 // get the vhost name in admin server
    	 
    	String vhostnameIn14 = VcenterManager.vhostNameInAdminMap.get(vhostname);
    	
 		URL url = new URL(AvailabilityManager.ADMINSERVERURL);
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
    	 //pre condition: the vhost should be normal, could be ping through
    	 //create a snapshot for selected vHost in the admin server (130.65.132.14) 
    	 String vhostname=vhost.getName();
    	 System.out.println("\nTrying to create snapshot for " + vhostname + " now...");
    	 
    	 //make sure vhost could be ping through
    	 if( !PingManager.pingVhost(vhost) ) {
    		 System.out.println("could not ping through the vHost, it's abnormal, will not create snapshot");
    	     return;		 
    	 }   	 
    	 
    	 //find vhost as Vm in admin server
    	
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
     
     
     public static void recoverVhostFromSnapshotAndPoweron(String vhostname) throws Exception{
    	 //recover vHost by snapshot
    	System.out.println("\nstart to try to revert " + vhostname + " to snapshot...." );
    	//find vhost as Vm in admin server 
    	VirtualMachine vhostAsVm=VhostManager.findVhostAsVMInAdminServerByName(vhostname);
 	
 		// revert vhost to snapshot
 		Task revertTask = vhostAsVm.revertToCurrentSnapshot_Task(null);
		  System.out.println("reverting " + vhostAsVm.getName() + " to snapshot now...." );
		  if (revertTask.waitForTask() == Task.SUCCESS) 
				System.out.println("vHost "+vhostAsVm.getName()+" has been reverted to recent snapshot.");			
		  else 
				System.out.println("fail to recover vHost "+vhostAsVm.getName());
		  
		  //power on vhost and vms
		  System.out.println("trying to poweron the vhost and all the VMs on it now.....");
		  if(VmManager.setPowerOn(vhostAsVm)){			  
			  // make sure the powering on process finished
			  HostSystem vhost=VhostManager.findVhostByNameInVcenter(vhostname);
				boolean hasPingThrough=false;
				do {  
					if(PingManager.pingVhost(vhost)) hasPingThrough=true;			
					} while (hasPingThrough==false);			
				System.out.println(vhostname + " is powered on and could ping through now");
				
				///make sure will not end until all the VM is powered on !!!!
				int WAITTIME=60000;
				do {
					System.out.println("after revert vhost, wait for "+ WAITTIME/1000+"s, then power on all the VMs in this vhost......");
					Thread.sleep(WAITTIME);
				} while (!VhostManager.setAllVmsInVhostPowerOnAndConfigure(vhost));
				
				
		  }
 				 
 		
     
     }


	
	public static boolean setAllVmsInVhostPowerOnAndConfigure(HostSystem vhost) throws Exception{
		boolean allsuccess=false;
		VirtualMachine[] vms= VhostManager.findAllVmsInVhost(vhost);
		for(VirtualMachine vm :vms) {
			if(VmManager.setPowerOn(vm)){			  
				  if (VmManager.makeSureVmIpConfigured(vm))		
					System.out.println(vm.getName() + " is powered on and its ip is configured now");
				  allsuccess=true;
			}
			else allsuccess=false;
		}
		return allsuccess;
		
	}


     
}
