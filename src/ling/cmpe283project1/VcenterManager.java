package ling.cmpe283project1;

import java.net.URL;
import java.util.ArrayList;

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
	static public Datacenter theVcenter;
	static public ArrayList<HostSystem> usedVhosts;
	static public ArrayList<HostConnectSpec> backupVhostConnects;
	
	public static void setVcenter() throws Exception{   
		//set the vCenter with given url, user name and password.
		URL url = new URL("https://130.65.132.150/sdk");
		ServiceInstance si = new ServiceInstance(url, "administrator", "12!@qwQW", true);			
		Folder rootFolder = si.getRootFolder();
		ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("Datacenter");
		VcenterManager.theVcenter =(Datacenter) mes[0];
		ArrayList<HostSystem> Vhosts =new ArrayList<HostSystem>();
		usedVhosts=Vhosts;
	}
	

	
	public static ArrayList<HostSystem> findandUpdateVhostsInVcenter() throws Exception{  
	//find all vHost in this vCenter and then update usedVhosts list
		System.out.println("finding and updating vHosts in this vCenter....");
		Folder vhostFolder = VcenterManager.theVcenter.getHostFolder(); 
		ManagedEntity[] mes =  
				new InventoryNavigator(vhostFolder).searchManagedEntities("HostSystem");
		//if(!VcenterManager.usedVhosts.isEmpty()) VcenterManager.usedVhosts.clear();
		for(ManagedEntity me : mes){			
			VcenterManager.usedVhosts.add( (HostSystem) me);
		}
		return VcenterManager.usedVhosts;
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



	public static void setBackupVhostConnects(){
		//set backupVhostConnects list with given vHost ip, user name and password
		HostConnectSpec newHost = new HostConnectSpec();
		newHost.setHostName("130.65.132.155");
		newHost.setUserName("root");
		newHost.setPassword("12!@qwQW");
		newHost.setSslThumbprint("130.65.132.155");
		VcenterManager.backupVhostConnects.add( newHost);
		
	}
	
	public static void addBackupVhostToVcenter(ArrayList<HostConnectSpec> backupVhostConnects) //need to test
			throws Exception{ 
		//add vHost from backup vHostConnects to the vCenter
		if(!backupVhostConnects.isEmpty()){ 
			System.out.println ("no back up vHost left!!! You need to add more vHost to back up");
			return;
		}	
		HostConnectSpec backupvhost = backupVhostConnects.get(0); //get the first vhost in back up list
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
		System.out.println("try to remove the vHost from the vCenter....");
		Task disconnectTask = vhost.disconnectHost();
		System.out.println("disconnecting vHost " + vhost.getName());

		if (disconnectTask.waitForTask() == Task.SUCCESS) {

			System.out.println("vHost " +vhost.getName() +  " disconnected.");
	         
			//may not need following:
			ComputeResource cr = (ComputeResource) vhost.getParent();
			Task destroyTask = cr.destroy_Task();	
			System.out.println("destroying vHost " +vhost.getName() +  " ........");
			if (destroyTask.waitForTask() == Task.SUCCESS) 
				System.out.println("vHost "+vhost.getName()+"has been destroyed.");			
			else 
				System.out.println("fail to destroy vHost "+vhost.getName());
			
		}
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
