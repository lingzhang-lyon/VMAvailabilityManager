package ling.cmpe283project1;

import java.net.URL;


import com.vmware.vim25.*;
import com.vmware.vim25.mo.*;

public class AvailManagerTest {   
	//private static final String USERNAME = "administrator";
	//private static final String PASSWORD = "12!@qwQW";
	private static boolean NoAlarm=true;
	private static int FailTimer=0;
	
	public static void main(String[] args) throws Exception {   
		
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
		
        startMonitor(mes);
	
		si.getServerConnection().logout();
		
	}
	
	public static void startMonitor(ManagedEntity[] mes) throws Exception{
		
		while(NoAlarm ){	
				
			for(int i=0; i<mes.length; i++){
				VirtualMachine vm = (VirtualMachine) mes[i]; 
				
				if(PingManager.pingVM(vm)){  // test for ping function
					System.out.println("ping " + vm.getName() + " successful");
					System.out.println("Timer now is " + FailTimer);
				}
				else {
					FailTimer++;
					System.out.println("ping " + vm.getName() + " fail");
					System.out.println("timer now is " + FailTimer);
					VirtualMachinePowerState vmps = vm.getRuntime().getPowerState();
					if (vmps!=VirtualMachinePowerState.poweredOff ) {
						System.out.println(vm.getName() + "do has some problem");
						//NoAlarm=false;  					
						FailTimer=0;
						
					}
				}
		    }//end of for loop
		}//end of while
	}
	
	


}