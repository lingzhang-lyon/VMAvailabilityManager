package ling.cmpe283project1;

import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.mo.VirtualMachine;

public class MonitorThread implements Runnable {
	private int ReTryPingVMTimes=10;
	@Override
	public void run() { 
		System.out.println("MonitorThread ThreadId: " + Thread.currentThread().getId());		
		try {			
			
			while (AvailabilityManager.AllowToMonitor)  {
				VirtualMachine[] vms = VcenterManager.findAllVmsInVcenter();				
				for(VirtualMachine vm : vms){
					String vmname=vm.getName();
					if (PingManager.pingVM(vm)){//if ping vm successfully
						System.out.println(vmname +" is fine now");  //may not need to show this message
					}//end if ping vm successfully
					
					else if (vm.getRuntime().getPowerState()==VirtualMachinePowerState.poweredOff  ){ 
					//if ping vm failed but the status is powered off normally
						System.out.println(vmname +" is powered off normally now");
					}//end if ping vm failed but the status is powered off normally
					
					else {//if ping vm failed and the status if not powered off
						//????question: if vm down abnormal, could we still find it??????????????????
						//try to ping several times, make sure no false alarm to triger failover
						System.out.println("trying to ping again now ");
						for(int i=0; i<ReTryPingVMTimes; i++){
							if (PingManager.pingVM(vm)) break; //use break??????????? How to quit???????????????? 
						}
						//if after checked ReTryPingVMTimes, still could not ping through vm
						//createAlarm!! start failover for the vm
						System.out.println(vmname +" is abnormally now!!!!!!!!!!!!!!!!!!!");						
						VcenterManager.failOver(vmname);
					} //end if ping vm failed
				
				}//end of for each vm loop
				
			}//end of while allow to start loop
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	

}