<%@page import="it.cineca.utils.timbrum.request.TimbraturaRequest"%>
<%@page import="it.cineca.utils.timbrum.TimeBean"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="it.cineca.utils.timbrum.request.RecordTimbratura"%>
<%@page import="it.cineca.utils.timbrum.TimeBean"%>
<%@page import="java.util.List"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->

<!-- no cache -->
<meta http-equiv="cache-control" content="max-age=0" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="0" />
<meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT" />
<meta http-equiv="pragma" content="no-cache" />

<!-- Bootstrap -->
<link href="css/bootstrap.min.css" rel="stylesheet">

<title>ConTempLa</title>

<style>
body {
	min-height: 2000px;
}

.navbar-static-top {
	margin-bottom: 19px;
}

#logout-btn {
	margin-top: 8px;
}

.progress {
  position: relative;
}

.progress span {
    position: absolute;
    display: block;
    width: 100%;
    color: black;
}
</style>
</head>

<body>
	<nav class="navbar navbar-default navbar-static-top">
		<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed"
					data-toggle="collapse" data-target="#navbar" aria-expanded="false"
					aria-controls="navbar">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="#">ConTempLa</a>
			</div>
			<div class="nav navbar-nav navbar-right">
				<%
					String action = (String)request.getAttribute("action");
					if (action == null || !action.equals("login")){
				%>

				<form action="Timbrum" method="POST">
					<input type="hidden" name="action" value="logout" />
					<button id="logout-btn" type="submit" class="btn btn-default">Logout <span class="glyphicon glyphicon-log-out"></span></button>
				</form>

				<%
					}
				%>


			</div>
			<!--/.nav-collapse -->
		</div>
	</nav>
	<div class="container">

		<%
		
			TimeBean tb = (TimeBean) request.getAttribute("timeBean");
		
			SimpleDateFormat hhmmSDF = new SimpleDateFormat("HH:mm");
			SimpleDateFormat ggmmyyyySDF = new SimpleDateFormat("dd/MM/yyyy");
			String message = (String)request.getAttribute("message");
			String debug = (String)request.getAttribute("debug");
			String modifyMode = (String)request.getSession().getAttribute("modifyMode");
			List<RecordTimbratura> timbrature = ( tb != null ? tb.getTimbratureList() : null ); 
			String tempoLavorato = null;
			String tempoMancante = null;
			String oraUscita = null;
			
			Date nowDate = new Date();
			
			String date = (String)request.getAttribute("date");
			if (date == null){
				date = ggmmyyyySDF.format(nowDate);
			}
			if (message != null){
		%>
		
		<div class="alert alert-danger" role="alert">
			<p><%=message%></p>
		</div>
		<%
			}
			if (debug != null){
		%>
		<div class="alert alert-warning" role="alert">
			<p>DEBUG:<%=debug%></p>
		</div>
		<%
			}
			if (action == null || action.equals("login")){
		%>
				<div class="row">
					<div class="col-md-4">
						<form action="Timbrum" method="POST">
							<div class="form-group">
								<label for="username">Username</label> <input type="email"
									name="username" class="form-control" id="username"
									placeholder="Username">
							</div>
							<div class="form-group">
								<label for="password">Password</label> <input type="password"
									name="password" class="form-control" id="password"
									placeholder="Password">
							</div>
							<input type="hidden" name="action" value="login" />
		
							<button type="submit" class="btn btn-primary">Login <span class="glyphicon glyphicon-log-in"></span></button>
		
						</form>
					</div>
				</div>
		<%
			}else{
		%>
				<br /> <br />
		
				<div class="row">
					<div class="col-md-10">
						<form id="form-timbrature" class="form-inline" action="Timbrum"
							method="POST">
							<div class="form-group">
								<label for="exampleInputName2">Data timbrature</label> <input
									id="data-timbrature" type="text" class="form-control" name="date"
									value="<%=date%>" /> 
									<input type="hidden" name="action" value="update" />
		
							</div>
							<button type="submit" class="btn btn-success">Aggiorna <span class="glyphicon glyphicon-refresh"></span></button>
		
						</form>
						<br />
						<form form id="form-modifica-timbrature" class="form-inline" action="Timbrum"
							method="POST">
							<input type="hidden" name="date" value=<%=date%> />
							<input type="hidden" name="action" value="update" />
							<input type="hidden" name="modify" value="switchModifyMode" />
							<button type="submit" class="btn btn-success">Abilita/Disabilita modalita' modifica </button>
						</form>
					</div>
				</div>
				<br /> <br />
				<div class="row">
					<div class="col-md-6">
						<%
							if (timbrature != null && !timbrature.isEmpty()){
						%>
		
						<p class="bg-primary text-center">
							Hai lavorato per <strong><%= tb.prettyPrint( tb.getWorkedPeriod() ) %></strong>
						</p>
						<div class="progress text-center">
							<div id="progress-bar" class="progress-bar progress-bar-<%= tb.isToday( date ) ? "info" : "success" %> progress-bar-striped <%= tb.isToday( date ) ? "active" : "" %> text-center" role="progressbar" aria-valuenow="<%= tb.getWorkedPercent() %>" aria-valuemin="0" aria-valuemax="100" style="width: 0%;">
								<span><strong id="percent">0%</strong></span>
							 </div>
							 
							 <!-- http://stackoverflow.com/questions/12937470/twitter-bootstrap-center-text-on-progress-bar -->
							
						</div>
						<table id="timbrature-table" class="table table-striped table-bordered table-hover">
							<%
								for(int i=0; i<timbrature.size(); ++i){
									RecordTimbratura timbratura = timbrature.get(i);
							%>
							<tr>
								<td><span class="glyphicon glyphicon-triangle-<%= (TimbraturaRequest.VERSO_ENTRATA.equals(timbratura.getDirection()) ? "right" : "left") %>"></span> <%=timbratura.getDirection()%></td>
								<td><%=timbratura.isEnabled() ? timbratura.getTime() : "<del>"+timbratura.getTime()+"</del>"%></td>
								<% 
									if (modifyMode != null && modifyMode.equals("true")){
										
								%>
								<td>
									<form form id="form-modifica-timbrature" class="form-inline" action="Timbrum"
											method="POST">
										<input type="hidden" name="date" value=<%=date%> />
										<input type="hidden" name="action" value="update" />
										<input type="hidden" name="modify" value=<%= "switchAbilitato-"+i %> />
										<button type="submit" class="btn btn-success">Abilita/Disabilita </button>
									</form>
								</td>
								<td>
									<form action="Timbrum">
										<input type="hidden" name="date" value=<%=date%> />
										<input type="hidden" name="action" value="update" />
										<input type="hidden" name="modify" value=<%= "switchVerso-"+i %> />
										<button type="submit" class="btn btn-success">Cambia verso </button>
									</form>
								</td>
								<%
									}
								%>
							</tr>
							<%
								}
							%>
						</table>
						<!-- 
						worked <%= tb.getWorkedPeriod().getHours() %> <%= tb.getWorkedPeriod().getMinutes() %>
						pause <%= tb.getPausePeriod().getHours() %> <%= tb.getPausePeriod().getMinutes() %>
						remaining <%= tb.getRemainingPeriod().getHours() %> <%= tb.getRemainingPeriod().getMinutes() %>
						forecast <%= tb.prettyPrint( tb.getExitForecast() )  %>
						percent <%= tb.getWorkedPercent() %>
						isToday <%= ( tb.isToday( date ) ? "true" : "false") %>
						-->
					
						
						<!-- div class="btn-group">
							<button class="btn btn-warning btn-sm dropdown-toggle" data-toggle="dropdown">
								<i class="glyphicon glyphicon-align-justify"></i> Export Table Data
							</button>
							<ul class="dropdown-menu" role="menu">
								<li><a href="#" onclick="$('#timbrature-table').tableExport({type:'csv',escape:'false'});"> <img src="icons/csv.png" width="24px"> CSV</a></li>
								<li><a href="#" onclick="$('#timbrature-table').tableExport({type:'excel',escape:'false'});"> <img src="icons/xls.png" width="24px"> XLS</a></li>
								<li class="divider"></li>
								<li><a href="#" onclick="$('#timbrature-table').tableExport({type:'json',escape:'false'});"> <img src="icons/json.png" width="24px"> JSON</a></li>
								<li><a href="#" onclick="$('#timbrature-table').tableExport({type:'xml',escape:'false'});"> <img src="icons/xml.png" width="24px"> XML</a></li>
								<li class="divider"></li>
								<li><a href="#" onclick="$('#timbrature-table').tableExport({type:'pdf',pdfFontSize:'7',escape:'false'});"> <img src="icons/pdf.png" width="24px"> PDF</a></li>
							</ul>
						</div -->
						<%
							
							if ( tb.isToday( date ) && tb.isDayFinished() ){
						%>
						<p class="bg-success">Complimenti !!! Anche oggi hai portato a casa la
							pagnotta. Ma ora la domanda sorge spontanea ... che ci fai
							ancora su quella sedia???</p>
						<%
							}
							if ( tb.isToday( date) ) {
						%>
						<p class="text-warning text-center">
							Puoi uscire alle <strong><%= tb.prettyPrint( tb.getExitForecast() ) %></strong> per completare
							le 7h 12m di lavoro
						</p>
						<%
							}
							
								
							if (tempoMancante != null){
						%>
						<p class="text-info"><span class="glyphicon glyphicon-refresh"></span>
							Mancano
							<%=tempoMancante%>
							per completare le 7h 12m di lavoro
						</p>
						<%
							}
						%>
		
						<%
							}else{
						%>
						<table class="table table-bordered table-hover">
							<tr>
								<td>Nessuna timbratura</td>
							</tr>
						</table>
						<%
							}
						%>
					</div>
				</div>
		<%
			}
		%>
	</div>
	<div class="row">
		<div class="col-md-6">
			<p class="text-primary text-right"><span class="
