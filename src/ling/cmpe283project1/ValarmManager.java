package ling.cmpe283project1;

import java.net.URL;

import com.vmware.vim25.AlarmSetting;
import com.vmware.vim25.AlarmSpec;
import com.vmware.vim25.StateAlarmExpression;
import com.vmware.vim25.StateAlarmOperator;
import com.vmware.vim25.mo.AlarmManager;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

public class ValarmManager {
	
	public static void setAlarm (VirtualMachine vm, String alarmname) throws Exception {
	URL url = new URL(AvailabilityManager.VCENTERURL);
	ServiceInstance si = new ServiceInstance(url, AvailabilityManager.USERNAME, AvailabilityManager.PASSWORD, true);
	AlarmManager alarmMgr = si.getAlarmManager();
	AlarmSpec spec = new AlarmSpec();
	
	StateAlarmExpression expression = createStateAlarmExpression();
	
	  spec.setAction(null);
	  spec.setExpression(expression);
	  spec.setName("VmPowerStateAlarm");
	  spec.setDescription("Monitor VM state is power off");
	  spec.setEnabled(true);
	  AlarmSetting as = new AlarmSetting();
	  as.setReportingFrequency(0); //as often as possible
	  as.setToleranceRange(0);
	  spec.setSetting(as);
	  alarmMgr.createAlarm(vm, spec);
	  si.getServerConnection().logout();
	  
	}
	
	static StateAlarmExpression createStateAlarmExpression()
	{
	  StateAlarmExpression expression =
	    new StateAlarmExpression();
	expression.setType("VirtualMachine"); expression.setStatePath("runtime.powerState"); expression.setOperator(StateAlarmOperator.isEqual); expression.setRed("poweredOff");
	  return expression;
	}
	



}

