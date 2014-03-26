package ling.cmpe283project1;

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
		System.out.println(vm.getName() + " statics test ");
	}
	
	
	
		
}
