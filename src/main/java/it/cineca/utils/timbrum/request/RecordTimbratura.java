package it.cineca.utils.timbrum.request;

import java.util.Calendar;
import java.util.Date;

//import it.cineca.utils.timbrum.MainActivity;
//import it.cineca.utils.timbrum.R;


public class RecordTimbratura {
	private String time;
	private String dir;
	private boolean enabled = true;
	public RecordTimbratura(String[] strings,String[] headers) {
		int timeIndex =getIndexFor(headers,"TIMETIMBR");
		int dirIndex =getIndexFor(headers,"DIRTIMBR");
		time= strings[timeIndex];
		dir= strings[dirIndex];
	}
	
	//DAYSTAMP, TIMETIMBR, DIRTIMBR, CAUSETIMBR, TYPETIMBR, IPTIMBR
	//@Override
	//public String toString() {
	//	
	//	String message= time + " "+ (isEntry()?MainActivity.instance.getString(R.string.entry):MainActivity.instance.getString(R.string.exit));
	//	return message;
	//}

	private int getIndexFor(String[] headers,String string) {
		for(int i=0;i<=headers.length;i++){
			if(headers[i].equals(string)){
				return i;
			}
		}
		return -1;
	}
	
	public String getTime() {
		return time;
	}
	
	public void switchEnabled(){
		if (isEnabled()){
			enabled = false;
		} else {
			enabled = true;
		}
	}
	
	
	public boolean isEnabled(){
		return enabled;
	}
	
	public String getDirection() {
		return dir;
	}
	
	public void switchDirection(){
		if (this.dir.equals(TimbraturaRequest.VERSO_ENTRATA)){
			this.dir = TimbraturaRequest.VERSO_USCITA;
		} else if (this.dir.equals(TimbraturaRequest.VERSO_USCITA)){
			this.dir = TimbraturaRequest.VERSO_ENTRATA;
		} else{}
	}
	
	
	public boolean isEntry(){
		return dir.equals(TimbraturaRequest.VERSO_ENTRATA);
	}
	public boolean isExit(){
		return !isEntry();
	}
	
	public Date getTimeFor(Date date){
		String time=getTime();
		String[] tokens=time.split(":");
		String hour=tokens[0];
		String minutes=tokens[1];
		Calendar c=Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.AM_PM,Calendar.AM);
		c.set(Calendar.HOUR,Integer.parseInt(hour));
		c.set(Calendar.MINUTE,Integer.parseInt(minutes));
		c.set(Calendar.SECOND,0);
		return c.getTime();
	}

	@Override
	public boolean equals(Object obj) {
		RecordTimbratura other = (RecordTimbratura)obj;
		if (this.enabled == other.enabled && this.time.equals(other.time) && this.dir.equals(other.dir)){
			return true;
		}
		return false;
	}
}
