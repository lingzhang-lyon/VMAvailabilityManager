package ling.cmpe283project1;

import com.vmware.vim25.VirtualMachineQuickStats;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

public class VmManager {
	
	public static void createVmSnapshot(VirtualMachine vm) throws Exception {
		// create a snapshot for selected virtual machine
		//pre condition:  vm is noramal and could be ping through
		
		System.out.println("\nTrying to create snapshot for " + vm.getName() + " now...");
		
		//first make sure vm could be ping through
		if( !PingManager.pingVM(vm) ) {
			if( !PingManager.pingVM(vm) ){//ping for twice
	   		 System.out.println("could not ping through VM, will not create snapshot");
	   	     return;
			}
	   	 }
		
		String snapshotname = vm.getName() + "_SnapShot";
		String description = "new snapshot of " + vm.getName();
		
		Task task = vm.createSnapshot_Task(snapshotname, description, false, false);
		System.out.println("creating " + snapshotname + " now....");
		if (task.waitForTask() == Task.SUCCESS)
		System.out.println(snapshotname + " was created.");
		else System.out.println(snapshotname + " create failed.");
	}
	

	
	public static void revertToSnapshotAndPoweron(VirtualMachine vm) throws Exception {
		//recover the virtual machine from snapshot
		  Task revertTask = vm.revertToCurrentSnapshot_Task(null);
		  System.out.println("\ntrying to revert " + vm.getName() + " to snapshot...." );
		  if (revertTask.waitForTask() == Task.SUCCESS) 
				System.out.println("VM "+vm.getName()+" has been reverted to recent snapshot.");			
		  else 
				System.out.println("fail to recover VM "+vm.getName());
		  if (VmManager.setPowerOn(vm)){
			  if (VmManager.makeSureVmIpConfigured(vm))		
					System.out.println(vm.getName() + " is powered on and its ip is configured now");
		  }
			  
	}	
	
	public static void printStatics(VirtualMachine vm){
		System.out.println("\nVM " +vm.getName() + " statics --------------- ");		
		System.out.println("Name: " + vm.getName());
		System.out.println("Guest OS: "
		+ vm.getSummary().getConfig().guestFullName);
		System.out.println("VM Version: " + vm.getConfig().version);
		System.out.println("CPU: " + vm.getConfig().getHardware().numCPU
		+ " vCPU");
		System.out.println("Memory: " + vm.getConfig().getHardware().memoryMB
		+ " MB");
		System.out.println("IP Addresses: " + vm.getGuest().getIpAddress());
		System.out.println("State: " + vm.getGuest().guestState);

		//if (!vm.getGuest().guestState.equals("notRunning")) {
		//PerfMgr.printPerf(vm); // print real time performance
		System.out.println("Data from VirtualMachineQuickStats: ");
		VirtualMachineQuickStats vqs = vm.getSummary().getQuickStats();
		System.out.println( "OverallCpuUsage: " + vqs.getOverallCpuUsage() + " MHz");
		System.out.println( "GuestMemoryUsage: " + vqs.getGuestMemoryUsage() + " MB");
		System.out.println( "ConsumedOverheadMemory: " + vqs.getConsumedOverheadMemory() + " MB");
		System.out.println( "FtLatencyStatus: " + vqs.getFtLatencyStatus());
		System.out.println( "GuestHeartbeatStatus: " + vqs.getGuestHeartbeatStatus());
		System.out.println( "End of statics ---------------------- ");
		//}
	}
	
	public static boolean setPowerOn(VirtualMachine vm) throws Exception {
		Task task = vm.powerOnVM_Task(null);
		System.out.println("Try to start poweron " + vm.getName() + " now, please wait...");
		if (task.waitForTask() == Task.SUCCESS) {
		  System.out.println(vm.getName() + " poweron started, still powering on and configureing now...");
		  return true;
		}
		
		else {
			System.out.println(vm.getName() + " failed to poweron");
			return false;
		}

	}
	
	public static boolean makeSureVmIpConfigured(VirtualMachine vm) throws Exception{
		boolean VMhasPingThrough=false;
		do {  
			if(PingManager.pingVM(vm)) VMhasPingThrough=true;
			Thread.sleep(10000);  // sleep instead of busy waiting
			} while (VMhasPingThrough==false);	
		return VMhasPingThrough;
	}
	
	public static VirtualMachine findVmByNameInVcenter(String vmname) throws Exception{
		// this vmname is the actual name not like ip address
		if (VcenterManager.theVcenter== null)  throw new Exception("vCenter is not defined");
		//System.out.println("Searching for VM " +vmname+ " now....");
		Folder vmFolder = VcenterManager.theVcenter.getVmFolder();
		VirtualMachine vm =
					(VirtualMachine) new InventoryNavigator(vmFolder).searchManagedEntity("VirtualMachine", vmname);
		if (vm== null)  throw new Exception("vm is not found");
		//else System.out.println("VM " +vmname+ " is found");
		return vm;
	}
	
	public static String findVhostNameByVmName(String vmname) throws Exception{
		if (VcenterManager.theVcenter== null)  throw new Exception("vCenter is not defined");
		if (VcenterManager.vmNameToVhostNameMap== null)  throw new Exception("vmNameToVhostNameMap is not set up");
		if (VcenterManager.vmNameToVhostNameMap.containsKey(vmname)) 
			return VcenterManager.vmNameToVhostNameMap.get(vmname);
		else {
			System.out.println(vmname+"'s vhost is not found");
			return null;		
		}
	}
}
