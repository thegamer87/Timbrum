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

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;

/**
 * Created by mbacer on 11/04/14.
 */
public class TimbraturaRequest extends AbstractRequest {

    public static final String VERSO_FIELD   = "verso";
    public static final String VERSO_ENTRATA = "E";
    public static final String VERSO_USCITA  = "U";

    public TimbraturaRequest(HttpClient httpclient, HttpContext context) {
        super(httpclient, context);
    }

    private void timbraVerso(String verso) throws IOException {
    	request = new HttpPost(URI.create(url));
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair(VERSO_FIELD, verso));
        request.setEntity(new UrlEncodedFormEntity(formparams, "UTF-8"));
        HttpResponse response = httpclient.execute(request,context);
        response.getEntity().consumeContent();
        System.out.println("Login form get: " + response.getStatusLine());
    }

    public void entrata() throws IOException {
        timbraVerso(VERSO_ENTRATA);
    }

    public void uscita() throws IOException {
        timbraVerso(VERSO_USCITA);
    }
}
