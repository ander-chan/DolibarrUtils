package co.medicamecanica.rest;

import org.restlet.resource.ClientResource;

public interface ConsumeListener {
    Integer doInBackground(ClientResource cr);

    void onPostExecute(Integer code);
}
