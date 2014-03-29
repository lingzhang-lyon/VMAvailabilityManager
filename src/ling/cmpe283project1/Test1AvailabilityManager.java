/*================================================================================
Copyright (c) 2008 VMware, Inc. All Rights Reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, 
this list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice, 
this list of conditions and the following disclaimer in the documentation 
and/or other materials provided with the distribution.

* Neither the name of VMware, Inc. nor the names of its contributors may be used
to endorse or promote products derived from this software without specific prior 
written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL VMWARE, INC. OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
POSSIBILITY OF SUCH DAMAGE.
================================================================================*/

package ling.cmpe283project1;

import java.util.HashMap;

import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.VirtualMachine;



public class Test1AvailabilityManager  
// test print VM statics every 50 times ping successfully
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
								+ "\nafter ping 50 times,will print statics and create snapshot. Please wait....... ");
					
					//for test, no need any more//System.out.println(vmname+" ping success for "+SuccessTimer.get(vmname) + " times");
				    if(AvailabilityManager.SuccessTimer.get(vmname)==50){
				    	VmManager.printStatics(vm);
				    	//VmManager.createVmSnapshot(vm);
				    	AvailabilityManager.SuccessTimer.put(vmname, 0);
				    	//for test, no need any more //System.out.println("after print,"+ vmname + "ping success times now is: " + SuccessTimer.get(vmname));
				    }
				}
				else if (vm.getRuntime().getPowerState()!=VirtualMachinePowerState.poweredOff  ){ //if ping vm failed and the status if not powered off
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
		
	

}