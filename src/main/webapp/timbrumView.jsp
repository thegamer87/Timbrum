<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="it.cineca.utils.timbrum.request.RecordTimbratura"%>
<%@page import="java.util.ArrayList"%>
<head>
	<title>ConTempLa</title>
</head>
<html>
<body>
	<h1>ConTempLa (Controllo Tempo Lavorato)</h1>
	<% 
	SimpleDateFormat hhmmSDF = new SimpleDateFormat("HH:mm");
	SimpleDateFormat ggmmyyyySDF = new SimpleDateFormat("dd/MM/yyyy");
	String action = (String)request.getAttribute("action");
	String message = (String)request.getAttribute("message");
	String debug = (String)request.getAttribute("debug");
	ArrayList<String> timbrature = (ArrayList<String>)request.getAttribute("timbrature");
	String tempoLavorato = (String)request.getAttribute("tempoLavorato");
	String tempoMancante = (String)request.getAttribute("tempoMancante");
	String oraUscita = (String)request.getAttribute("oraUscita");
	Date nowDate = new Date();
	String date = (String)request.getAttribute("date");
	if (date == null){
		date = ggmmyyyySDF.format(nowDate);
	}
	if (message != null){
		%>
			<p><%= message %></p>
		<% 	
	}
	if (debug != null){
		%>
			<p>DEBUG: <%= debug %></p>
		<% 	
	}
	if (action == null || action.equals("login")){
	%>
		<form action="Timbrum" method="POST" align="center">
			<table>
				<tr>
					<td>
						Username:
					</td>
					<td>
						<input type="text" name="username"/>
					</td>
				</tr>
				<tr>
					<td>
						Password:
					</td>
					<td>
						<input type="password" name="password"/>
					</td>
				</tr>
				<input type="hidden" name="action" value="login" />
				<tr><td>	
					<input type="submit" value="Submit" />
				</td></tr>
			</table>
		</form>
	<%
	}else{
	%>
		<form action="Timbrum" method="POST">
			<input type="hidden" name="action" value="logout" />
			<input type="submit" value="Logout" />
		</form>
		<br/>
		<form action="Timbrum" method="POST">
			Data timbrature: <input type="text" name="date" value="<%= date %>"/>
			<input type="hidden" name="action" value="update" />
			<input type="submit" value="Aggiorna" />
		</form>			
		<%
		if (!timbrature.isEmpty()){
		%>		
				<table border="1">
					<% 
					for(String timbratura : timbrature){
					%>	
						<tr><td><%= timbratura %></td></tr>	
					<%
					}
					%>
				</table>
				<p>Hai lavorato per <%= tempoLavorato %></p>
				<%
				if (oraUscita != null){
				%>
					<p>Puoi uscire alle <%= oraUscita %> per completare le 7h 12m di lavoro</p>
				<%	
				}
				if (tempoMancante != null){
				%>
					<p>Mancano <%= tempoMancante %> per completare le 7h 12m di lavoro</p>
				<%	
				}
				%>
		<%
		}else{
		%>
			<table border="1">
				<tr><td>Nessuna timbratura</td></tr>
			</table>	
		<%
		}
		%>
	<%	
	}
	%>

</body>
</html>
