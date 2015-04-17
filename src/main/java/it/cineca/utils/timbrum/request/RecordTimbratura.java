/**
    TimbrumWEB aka Contempla aka Controllo Tempo Lavorato
    Copyright (C) 2020  Marco Verrocchio

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
**/

package it.cineca.utils.timbrum.request;

import java.util.Calendar;
import java.util.Date;

//import it.cineca.utils.timbrum.MainActivity;
//import it.cineca.utils.timbrum.R;


public class RecordTimbratura {
	private String time;
	private String dir;
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
	
	public String getDirection() {
		return dir;
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
}
