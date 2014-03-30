package ling.cmpe283project1;

import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.VirtualMachine;

public class BackupThread implements Runnable {
	//back up the all the vm in the vCenter every interval time
	private int INTERVAL=300000; //5min
	
	@Override
	public void run() {
		System.out.println("BackupVmThread ThreadId: " + Thread.currentThread().getId());
		try {
			
			while (AvailabilityManager.AllowToBackup){
				
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
				
				System.out.println("\nFinished VMs print and backup, and vhosts backup. "+ INTERVAL/1000 +"sec later will back up again, now waiting....");							
				Thread.sleep(INTERVAL);			
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
