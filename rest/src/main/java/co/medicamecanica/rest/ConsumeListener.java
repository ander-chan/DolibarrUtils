package co.medicamecanica.rest;

import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

interface ConsumeListener {
    Integer doInBackground(ClientResource cr);

    void onPostExecute(Integer code);
}