glyphicon glyphicon-envelope"></span>
				<a href="https://github.com/thegamer87/Timbrum/issues"><strong>Report a bug</strong></a>
			</p>
		</div>
	</div>

	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/jquery-easing/1.3/jquery.easing.min.js"></script>
	<!-- Include all compiled plugins (below), or include individual files as needed -->
	<script src="js/bootstrap.min.js"></script>
	<script src="js/bootstrap-datepicker.js"></script>
	<script src="js/timbrum.js"></script>
	<!-- script type="text/javascript" src="js/tableExport.js"></script -->
	<!-- script type="text/javascript" src="js/jquery.base64.js"></script --> <!--  http://ngiriraj.com/pages/htmltable_export/demo.php#  -->
	<!-- script type="text/javascript" src="jspdf/libs/sprintf.js"></script -->
	<!-- script type="text/javascript" src="jspdf/jspdf.js"></script -->
	<!-- script type="text/javascript" src="jspdf/libs/base64.js"></script -->
	<script type="text/javascript" src="js/jquery.animateNumber.min.js"></script>
	<script>
		jQuery(document).ready(function() {
			
			var tag = "timbrumNotification"; // TODO: leggere da bean
			
			$('#data-timbrature').datepicker({
				'format' : 'dd/mm/yyyy'
			});
			
			<% if ( tb != null ) { %>
	 
				$('#progress-bar').animate({"width": "<%= tb.getWorkedPercent() %>%" }, 250);
				$('#percent').animateNumber({
									number: <%= tb.getWorkedPercent() %>,
									numberStep: $.animateNumber.numberStepFactories.append('%'),
									easing: 'easeInLinear' 
				});
			<% } %>
			
			var dayFinished = <%= ( tb == null ? false : ( tb.isToday( date ) && tb.isDayFinished() ) ) %>;
			
			notifyMe( dayFinished, tag );
			
			setInterval(function() {
				
				$('#form-timbrature').submit();
	
				notifyMe( dayFinished, tag );
	
			}, 300000); // 5 minutes
	});</script>

</body>
</html>
