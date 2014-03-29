package ling.cmpe283project1;

import com.vmware.vim25.mo.HostSystem;

public class BackupVhostThread implements Runnable{
	//back up the all the vhost in the vCenter every interval time
	private int INTERVAL=600000;
	
	@Override
	public void run() { 
		System.out.println("BackupVhostThread ThreadId: " + Thread.currentThread().getId());
		try {
			//set up the VcenterManager theVcenter and vhostNameIn14Map
			// to make sure could find vhosts from admin server 14.
			AvailabilityManager.setAvailabilityManager(); 
		
			while (AvailabilityManager.AllowToBackup){
				HostSystem[] vhosts = VcenterManager.findandUpdateVhostsInVcenter();
				System.out.println("Start to backup vhosts now...");
				for (HostSystem vhost : vhosts) {
					VhostManager.createVhostSnapshot(vhost);
				}
				System.out.println("finished vhosts backup. "+ INTERVAL/1000 +"sec later will back up again, now waiting....");
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
