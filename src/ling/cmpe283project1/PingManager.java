package ling.cmpe283project1;

import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.VirtualMachine;

public class PingManager {
		
	public static boolean pingByIP(String ip) throws Exception {
		System.out.println("ping....");	
	String cmd = "";
	if (System.getProperty("os.name").startsWith("Windows")) {
		// For Windows
		cmd = "ping -n 1 " + ip;
		} else {
		// For Linux and OSX
		cmd = "ping -c 1 " + ip;
		}
	Process process = Runtime.getRuntime().exec(cmd);
	process.waitFor();
	
	return process.exitValue() == 0;
	}
	
	public static boolean pingVM (VirtualMachine vm) throws Exception{
		String ip=vm.getGuest().getIpAddress();
	    //System.out.println(vm.getName() +" ip is "+ ip);
		return pingByIP(ip);
	}
	
	public static boolean pingVhost (HostSystem vhost) throws Exception{
		String ip=vhost.getConfig().getNetwork().getVnic()[0].getSpec().getIp().getIpAddress();
		return pingByIP(ip);
	}
	
	public static String getVhostIP(HostSystem vhost) throws Exception{
		String ip=vhost.getConfig().getNetwork().getVnic()[0].getSpec().getIp().getIpAddress();
		return ip;
	}

	
}
