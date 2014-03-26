package ling.cmpe283project1;

import java.util.ArrayList;

import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.VirtualMachine;

public class VcenterManager {
	static private Datacenter vcenter;
	static private ArrayList<HostSystem> vhosts;
	static private ArrayList<HostSystem> backupvhosts;
	
	public static void findAllVhost(){
	//find all vHost in this vCenter
	}
	
	public static void pingAllVhost() throws Exception{
	//ping all the vHost in this vCenter

   	 for(HostSystem vhost : vhosts) {
   		 PingManager.pingVhost (vhost);
   	 }
	}
	
	public static void addVhostToList(HostSystem vhost){
		vhosts.add(vhost);
	}
	
	public static void addVhostToBackup(HostSystem vhost){
		backupvhosts.add(vhost);
	}
	
    public static void removeVhostFromList(HostSystem vhost){
		
	}
	
	public static void removeVhostFromBackup(HostSystem vhost){
		
	}

}
