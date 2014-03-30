package ling.cmpe283project1;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import com.vmware.vim25.ComputeResourceConfigSpec;
import com.vmware.vim25.HostConnectSpec;
import com.vmware.vim25.mo.ComputeResource;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

public class VcenterManager {
	static public Datacenter theVcenter; //pre-define
	static public ArrayList<HostConnectSpec> backupVhostConnects;  //pre-define
	public static HashMap<String, String> vhostNameIn14Map; //pre-define
	static public ArrayList<HostSystem> usedVhosts; //runtime-update
	static public HashMap<String, String> vmNameToVhostNameMap; //runtime-update
	private static int ReTryPingVhostTimes=2;  //for failover
	
	public static void setVcenter() throws Exception{   //constructor
		//initial the vCenter with given url, user name and password.
		URL url = new URL("https://130.65.132.150/sdk");
		ServiceInstance si = new ServiceInstance(url, "administrator", "12!@qwQW", true);			
		Folder rootFolder = si.getRootFolder();
		ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("Datacenter");
		VcenterManager.theVcenter =(Datacenter) mes[0];
		//initial usedVhosts
		ArrayList<HostSystem> Vhosts =new ArrayList<HostSystem>(); 
		usedVhosts=Vhosts;
		//initial backupVhostConnects
		ArrayList<HostConnectSpec> VhostConnects =new ArrayList<HostConnectSpec>(); 
		backupVhostConnects=VhostConnects;
		//initial vmNameToVhostNameMap
		HashMap<String, String> map= new HashMap<String,String>();
		vmNameToVhostNameMap= map;
		//initial vhostNameIn14Map
		HashMap<String, String> map2= new HashMap<String,String>();
		vhostNameIn14Map=map2;
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
	
	public static HostSystem[] findandUpdateVhostsInVcenter() throws Exception{  
	//runtime find all vHost in this vCenter and then update usedVhosts list
		
		System.out.println("finding and updating vHosts in this vCenter....");
		Folder vhostFolder = VcenterManager.theVcenter.getHostFolder(); 
		ManagedEntity[] mes =  
				new InventoryNavigator(vhostFolder).searchManagedEntities("HostSystem");
		if(!VcenterManager.usedVhosts.isEmpty()) VcenterManager.usedVhosts.clear();
		for(ManagedEntity me : mes){			
			VcenterManager.usedVhosts.add( (HostSystem) me);
		}
		HostSystem[] vhosts = new HostSystem[mes.length] ;
		for(int i=0; i<mes.length; i++){
			vhosts[i]=(HostSystem) mes[i];
		}
		return vhosts;
	}
	
	public static void updateVmNameToVhostNameMap() throws Exception{  
		 //runtime update the VmNameToVhostNameMap
		
		HostSystem[] vhosts=findandUpdateVhostsInVcenter();
		for (HostSystem vhost: vhosts){
			String vhostname=vhost.getName();
			VirtualMachine[] vms=vhost.getVms();
			for(VirtualMachine vm : vms) {
				String vmname=vm.getName();
				VcenterManager.vmNameToVhostNameMap.put(vmname, vhostname);
			}			
		}
		System.out.println("vmNameToVhostNameMap is updated");
	}
	
	public static HostSystem findFirstAvailableVhost() throws Exception{ //need to test
	//find first available vHost from usedVhosts list		
		VcenterManager.findandUpdateVhostsInVcenter();  //update current usedVhost list
		System.out.println("finding first available vHosts in this vCenter....");
		for(HostSystem vhost : VcenterManager.usedVhosts){  //search usedVhost list
			if(PingManager.pingVhost(vhost)) return vhost;
		}		
		System.out.println("No vHost available in used list");
		return null;
	
	}

	
	public static void addBackupVhostToVcenter(ArrayList<HostConnectSpec> backupVhostConnects) //need to test
			throws Exception{ 
		//add vHost from backup vHostConnects to the vCenter
		if(backupVhostConnects.isEmpty()){ //if backup list is empty
			System.out.println ("no back up vHost left!!! You need to add more vHost to back up");
			return;
		}
		//if backup list is not empty
		HostConnectSpec backupvhost = backupVhostConnects.get(0); //get the first vhostconnect in back up list
		System.out.println ("trying to add backup vHost to the vCenter now....");
		Task addHostTask =
				VcenterManager.theVcenter.getHostFolder().addStandaloneHost_Task(backupvhost, new ComputeResourceConfigSpec(), true);
		if (addHostTask.waitForTask() == Task.SUCCESS) {
			System.out.println ("add back up vHost to the vCenter successfully");
			backupVhostConnects.remove(0);
		}			
		else System.out.println ("fail to add back up vHost to the vCenter");		
	}
	
	public static void removeVhostFromVcenter(HostSystem vhost) throws Exception{ //need to test
		//remove selected vHost from the vCenter
		System.out.println("trying to remove the vHost from the vCenter....");
		Task disconnectTask = vhost.disconnectHost();
		System.out.println("disconnecting vHost " + vhost.getName());
        String vhostName=vhost.getName();
		if (disconnectTask.waitForTask() == Task.SUCCESS) {

			System.out.println("vHost " +vhost.getName() +  " disconnected.");
	         
	
			ComputeResource cr = (ComputeResource) vhost.getParent();
			Task destroyTask = cr.destroy_Task();	
			System.out.println("destroying vHost " +vhost.getName() +  " ........");
			
			if (destroyTask.waitForTask() == Task.SUCCESS) 
				System.out.println("vHost "+vhostName+"has been destroyed.");			
			else 
				System.out.println("fail to destroy vHost "+vhostName);
			
		}
		else System.out.println("check");
	}



	public static VirtualMachine[] findAllVmsInVcenter() throws Exception{  
	//find all virtual machines in this vCenter

		Folder vmFolder =VcenterManager.theVcenter.getVmFolder();
		ManagedEntity[] mes = new InventoryNavigator(vmFolder).searchManagedEntities("VirtualMachine");
		
		VirtualMachine[] vms = new VirtualMachine[mes.length] ;
		for(int i=0; i<mes.length; i++){
			vms[i]=(VirtualMachine) mes[i];
		}
		return vms;
	}

	public static void failOver(String vmname) throws Exception {  //need to test
		//Action: try different ways to recover the failed VM 
		//Precondition:  the VM could not ping through and the status is not powered off normally 
		
		System.out.println("trying to failover for "+ vmname + " now...");
		
		//first figure out the parent vHost is dead or not 
		System.out.println("finding "+ vmname + "'s parent vhost now...");
		String vhostname = VmManager.findVhostNameByVmName(vmname);
		HostSystem parentvhost =VhostManager.findVhostByNameInVcenter(vhostname);
		System.out.println("checking the parent vhost now...");
		if (parentvhost!=null && PingManager.pingVhost(parentvhost)){	//if vHost is found and normal, you should be able to find the VM 		
			System.out.println("the parent vHost is normal now, trying to recover " + vmname +" now...");			
			//first try to power on the VM // do we need?
			VirtualMachine vm = VmManager.findVmByNameInVcenter(vmname);
			VmManager.setPowerOn(vm);
			
			if(vm==null||!PingManager.pingVM(vm)){
			//if still could not ping through VM
				System.out.println("still could not ping through "+ vmname + ", will revert to snapshot");
				VmManager.revertToSnapshotAndPoweron(vm);
			}
		}
		else { //if vHost could not found or is abnormal
              //try to ping vHost several times to make sure it's not alarm by mistake
			System.out.println("the parent vhost ping failed once");
			System.out.println("retrying ping for " + ReTryPingVhostTimes + " more times now...");
			for(int i=0; i<VcenterManager.ReTryPingVhostTimes; i++){
				if (parentvhost!=null && PingManager.pingVhost(parentvhost)) {
					System.out.println("the parent vhost is actually normal, will retry to failover the VM again");
					return;	
				}//will exit failover method, then the monitorThread will check VM again and call failover again
				else System.out.println("the parent vhost ping failed for " + (i+1)+ " times ");
			}
			
			//if after checked ReTryPingVhostTimes times, still could not ping through vhost
			//System.out.println("simulating revert " +vmname +"'s vhost to snapshot: " + vhostname);
			VhostManager.recoverVhostFromSnapshotAndPoweron(vhostname);
			//after recover vhost, monitorThread will check VM again and call failover again to recover VM
			
//			//if could not recover vhost from snapshot ---should not happen!!!
//			//find other available vhost
//			HostSystem newvhost = VcenterManager.findFirstAvailableVhost();
//				// if found new vhost Migrate all VMs from the down vhost to new vhost
//				VhostManager.migrateVmsToNewVhost(oldvhost, newvhost);
						
		}
		
	}


}
