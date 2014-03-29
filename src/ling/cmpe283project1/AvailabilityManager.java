

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
	


}
