

package ling.cmpe283project1;

import java.util.HashMap;

import com.vmware.vim25.HostConnectSpec;

//This class include main thread
//This class include all the constant variables and parameters


public class AvailabilityManager 
{   
	public static String VCENTERURL="https://130.65.132.150/sdk";
	public static String USERNAME="administrator";
	public static String PASSWORD="12!@qwQW";
	public static boolean AllowToMonitor=false;
	public static boolean AllowToBackup=false;	
	public static int INTERVAL=300000; //5min
	public static int ReTryPingVMTimes=3; //for monitor
	public static int ReTryPingVhostTimes=2;  //for failover
	public static boolean MONITORBACKUPONEVHOST=true;
	public static String VHOSTNAME="130.65.132.159"; //for just monitor one vHost in vCenter
	


	public static void main(String[] args) throws Exception {
	    System.out.println("Main ThreadId: " + Thread.currentThread().getId());
		AvailabilityManager.setAvailabilityManager();
		
		new Thread(new BackupAndStaticsThread()).start(); 
				
		new Thread(new MonitorAndFailoverThread()).start();
		
	}
	
	public static void setAvailabilityManager() throws Exception{  //constructor
		VcenterManager.setVcenter();//set the predefined vCenter, and initial new list for the following
		AvailabilityManager.setBackupVhostConnects(); // set up backup vHost List in VcenterManager
		AvailabilityManager.setVhostNameIn14Map(); //set up VhostNameIn14Map in VcenterManager
		VcenterManager.findandUpdateVhostsInVcenter(); //update VhostsInVcenter List
		VcenterManager.updateVmNameToVhostNameMap();  //update VmNameToVhostNameMap
		AvailabilityManager.allowToStartMonitor();
		AvailabilityManager.allowToStartBackup();
	}
	
	
	public static void setBackupVhostConnects(){ 
		//pre define backupVhostConnects list with given vHost ip, user name and password
		HostConnectSpec newHost = new HostConnectSpec();
		newHost.setHostName("130.65.132.155");
		newHost.setUserName("root");
		newHost.setPassword("12!@qwQW");
		newHost.setSslThumbprint("EE:2B:25:8F:48:6E:38:5C:B3:DD:B0:87:FD:66:AA:1B:25:DF:B9:7C");
		VcenterManager.backupVhostConnects.add( newHost);
		
	}

	public static void setVhostNameIn14Map() throws Exception{
		// pre define the vhostNameIn14Map
		HashMap<String, String> Map = new HashMap<String, String>();
	    Map.put("130.65.132.151", "t03-vHost01-cum1-lab1 _.132.151");
	    Map.put("130.65.132.155", "t03-vHost01-cum1-proj1_132.155");
	    Map.put("130.65.132.159", "t03-vHost01-cum1-lab2_132.159");
	    VcenterManager.vhostNameIn14Map=Map;
	    
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
