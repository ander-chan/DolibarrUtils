package co.medicamecanica.rest;

import org.restlet.resource.Get;

public interface ProductResource {
    @Get
   public  Product[] list();
}
