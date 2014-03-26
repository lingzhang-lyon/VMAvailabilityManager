package ling.cmpe283project1;

import java.util.ArrayList;

import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.VirtualMachine;

public class VhostManager {
     public static void restartVhost(HostSystem vhost){
    	 
     }
     
     public static void createNewVhost(){
    	 
     }
     
     public static void migrateAllVmsToVhost(HostSystem oldvhost, HostSystem newvhost){
    	 
     }
     
     public static ArrayList<VirtualMachine> findAllVms (HostSystem vhost){
    	 //find all the virtual machines in the selected vHost
    	 ArrayList<VirtualMachine> vms = new ArrayList<VirtualMachine>();
    	 return vms;
     }
     
     public static void pingAllVms(HostSystem vhost) throws Exception {
    	 //find and ping all vms in selected vhost 
    	 ArrayList<VirtualMachine> vms = new ArrayList<VirtualMachine>();
    	 vms=findAllVms(vhost);
    	 for(VirtualMachine vm : vms){
    		 PingManager.pingVM (vm);
    	 }
     }
     
     
     //optional
     public static void createSnapshot(HostSystem vhost){
    	 //create a snapshot for selected vHost in the admin server (130.65.132.14) ??
     }
     
     //optional
     public static void recoverVhostFromSnapshot(HostSystem oldvhost, HostSystem newvhost){
    	 //recover vHost by snapshot???
     
     }
     
}
