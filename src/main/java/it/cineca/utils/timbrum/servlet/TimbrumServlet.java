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

package it.cineca.utils.timbrum.servlet;

import it.cineca.utils.timbrum.Timbrum;
import it.cineca.utils.timbrum.request.RecordTimbratura;
import it.cineca.utils.timbrum.request.LoginRequest.LoginResult;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class TimbrumServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4190449050840404077L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		DateTimeFormatter hhmmDTF = DateTimeFormat.forPattern("HH:mm");
		DateTimeFormatter ggmmyyyyDTF = DateTimeFormat.forPattern("dd/MM/yyyy");
		PeriodFormatter periodFormatter = new PeriodFormatterBuilder().
											appendHours().appendSuffix("h").
											appendSeparator(" ").
											appendMinutes().appendSuffix("m").toFormatter();
		DateTime nowDate = new DateTime();
		String dateString = ggmmyyyyDTF.print(nowDate);
		try {
			String action = req.getParameter("action");
			if (action == null || action.equals("login") || action.equals("update")){
				Timbrum timbrum = (Timbrum)req.getSession().getAttribute("timbrum");
				if (timbrum == null){
					String username = req.getParameter("username");
					String password = req.getParameter("password");
					if (username == null || username.isEmpty() || 
							password == null || password.isEmpty()){
						
						req.setAttribute("action","login");
						RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/timbrumView.jsp");
			            dispatcher.forward(req, resp);
			            return;
					}
					timbrum = new Timbrum("https://hr.cineca.it/HRPortal", username, password);
					LoginResult loginResult = timbrum.login();
					if (!loginResult.isSuccess()){
						req.setAttribute("message", "Errore nel login");
						req.setAttribute("action","login");
						RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/timbrumView.jsp");
			            dispatcher.forward(req, resp);
			            return;
					}
					req.getSession().setAttribute("timbrum", timbrum);
				}

				dateString = req.getParameter("date");
				DateTime date;
				
				if (dateString != null && !dateString.isEmpty()){
					date = ggmmyyyyDTF.parseDateTime(dateString);
				}
				else{
					date = nowDate;
				}

				ArrayList<String> timbratureString = new ArrayList<String>();
				ArrayList<RecordTimbratura> timbrature = timbrum.getReport(date.toDate());
				Period workedTime = Period.ZERO;
				Period exitTime = Period.ZERO;;
				Period totalDayWorkTime = Period.hours(7).withMinutes(12);
				Period remainingTime = Period.ZERO;
				DateTime outTimeDate;
				
				if (timbrature.size() > 0){
					RecordTimbratura timbratura = null;
					for (int i=0; i < timbrature.size(); i++){
						timbratura = timbrature.get(i);
						//Costruzione stringa timbratura
						String direzione;
						if (timbratura.getDirection().equals("E")){
							direzione = "Entrata";
						}
						else if (timbratura.getDirection().equals("U")){
							direzione = "Uscita";
						}
						else{
							direzione = "N/D";
						}
						String t = direzione+" alle "+timbratura.getTime();
						timbratureString.add(t);
						//Calcolo tempo lavorato
						DateTime timbraturaDate = hhmmDTF.parseDateTime(timbratura.getTime());
						if (i>0){
							RecordTimbratura precTimbratura = timbrature.get(i-1);
							//Tempo lavoro
							if (precTimbratura.getDirection().equals("E") && timbratura.getDirection().equals("U")){
								DateTime precTimbraturaDate = hhmmDTF.parseDateTime(precTimbratura.getTime());
								Period periodBeetwenTimbr = new Period(precTimbraturaDate, timbraturaDate);
								workedTime = workedTime.plus(periodBeetwenTimbr);					
							}
							//Tempo uscita
							if (precTimbratura.getDirection().equals("U") && timbratura.getDirection().equals("E")){
								DateTime precTimbraturaDate = hhmmDTF.parseDateTime(precTimbratura.getTime());
								Period periodBeetwenTimbr = new Period(precTimbraturaDate, timbraturaDate);
								exitTime = exitTime.plus(periodBeetwenTimbr);								
							}
						}
						if (i == timbrature.size() - 1){
							if (timbratura.getDirection().equals("E")){
								Period periodFromTimbrToNow = new Period(timbraturaDate, nowDate);
								workedTime = workedTime.plus(periodFromTimbrToNow);	
							}
							if (timbratura.getDirection().equals("U")){
								Period periodFromTimbrToNow = new Period(timbraturaDate, nowDate);
								exitTime = exitTime.plus(periodFromTimbrToNow);
							}
						}
					}
					
					if (exitTime.getYears() == 0 && exitTime.getMonths() == 0 && 
							exitTime.getWeeks() == 0 && exitTime.getDays() == 0 && 
							exitTime.getMinutes() < 30){
						remainingTime = remainingTime.plus(Period.minutes(30));
					}
				}
		
				//Calcolo ora di uscita e tempo mancante
				Period remainingTimePartial = totalDayWorkTime.minus(workedTime);
				System.out.println("totalDayWorkTime = "+periodFormatter.print(totalDayWorkTime));
				System.out.println("workedTime = "+periodFormatter.print(workedTime));
				System.out.println("remainingTimePartial = "+periodFormatter.print(remainingTimePartial));
				remainingTime = remainingTime.plus(remainingTimePartial);
				System.out.println("final remainingTime = "+periodFormatter.print(remainingTime));
				System.out.println("final remainingTimeNormalized = "+periodFormatter.print(remainingTime.normalizedStandard()));				
				outTimeDate = nowDate.plus(remainingTime);
				if (date.getYear() == nowDate.getYear() && date.getDayOfYear() == nowDate.getDayOfYear()){
					//req.setAttribute("tempoMancante",periodFormatter.print(remainingTime.toPeriod().normalizedStandard()));
					req.setAttribute("oraUscita",hhmmDTF.print(outTimeDate));					
				}

				req.setAttribute("timbrature", timbratureString);
				req.setAttribute("tempoLavorato", periodFormatter.print(workedTime.toPeriod().normalizedStandard()));
				
				req.setAttribute("action","result");
				req.setAttribute("date", dateString);
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/timbrumView.jsp");
	            dispatcher.forward(req, resp);
			}
			else if (action.equals("logout")){
				req.getSession().invalidate();
				req.setAttribute("action","login");
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/timbrumView.jsp");
	            dispatcher.forward(req, resp);
	            return;
			}
			else{
				throw new ServletException("action non supportata");
			}


		} catch (Exception e) {
			req.setAttribute("message", "Errore nel recupero delle timbrature: "+e.getMessage());
			req.setAttribute("action","result");
			req.setAttribute("date", dateString);
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/timbrumView.jsp");
            dispatcher.forward(req, resp);
            return;
		}
	}
	
	
	
}
