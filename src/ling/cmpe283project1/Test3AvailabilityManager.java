

package ling.cmpe283project1;

import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.VirtualMachine;


public class Test3AvailabilityManager   //test methods for threads

{   

	public static void main(String[] args) throws Exception {
		
		//AvailabilityManager.setAvailabilityManager();  //will set up VcenterManager 		
		//we can use different thread for monitor and backup		
		//backupVMPeriodically(60000);  //test success
		//backupVhostPeriodically(60000); //test success

		//test failover
		//--first disconnect the network then test, but vhost is normal
		//testFailOverMethod("T03-VM02-Lin-Ling"); //will try to recover VM //test success
				
		//--then test when vhost disconnect 
		//TestHelloVM.testCreateVhostSnapshot("130.65.132.159");
		 //then disconnect vhost "130.65.132.155" manually
		 testFailOverMethod2("T03-VM02-Lin-Ling"); //will try to recover vhost

		
		//monitor();  // need to test
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
	
	public static void testFailOverMethod(String vmname) throws Exception {		
		//pre condtion: already have snapshot of the vm
		   //  test disconnect the network of vm!!!!!!!!!
		
		
		AvailabilityManager.setAvailabilityManager();
		VcenterManager.failOver(vmname);
		VirtualMachine vm= VmManager.findVmByNameInVcenter(vmname);
		if(PingManager.pingVM(vm)) 
		   System.out.println("ping VM " +vmname+" success now");
		else System.out.println("ping VM " +vmname+" failed");
		
	}
	
	public static void testFailOverMethod2(String vmname) throws Exception {		
		//pre condition: need first backup vhost 		
		//then disconnect vhost manually
		//Action:  test when vhost disconnect!!!!!!!!
		AvailabilityManager.setAvailabilityManager();
		
		VirtualMachine vm= VmManager.findVmByNameInVcenter(vmname);
		if(!PingManager.pingVM(vm)) 
			System.out.println(vmname + " ping failed, need to failover");
		
		
		VcenterManager.failOver(vmname);
		System.out.println("sleep for a while waiting for vhost and vm be active....");
		
		Thread.sleep(60000);
		
		//after failover check vhost
		System.out.println("Rechecking the Vhost and VM after failover now...");
		String vhostname=VmManager.findVhostNameByVmName(vmname);
		HostSystem vhost=VhostManager.findVhostByNameInVcenter(vhostname);
		if(PingManager.pingVhost(vhost)) 
			   System.out.println("ping VM " +vhostname+" success now");
			else System.out.println("ping VM " +vhostname+" failed now");	
		
		//after failover check VM
		//VirtualMachine vm= VmManager.findVmByNameInVcenter(vmname);
		if(PingManager.pingVM(vm)) 
		   System.out.println("ping VM " +vmname+" success now");
		else System.out.println("ping VM " +vmname+" failed now");		
		Thread.sleep(20000);
		if(PingManager.pingVM(vm)) 
			   System.out.println("ping VM " +vmname+" success now");
			else System.out.println("ping VM " +vmname+" failed now");
		
	}







}
