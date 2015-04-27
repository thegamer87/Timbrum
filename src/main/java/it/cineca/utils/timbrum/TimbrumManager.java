package it.cineca.utils.timbrum;

import it.cineca.utils.timbrum.request.RecordTimbratura;
import it.cineca.utils.timbrum.request.TimbraturaRequest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Period;

public class TimbrumManager {

	private static DateFormat mTimeFormatter = new SimpleDateFormat("HH:mm");
	private static final Period mWorkingDayPeriod = new Period(7, 12, 0, 0);

	public static List<RecordTimbratura> normalizeTimbrature(
			List<RecordTimbratura> pTimbratureList) {
		List<RecordTimbratura> normalizedTimbrature = new ArrayList<RecordTimbratura>();
		for (int i = 0; i < pTimbratureList.size(); ++i) {
			RecordTimbratura timbratura = pTimbratureList.get(i);
			if ((i % 2 == 0 && !timbratura.getDirection().equals(
					TimbraturaRequest.VERSO_ENTRATA))
					|| (i % 2 != 0 && !timbratura.getDirection().equals(
							TimbraturaRequest.VERSO_USCITA))) {

				timbratura.switchDirection();
			}
			normalizedTimbrature.add(i, timbratura);

		}
		return normalizedTimbrature;
	}

	public static TimeBean computeWorkingDay(
			List<RecordTimbratura> pTimbratureList, Date pTime) {

		pTimbratureList = normalizeTimbrature(pTimbratureList);

		Long lWorkedTime = 0L;
		Long lPauseTime = 0L;
		Long lRemainingTime = 0L;

		DateTime lTime = new DateTime(pTime);
		lTime.withSecondOfMinute(0);
		lTime.withMillisOfSecond(0);

		String lTimeString = null;
		Date lDateTimbratura = null;
		DateTime lDateTimeTimbratura = null;

		Long lPeriod = null;
		Long lMillisFromEntrance = 0L;
		boolean isFirst = true;

		for (RecordTimbratura t : pTimbratureList) {

			if (t.isEnabled()) {
				lTimeString = t.getTime();

				try {

					lDateTimbratura = mTimeFormatter.parse(lTimeString);
					lDateTimeTimbratura = new DateTime(lDateTimbratura);
					lDateTimeTimbratura = lDateTimeTimbratura
							.withMillisOfSecond(0).withSecondOfMinute(0)
							.withDayOfMonth(lTime.getDayOfMonth())
							.withMonthOfYear(lTime.getMonthOfYear())
							.withYear(lTime.getYear());

					lPeriod = (lTime.getMillis() - lDateTimeTimbratura
							.getMillis());

					if (isFirst) {
						lMillisFromEntrance = lPeriod;
						isFirst = false;
					}

					if ("E".equals(t.getDirection())) {
						lWorkedTime += lPeriod;
					} else {
						lWorkedTime -= lPeriod;
					}

				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

		}

		lRemainingTime = mWorkingDayPeriod
				.minus(new Period(lWorkedTime.longValue()))
				.toStandardDuration().getMillis();
		lPauseTime = lMillisFromEntrance - lWorkedTime;

		Long extraTime = new Long(1000 * 60 * 30);

		if (lPauseTime < extraTime) {
			lRemainingTime += extraTime;
		}

		TimeBean lBean = new TimeBean(mWorkingDayPeriod, lWorkedTime,
				lRemainingTime, lPauseTime, pTimbratureList, pTime);
		return lBean;
	}

}
