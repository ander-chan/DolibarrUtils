package co.medicamecanica.rest;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExampleInstrumentedTest {
    @Test
    public void login() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        RestClient.conf(appContext);
        System.out.println("Start");
        new RestClient.UserLoginTask("http://192.168.1.11/dolibarr-6/htdocs", "ander", "ander",
                new RestClient.LoginListener() {
                    @After
                    @Override
                    public void onPostExecute(Integer code) {
                        System.out.println();
                        assertEquals("" + code, "" + 200);
                    }

                    @Override
                    public void onCancelled() {

                    }
                }).execute();
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        RestClient.ConsumeWSTask<ProductResource> consumeWSTask = new RestClient.ConsumeWSTask<ProductResource>(new ConsumeListener<ProductResource>() {
            @Override
            public void onPostExecute(Integer integer) {
                assertEquals((int) integer, 300);
            }

            @Override
            public Integer doInBackground(ProductResource productResource) {
                Product[] products = productResource.list();

                System.out.println(products);
                Log.d("productos", products.toString());

                return 200;
            }
        });
        consumeWSTask.wrap(ProductResource.class);
        consumeWSTask.addSegment("products");
        consumeWSTask.execute();
        //assertEquals("co.medicamecanica.rest.test", appContext.getPackageName());
    }

}
