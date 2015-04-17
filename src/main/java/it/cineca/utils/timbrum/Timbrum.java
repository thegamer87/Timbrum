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
package it.cineca.utils.timbrum;

import it.cineca.utils.timbrum.request.LoginRequest;
import it.cineca.utils.timbrum.request.LoginRequest.LoginResult;
import it.cineca.utils.timbrum.request.RecordTimbratura;
import it.cineca.utils.timbrum.request.ReportRequest;
import it.cineca.utils.timbrum.request.TimbraturaRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;


/**
 * Created by mbacer on 23/04/14.
 */
public class Timbrum {

	private static Logger logger = Logger.getLogger("it.cineca.utils.timbrum.Timbrum");
	
    public static String LOGIN_URL             = "/servlet/cp_login";
    public static String TIMBRUS_URL           = "/servlet/ushp_ftimbrus";
    public static String SQL_DATA_PROVIDER_URL = "/servlet/SQLDataProviderServer";

    private final String              username;
    private final String              password;
	private BasicHttpContext context;
	private String host;

    public Timbrum(String host, String username, String password) {
        this.host = host;
		this.username = username;
        this.password = password;
        
        context=new BasicHttpContext();
        BasicCookieStore cookieStore=new BasicCookieStore();
        context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
    }

    public ArrayList<RecordTimbratura> getReport(Date date) throws Exception {
        ReportRequest report = new ReportRequest( new DefaultHttpClient(),context);
        report.setUrl(host+SQL_DATA_PROVIDER_URL);
        //return report.getTimbrature(new Date());
        return report.getTimbrature(date);
    }

    public LoginResult login() throws IOException {
        LoginRequest login = new LoginRequest( new DefaultHttpClient(),context);
        login.setUrl(host+LOGIN_URL);
        login.setUsername(username);
        login.setPassword(password);
        return login.submit();
    }


    public void timbra(String verso) throws IOException {
        TimbraturaRequest timbratura = new TimbraturaRequest(new DefaultHttpClient(),context);
        timbratura.setUrl(host+TIMBRUS_URL);
        if (TimbraturaRequest.VERSO_ENTRATA.equals(verso)) {
           timbratura.entrata();
        } else {
           timbratura.uscita();
        }
    }

}
