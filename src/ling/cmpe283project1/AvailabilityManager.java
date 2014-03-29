

package ling.cmpe283project1;

import java.util.HashMap;

import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.VirtualMachine;



public class AvailabilityManager 
{   
	protected static boolean AllowToMonitor=false;
	protected static boolean AllowToBackup=false;
	protected static HashMap<String, Integer> FailTimer;
	protected static HashMap<String, Integer> SuccessTimer;
	public static void main(String[] args) throws Exception {
		
		setAvailabilityManager();		
		//monitor();
		
	}
	
	public static void setAvailabilityManager() throws Exception{  //constructor
		VcenterManager.setVcenter();//set the predefined vCenter
		VcenterManager.setBackupVhostConnects(); // set up backup vHost List
		VcenterManager.setVhostNameIn14Map(); //set up VhostNameIn14Map
		AvailabilityManager.FailTimer= new HashMap<String, Integer> ();
		AvailabilityManager.SuccessTimer= new HashMap<String, Integer> ();
		AvailabilityManager.allowToStartMonitor();
		AvailabilityManager.allowToStartBackup();
	}
	
	
	public static void allowToStartMonitor(){
		AllowToMonitor=true;
	}
	
	public static void allowToStartBackup(){
		AllowToBackup=true;
	}
	
	public static void stopAllowMonitor(){
		AllowToMonitor=false;
	}
	
	public static void backupVMPeriodically(int interval) throws Exception {
	    //back up the all the vm in the vCenter every interval time
		if (interval <= 0)
			interval = 600000; //default time is 10min
		
		while (AvailabilityManager.AllowToBackup){
			VirtualMachine[] vms = VcenterManager.findAllVmsInVcenter();
			System.out.println("Start to backup VMs now...");
			for (VirtualMachine vm : vms) {
				VmManager.createVmSnapshot(vm);
			}
			System.out.println("finished VMs backup. "+ interval/1000 +"sec later will back up again, now waiting....");
			Thread.sleep(interval);			
		}		
	}
	
	public static void backupVhostPeriodically(int interval) throws Exception {
		//back up the all the vhost in the vCenter every interval time
		//pre-condition: need to set up the VcenterManager theVcenter and vhostNameIn14Map
		// to make sure could find vhosts from admin server.
		
		if (interval <= 0)
			interval = 600000; //default time is 10min
		
		while (AvailabilityManager.AllowToBackup){
			HostSystem[] vhosts = VcenterManager.findandUpdateVhostsInVcenter();
			System.out.println("Start to backup vhosts now...");
			for (HostSystem vhost : vhosts) {
				VhostManager.createVhostSnapshot(vhost);
			}
			System.out.println("finished vhosts backup. "+ interval/1000 +"sec later will back up again, now waiting....");
			Thread.sleep(interval);			
		}
	}
	
	
	public static void failOver(String vmname) throws Exception {  //need to test
		//Action: try different ways to recover the failed VM (
		//Precondition:  the VM could not ping through and the status is not powered off normally )
		
		//first figure out the parent vHost is dead or not 
		String vhostname = VmManager.findVhostNameByVmName(vmname);
		HostSystem parentvhost =VhostManager.findVhostByNameInVcenter(vhostname);
		if (parentvhost!=null && PingManager.pingVhost(parentvhost)){	//if vHost is found and normal, you should be able to find the VM 		
			//first try to power on the VM // do we need?
			VirtualMachine vm = VmManager.findVmByNameInVcenter(vmname);
			VmManager.setPowerOn(vm);
			
			if(vm==null||!PingManager.pingVM(vm)){
			//if still could not ping through VM
				VmManager.revertToSnapshotAndPoweron(vm);
			}
		}
		else { //if vHost could not found or is abnormal
              //try to ping vHost several times to make sure it's not alarm by mistake
			for(int i=0; i<5; i++){
				if (parentvhost!=null && PingManager.pingVhost(parentvhost)) break; 
			}
			//if after checked 5 times, still could not ping through vhost
			VhostManager.recoverVhostFromSnapshot(vhostname);
			
//			//if could not recover vhost from snapshot ---should not happen!!!
//			//find other available vhost
//			HostSystem newvhost = VcenterManager.findFirstAvailableVhost();
//				// if found new vhost Migrate all VMs from the down vhost to new vhost
//				VhostManager.migrateVmsToNewVhost(oldvhost, newvhost);
						
		}
		
	}
		
	public static void monitor() throws Exception{		
		while (AvailabilityManager.AllowToMonitor) {
			VirtualMachine[] vms = VcenterManager.findAllVmsInVcenter();
			for(VirtualMachine vm : vms){
				String vmname=vm.getName();
				if (PingManager.pingVM(vm)){//if ping vm successfully
					System.out.println(vmname +" is fine now");
				}//end if ping vm successfully
				else if (vm.getRuntime().getPowerState()==VirtualMachinePowerState.poweredOff  ){ 
					//if ping vm failed but the status is powered off normally
					System.out.println(vmname +" is powered off normally now");
				}//end if ping vm failed but the status is powered off normally
				else {//if ping vm failed and the status if not powered off
					//try to ping several times, make sure no false alarm to triger failover
					System.out.println("trying to ping again now ");
					//if failed fixed times
					System.out.println(vmname +" is abnormally now");
					//createAlarm!! start failover for the vm;
				} //end if ping vm failed
			
			}//end of for each vm loop
			
		}//end of while allow to start loop
	}

}
