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



public class Test3AvailabilityManager

{   


	
	public static void main(String[] args) throws Exception {
		
		AvailabilityManager.setAvailabilityManager();		
		//monitor();
		backupVMPeriodically(60000);
		
	}
	

	
	private static void monitor() throws Exception{		
		while (AvailabilityManager.AllowToMonitor) {
			VirtualMachine[] vms = VcenterManager.findAllVmsInVcenter();
			for(VirtualMachine vm : vms){
				String vmname=vm.getName();
				if (PingManager.pingVM(vm)){//if ping vm successfully
					
				}//end if ping vm successfully
				else if (vm.getRuntime().getPowerState()==VirtualMachinePowerState.poweredOff  ){ 
					  //if ping vm failed but the status is powered off normally
					  System.out.println(vmname +" is powered off normally now");
				}//end if ping vm failed but the status is powered off normally
				else {//if ping vm failed and the status if not powered off
							
				} //end if ping vm failed
			
			}//end of for each vm loop
			
		}//end of while allow to start loop
	}



	public static void backupVMPeriodically(int interval) throws Exception {
	    //back up the all the vm every interval time
		if (interval <= 0)
			interval = 600000; //default time is 10min
		
		while (AvailabilityManager.AllowToBackup){
			VirtualMachine[] vms = VcenterManager.findAllVmsInVcenter();
			System.out.println("Start to backup now");
			for (VirtualMachine vm : vms) {
				VmManager.createVmSnapshot(vm);
			}
			System.out.println("finished backup. "+ interval/1000 +"sec later will back up again, now waiting....");
			Thread.sleep(interval);			
		}
		
	}	
}
