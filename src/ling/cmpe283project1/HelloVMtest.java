package ling.cmpe283project1;

import java.net.URL;


import com.vmware.vim25.*;
import com.vmware.vim25.mo.*;

public class HelloVMtest 
{   
	//private static final String USERNAME = "administrator";
	//private static final String PASSWORD = "12!@qwQW";
	
	public static void main(String[] args) throws Exception
	{
		long start = System.currentTimeMillis();
		URL url = new URL("https://130.65.132.151/sdk");
		ServiceInstance si = new ServiceInstance(url, "root", "12!@qwQW", true);
		long end = System.currentTimeMillis();
		System.out.println("time taken:" + (end-start));
		Folder rootFolder = si.getRootFolder();
		String name = rootFolder.getName();
		System.out.println("root:" + name);
		ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
		if(mes==null || mes.length ==0)
		{
			return;
		}
		
		for(int i=0; i<mes.length; i++){
		VirtualMachine vm = (VirtualMachine) mes[i]; 
		
		if(PingManager.pingVM(vm))  // test for ping function
			System.out.println("ping successful");
		
		VirtualMachineConfigInfo vminfo = vm.getConfig();
		VirtualMachineCapability vmc = vm.getCapability();
        
		vm.getResourcePool();
		System.out.println("Hello " + vm.getName());
		System.out.println("GuestOS: " + vminfo.getGuestFullName());
		System.out.println("Multiple snapshot supported: " + vmc.isMultipleSnapshotsSupported());
				
	    }
	
		si.getServerConnection().logout();
		
	}
	
}