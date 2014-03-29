package ling.cmpe283project1;

import com.vmware.vim25.mo.VirtualMachine;

public class BackupVmThread implements Runnable {
	//back up the all the vm in the vCenter every interval time
	private int INTERVAL=600000;
	
	@Override
	public void run() {
		System.out.println("BackupVmThread ThreadId: " + Thread.currentThread().getId());
		try {
			
			while (AvailabilityManager.AllowToBackup){
				VirtualMachine[] vms = VcenterManager.findAllVmsInVcenter();
				System.out.println("Start to backup VMs now...");
				for (VirtualMachine vm : vms) {
					VmManager.createVmSnapshot(vm);
				}
				System.out.println("finished VMs backup. "+ INTERVAL/1000 +"sec later will back up again, now waiting....");
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
