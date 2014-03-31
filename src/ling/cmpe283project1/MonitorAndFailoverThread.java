package ling.cmpe283project1;

import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.VirtualMachine;

public class MonitorAndFailoverThread implements Runnable {
	
	@Override
	public void run() { 
		System.out.println("MonitorThread ThreadId: " + Thread.currentThread().getId());		
		try {
			if (AvailabilityManager.MONITORBACKUPONEVHOST==true) monitorOneVhost();
			else monitorAllVhost();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public void monitorOneVhost() throws Exception {			
		HostSystem vhost=VhostManager.findVhostByNameInVcenter(AvailabilityManager.VHOSTNAME);
		
		while (AvailabilityManager.AllowToMonitor)  {		
			VirtualMachine[] vms = VhostManager.findAllVmsInVhost(vhost);				
			monitorForOneTime(vms);
		}	
	}
	
	public void monitorAllVhost() throws Exception {
		
		while (AvailabilityManager.AllowToMonitor)  {		
			VirtualMachine[] vms = VcenterManager.findAllVmsInVcenter();				
			monitorForOneTime(vms);
		}
	}
	
	public void monitorForOneTime(VirtualMachine[] vms) throws Exception{
		for(VirtualMachine vm : vms){
			String vmname=vm.getName();
			if (PingManager.pingVM(vm)){//if ping vm successfully
				System.out.println("\n"+vmname +" is fine now");  //may not need to show this message
			}//end if ping vm successfully
			
			else if (vm.getRuntime().getPowerState()==VirtualMachinePowerState.poweredOff  ){ 
			//if ping vm failed but the status is powered off normally
				System.out.println("\n"+vmname +" is powered off normally now");
			}//end if ping vm failed but the status is powered off normally
			
			else {//if ping vm failed and the status if not powered off
				//try to ping several times, make sure no false alarm to triger failover
				System.out.println("\nping " + vmname +" failed once, trying to ping again now ");
				for(int i=0; i<AvailabilityManager.ReTryPingVMTimes; i++){
					System.out.println("retry ping " + vmname +" for " + (i+1)+ " times");
					if (PingManager.pingVM(vm)) break;//will not ping again
				}
				if (PingManager.pingVM(vm)) continue; // will not failover
				
				//if after checked ReTryPingVMTimes, still could not ping through vm
				//createAlarm!! start failover for the vm
				System.out.println(vmname +" is abnormally now!!!!!!!!!!!!!!!!!!!");						
				MonitorAndFailoverThread.failOver(vmname);
			} //end if ping vm failed
		
		}//end of for each vm loop
	}

	public static void failOver(String vmname) throws Exception {  
			//Action: try different ways to recover the failed VM 
			//Precondition:  the VM could not ping through and the status is not powered off normally 
			
			System.out.println("\ntrying to failover for "+ vmname + " now...");
			
			//first figure out the parent vHost is dead or not 
			System.out.println("finding "+ vmname + "'s parent vhost now...");
			String vhostname = VmManager.findVhostNameByVmName(vmname);
			HostSystem parentvhost =VhostManager.findVhostByNameInVcenter(vhostname);
			System.out.println("checking the parent vhost now...");
			if (parentvhost!=null && PingManager.pingVhost(parentvhost)){	
				//if vHost is found and normal, you should be able to find the VM 		
				System.out.println("the parent vHost is normal now, trying to recover " + vmname +" now...");			
				//first try to power on the VM // do we need?
				VirtualMachine vm = VmManager.findVmByNameInVcenter(vmname);
				VmManager.setPowerOn(vm);
				
				if(vm==null||!PingManager.pingVM(vm)){
				//if still could not ping through VM
					System.out.println("still could not ping through "+ vmname + ", will revert to snapshot");
					VmManager.revertToSnapshotAndPoweron(vm);
				}
			}
			else { //if vHost could not found or is abnormal
	              //try to ping vHost several times to make sure it's not alarm by mistake
				System.out.println("the parent vhost ping failed once");
				System.out.println("retrying ping for " + AvailabilityManager.ReTryPingVhostTimes + " more times now...");
				for(int i=0; i<AvailabilityManager.ReTryPingVhostTimes; i++){
					if (parentvhost!=null && PingManager.pingVhost(parentvhost)) {
						System.out.println("the parent vhost is actually normal, will retry to failover the VM again");
						return;	
					}//will exit failover method, then the monitorThread will check VM again and call failover again
					else System.out.println("the parent vhost ping failed for " + (i+1)+ " times ");
				}
				
				//if after checked ReTryPingVhostTimes times, still could not ping through vhost
				//System.out.println("simulating revert " +vmname +"'s vhost to snapshot: " + vhostname);
				VhostManager.recoverVhostFromSnapshotAndPoweron(vhostname);
				//after recover vhost, monitorThread will check VM again and call failover again to recover VM
				
	//			//if could not recover vhost from snapshot ---should not happen!!!
	//			//find other available vhost
	//			HostSystem newvhost = VcenterManager.findFirstAvailableVhost();
	//				// if found new vhost Migrate all VMs from the down vhost to new vhost
	//				VhostManager.migrateVmsToNewVhost(oldvhost, newvhost);
							
			}
			
		}

}
