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
	
	public static void setVcenter() throws Exception{   //constructor
		//initial the vCenter with given url, user name and password.
		URL url = new URL(AvailabilityManager.VCENTERURL);
		ServiceInstance si = new ServiceInstance(url, AvailabilityManager.USERNAME, AvailabilityManager.PASSWORD, true);			
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
	
	public static HostSystem[] findandUpdateVhostsInVcenter() throws Exception{  
	//runtime find all vHost in this vCenter and then update usedVhosts list
		
		//System.out.println("finding and updating vHosts in this vCenter....");
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

	
	public static void addBackupVhostToVcenter(ArrayList<HostConnectSpec> backupVhostConnects) 
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
	
	public static void removeVhostFromVcenter(HostSystem vhost) throws Exception{ 
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


}
