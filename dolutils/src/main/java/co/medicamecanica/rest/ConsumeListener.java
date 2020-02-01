package co.medicamecanica.rest;

import org.restlet.resource.ClientResource;

public interface ConsumeListener<T> {


    void onPostExecute(Integer code);

    Integer doInBackground(T wrap);
}
