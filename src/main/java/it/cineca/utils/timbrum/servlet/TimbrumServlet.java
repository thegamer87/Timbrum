package it.cineca.utils.timbrum.servlet;

import it.cineca.utils.timbrum.Timbrum;
import it.cineca.utils.timbrum.TimbrumManager;
import it.cineca.utils.timbrum.TimeBean;
import it.cineca.utils.timbrum.request.LoginRequest.LoginResult;
import it.cineca.utils.timbrum.request.RecordTimbratura;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
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
			String modify = req.getParameter("modify");
			if (modify == null){
				modify = "";
			}
			
			if (action == null || action.equals("login") || action.equals("update")){
				if (modify.equals("switchModifyMode")){
					String modifyMode = (String)req.getSession().getAttribute("modifyMode");
					if (modifyMode == null || modifyMode.equals("true")){
						modifyMode = "false";
						
					} else{
						modifyMode = "true";
					}
					req.getSession().setAttribute("modifyMode", modifyMode);
				}
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

				
				List<RecordTimbratura> timbratureFromSession = null;
				
				String precDate = (String)req.getSession().getAttribute("date");
				if (precDate == null){
					precDate = "";
				}
				TimeBean timeBean = (TimeBean)req.getSession().getAttribute("timeBean");
				if (timeBean != null){
					timbratureFromSession = timeBean.getTimbratureList();
				}
				

				
				List<RecordTimbratura> timbrature;
				if (timbratureFromSession == null || !precDate.equals(dateString)){
					timbrature = timbrum.getReport(date.toDate());
				} else{
					timbrature = timbrum.getReportAndUpdateFromSession(date.toDate(), timbratureFromSession);
				}
				if (modify.contains("switchAbilitato-") || modify.contains("switchVerso-")){
					int toSwitchIndex = Integer.valueOf(modify.split("-")[1]);
					if (modify.contains("switchAbilitato-")){
						timbrature.get(toSwitchIndex).switchEnabled();
					}
					else{
						timbrature.get(toSwitchIndex).switchDirection();
					}
					
				}
				
				timeBean = TimbrumManager.computeWorkingDay( timbrature , new Date(), date.toDate() );
				req.setAttribute("timeBean", timeBean);
				req.getSession().setAttribute("timeBean", timeBean);
				
				req.setAttribute("action","result");
				req.setAttribute("date", dateString);
				req.getSession().setAttribute("date", dateString);
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
			e.printStackTrace();
			req.setAttribute("message", "Errore nel recupero delle timbrature: "+e.getMessage());
			req.setAttribute("action","result");
			req.setAttribute("date", dateString);
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/timbrumView.jsp");
            dispatcher.forward(req, resp);
            return;
		}
	}
	
	
	
}
