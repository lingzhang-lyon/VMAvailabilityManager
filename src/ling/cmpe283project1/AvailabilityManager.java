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
		
	

}
