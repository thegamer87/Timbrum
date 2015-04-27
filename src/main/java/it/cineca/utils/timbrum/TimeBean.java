package it.cineca.utils.timbrum;

import it.cineca.utils.timbrum.request.RecordTimbratura;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

public class TimeBean {
	
	private DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	private Long mWorkedTime;
	private Long mRemainingTime;
	private Long mPauseTime;
	private Period mWorkingDayPeriod;
	private List<RecordTimbratura> mTimbratureList;
	private Date mTimbratureDate;
	
	
	public TimeBean( Period pWorkingDayPeriod, Long pWorkedTime, Long pRemainingTime, Long pPauseTime, List<RecordTimbratura> pTimbratureList, Date pTimbratureDate ) {
		this.mWorkedTime = pWorkedTime;
		this.mPauseTime = pPauseTime;
		this.mRemainingTime = pRemainingTime;
		this.mWorkingDayPeriod = pWorkingDayPeriod;
		this.mTimbratureList = pTimbratureList;
		this.mTimbratureDate = pTimbratureDate;
	
	}
	
	public String prettyPrint( DateTime dt ) {
		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
			.appendHourOfDay(2)
			.appendLiteral(":")
			.appendMinuteOfHour(2)
			.toFormatter();
	
		return formatter.print( dt );
	}
	
	public String prettyPrint( Period p ) {
		StringBuffer sb = new StringBuffer();
		sb.append( p.getHours() )
			.append("h")
			.append(" ")
			.append( p.getMinutes() )
			.append("m");
		return sb.toString();
		
	}
		
	public Period getWorkedPeriod() {
		return new Period( mWorkedTime.longValue() );
	}
	
	public Period getRemainingPeriod() {
		return new Period( mRemainingTime.longValue() );
	}
	
	public Period getPausePeriod() {
		return new Period( mPauseTime.longValue() );
	}
	
	public Integer getWorkedPercent() {
		Integer lPercent = 0;
		
		if ( mWorkedTime != null ) {
			lPercent = Math.round( ( mWorkedTime * 100 ) / ( mWorkingDayPeriod.toStandardDuration().getMillis() ) ); 
		}
		
		return ( lPercent > 100 ? 100 : lPercent);
	}
	
	public DateTime getExitForecast() {
		
		DateTime now = DateTime.now();
		DateTime forecast = now.plus( new Period( mRemainingTime.longValue() ) );
		return forecast;
	}
	
	public List<RecordTimbratura> getTimbratureList() {
		return this.mTimbratureList;
	}
	
	public boolean isDayFinished() {
		return ( ( mWorkedTime >= mWorkingDayPeriod.toStandardDuration().getMillis() ) ? true : false );
	}
	
	
	public boolean isToday( String pDate ) {
		boolean lIsToday = false;
		
		Date lDate = null;
		try {
			lDate = df.parse(pDate);
		} catch (ParseException e) {
			lDate = null;
		}
				
		lIsToday = ( lDate == null ? false : ( lDate.equals( getTodayDate() ) ) );
		return lIsToday;
	}
	
	private Date getTodayDate() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.HOUR_OF_DAY, 0);
		Date lToday = c.getTime();
		return lToday;
	}
	
	private Date getTimbratureDate(){
		return mTimbratureDate;
	}
	
	private void setTimbratureDate(Date pTimbratureDate){
		mTimbratureDate = pTimbratureDate;
	}
	
}
