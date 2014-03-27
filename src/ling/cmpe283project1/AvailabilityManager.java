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


import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.VirtualMachine;



public class AvailabilityManager 
{   
	//private static final String USERNAME = "administrator";
	//private static final String PASSWORD = "12!@qwQW";
	private static boolean AllowToStart=true;
	private static HashMap<VirtualMachine, Integer> FailTimer;
	private static HashMap<VirtualMachine, Integer> SuccessTimer;
	
	public static void main(String[] args) throws Exception {
		
		
		VcenterManager.setVcenter();//set the predefined vCenter
		VcenterManager.setBackupVhostConnects(); // set up backup vHost List
		monitor();
		
	}
	
	private void startMonitor(){
		AllowToStart=true;
	}
	
	private  void stopMonitor(){
		AllowToStart=false;
	}
	
	private static void monitor() throws Exception{
		while (AllowToStart) {
			VirtualMachine[] vms = VcenterManager.findAllVmsInVcenter();
			for(VirtualMachine vm : vms){
				if (PingManager.pingVM(vm)){//if ping vm successfully
					FailTimer.put(vm, 0);
					if(!SuccessTimer.containsKey(vm)) SuccessTimer.put(vm, 1);
					else SuccessTimer.put(vm, SuccessTimer.get(vm)+1);
				    if(SuccessTimer.get(vm)==10){
				    	VmManager.printStatics(vm);
				    	VmManager.createSnapshot(vm);
				    	SuccessTimer.put(vm, 0);
				    }
				}
				else{ //if ping vm failed
					if(!FailTimer.containsKey(vm)) FailTimer.put(vm, 1);
					else FailTimer.put(vm, FailTimer.get(vm)+1);
				    if(FailTimer.get(vm)==5){
				    	FailTimer.put(vm, 0);
				    	HostSystem parentvhost=(HostSystem) vm.getParent(); //get failed vm's vHost
				    	int pingVhostTime=0;
				    	while (pingVhostTime<=5){  //try to ping parentvhost
				    		if (!PingManager.pingVhost(parentvhost)) pingVhostTime++;
				    		else {
				    			pingVhostTime=0;
				    			break;			//once ping parentvhost successfully, will exit while loop	    			
				    		}
				    	}
				    	if(pingVhostTime==5) { //ping parentvhost failed 5 times, reboot parentvhost
				    		if(!VhostManager.rebootVhost(parentvhost)) { //if reboot failed, migrate to new vHost
				    			HostSystem newvhost = VcenterManager.findFirstAvailableVhost();
				    			VhostManager.migrateVmsToNewVhost(parentvhost, newvhost);
				    		}
				    		else{} //reboot successfully , do nothing, start monitor again, will eventually go to next step;
				    	}
				    	else { //if ping parentvhost successfully
				    		//VmManager.revertToSnapshot(vm, snapshotname);/// need to figure out!!!!!!!!!
				    		//--------------------------------------------
				    		
				    	}				    	
				    }//end if failed to ping vm 5 times									
				} //end if ping vm failed
			
			}//end of for each vm loop
			
		}//end of while allow to start loop
	}
		
	

}
