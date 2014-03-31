package ling.cmpe283project1;

import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.VirtualMachine;

public class BackupAndStaticsThread implements Runnable {
	//back up the all the vm in the vCenter every interval time
	@Override
	public void run() {
		System.out.println("BackupVmThread ThreadId: " + Thread.currentThread().getId());
		try {
			
			while (AvailabilityManager.AllowToBackup){
				if (AvailabilityManager.MONITORBACKUPONEVHOST==true) BackupAndStaticsForOneVhostForOneTime();
				else BackupAndStaticsForAllVhostInVcenterForOneTime();
					
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void BackupAndStaticsForOneVhostForOneTime() throws Exception {
		
		HostSystem vhost=VhostManager.findVhostByNameInVcenter( AvailabilityManager.VHOSTNAME);
		VirtualMachine[] vms = VhostManager.findAllVmsInVhost(vhost);
		//print vm statics
		System.out.println("\nStart to print VMs statics now...");
		for (VirtualMachine vm : vms) {
			VmManager.printStatics(vm);
		}
		//backup vms
		System.out.println("\nStart to backup VMs now...");
		for (VirtualMachine vm : vms) {
			VmManager.createVmSnapshot(vm);
		}
		
		//backup vhost		
		System.out.println("\nStart to backup vhosts now...");	
		VhostManager.createVhostSnapshot(vhost);
		
		
		System.out.println("\nFinished VMs print and backup, and vhosts backup. "
		  + AvailabilityManager.INTERVAL/1000 +"sec later will back up again, now waiting....");							
		
		Thread.sleep(AvailabilityManager.INTERVAL);	
	}
	
	public void BackupAndStaticsForAllVhostInVcenterForOneTime() throws Exception {
		VirtualMachine[] vms = VcenterManager.findAllVmsInVcenter();
		//print vm statics
		System.out.println("\nStart to print VMs statics now...");
		for (VirtualMachine vm : vms) {
			VmManager.printStatics(vm);
		}
		//backup vms
		System.out.println("\nStart to backup VMs now...");
		for (VirtualMachine vm : vms) {
			VmManager.createVmSnapshot(vm);
		}
		
		//backup vhost
		HostSystem[] vhosts = VcenterManager.findandUpdateVhostsInVcenter();
		System.out.println("\nStart to backup vhosts now...");
		for (HostSystem vhost : vhosts) {
			VhostManager.createVhostSnapshot(vhost);
		}
		
		System.out.println("\nFinished VMs print and backup, and vhosts backup. "
		  + AvailabilityManager.INTERVAL/1000 +"sec later will back up again, now waiting....");							
		
		Thread.sleep(AvailabilityManager.INTERVAL);	
	}
	
}
