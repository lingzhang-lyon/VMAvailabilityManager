package ling.cmpe283project1;

import com.vmware.vim25.VirtualMachineQuickStats;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;
import com.vmware.vim25.mo.VirtualMachineSnapshot;

public class VmManager {

	public static void createSnapshot(VirtualMachine vm) throws Exception {
		// create a snapshot for selected virtual machine
		String snapshotname = vm.getName() + "_SnapShot";
		String description = "new snapshot of " + vm.getName();
		
		Task task = vm.createSnapshot_Task(snapshotname, description, false, false);
		if (task.waitForTask() == Task.SUCCESS)
		System.out.println(snapshotname + " was created.");
		else System.out.println(snapshotname + " create failed.");
	}
	
	public static void removeSnapshot(VirtualMachine vm, String snapshotname){
			
	}
	
	public static void revertToSnapshot(VirtualMachine vm, String snapshotname) throws Exception {
		//recover the virtual machine from snapshot
	}	
	
	public static void printStatics(VirtualMachine vm){
		System.out.println(vm.getName() + " statics --------------- ");		
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
		//}
	}
	
	public static void setPowerOn(VirtualMachine vm) throws Exception {
		Task task = vm.powerOnVM_Task(null);
		System.out.println("we are powerring on " + vm.getName() + " now, please wait...");
		if (task.waitForTask() == Task.SUCCESS) 
		  System.out.println(vm.getName() + "is powered on");
		else System.out.println(vm.getName() + " failed to power on");
	}
	
		
}
