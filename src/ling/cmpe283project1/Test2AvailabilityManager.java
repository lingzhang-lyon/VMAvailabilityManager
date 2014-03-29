

package ling.cmpe283project1;

import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.mo.VirtualMachine;



public class Test2AvailabilityManager  
//test create snapshot every 50 times ping successfully
//test success

{   


	
	public static void main(String[] args) throws Exception {
		
		AvailabilityManager.setAvailabilityManager();		
		monitor();
		
	}
	

	
	private static void monitor() throws Exception{		
		while (AvailabilityManager.AllowToMonitor) {
			VirtualMachine[] vms = VcenterManager.findAllVmsInVcenter();
			for(VirtualMachine vm : vms){
				String vmname=vm.getName();
				if (PingManager.pingVM(vm)){//if ping vm successfully
					AvailabilityManager.FailTimer.put(vmname, 0);
					System.out.println(vmname +" ping successful ");
					if(!AvailabilityManager.SuccessTimer.containsKey(vmname)) {
						AvailabilityManager.SuccessTimer.put(vmname, 1);						
					}					
					else{
						int newtime;
					    newtime=AvailabilityManager.SuccessTimer.get(vmname)+1;
					  //for test, no need any more//System.out.println("new times: " +newtime);
					    AvailabilityManager.SuccessTimer.put(vmname, newtime);						
					}
					
					if(AvailabilityManager.SuccessTimer.get(vmname)==1)
						System.out.println(vmname +" is fine now"
								+ "\nafter ping 50 times,will print statics. Please wait....... ");
					
					//for test, no need any more//System.out.println(vmname+" ping success for "+SuccessTimer.get(vmname) + " times");
				    if(AvailabilityManager.SuccessTimer.get(vmname)==50){
				    	VmManager.printStatics(vm);
				    	VmManager.createVmSnapshot(vm);
				    	AvailabilityManager.SuccessTimer.put(vmname, 0);
				    	//for test, no need any more //System.out.println("after print,"+ vmname + "ping success times now is: " + SuccessTimer.get(vmname));
				    }
				}
				else if (vm.getRuntime().getPowerState()==VirtualMachinePowerState.poweredOff  ){ 
					  //if ping vm failed but the status is powered off normally
					  System.out.println(vmname +" is powered off normally now");
				}
				else {//if ping vm failed and the status if not powered off
//					if(!FailTimer.containsKey(vm)) FailTimer.put(vm, 1);
//					else FailTimer.put(vm, FailTimer.get(vm)+1);
//				    if(FailTimer.get(vm)==5){
//				    	FailTimer.put(vm, 0);
//				    	HostSystem parentvhost=(HostSystem) vm.getParent(); //get failed vm's vHost
//				    	int pingVhostTime=0;
//				    	while (pingVhostTime<=5){  //try to ping parentvhost
//				    		if (!PingManager.pingVhost(parentvhost)) pingVhostTime++;
//				    		else {
//				    			pingVhostTime=0;
//				    			break;			//once ping parentvhost successfully, will exit while loop	    			
//				    		}
//				    	}
//				    	if(pingVhostTime==5) { //ping parentvhost failed 5 times, reboot parentvhost
//				    		if(!VhostManager.rebootVhost(parentvhost)) { //if reboot failed, migrate to new vHost
//				    			HostSystem newvhost = VcenterManager.findFirstAvailableVhost();
//				    			VhostManager.migrateVmsToNewVhost(parentvhost, newvhost);
//				    		}
//				    		else{} //reboot successfully , do nothing, start monitor again, will eventually go to next step;
//				    	}
//				    	else { //if ping parentvhost successfully
//				    		//VmManager.revertToSnapshot(vm, snapshotname);/// need to figure out!!!!!!!!!
//				    		//--------------------------------------------
//				    		
//				    	}				    	
//				    }//end if failed to ping vm 5 times									
				} //end if ping vm failed
			
			}//end of for each vm loop
			
		}//end of while allow to start loop
	}



//	public static void backupVMPeriodically(int interval) throws Exception {
//	
//		if (interval <= 0)
//			interval = 600000;
//		
//		while (!stopBackup)
//		VirtualMachine[] vms = VcenterManager.findAllVmsInVcenter();
//		for (VirtualMachine vm : vms) {
//			VmManager.createVmSnapshot(vm);
//		}
//		
//		while (!stopBackup) {
//			for (String vmName : SnapshotMap.keySet()) {
//			create(vmName, "latest_snapshot", "");
//			}
//			
//		Thread.sleep(interval);
//		
//	
//	}
		
	

}
