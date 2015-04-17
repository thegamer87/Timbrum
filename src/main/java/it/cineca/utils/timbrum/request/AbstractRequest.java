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

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

/**
 * Created by mbacer on 23/04/14.
 */
public class AbstractRequest {

    protected HttpClient httpclient;
    protected String              url;
    protected HttpContext context;
    protected HttpPost request;

    public AbstractRequest(HttpClient httpclient, HttpContext context) {
        this.httpclient = httpclient;
        HttpParams httpParameters = httpclient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
        HttpConnectionParams.setSoTimeout(httpParameters, 10000);
		this.context = context;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
