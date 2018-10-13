package co.medicamecanica.rest.login;

import org.restlet.resource.Get;

public interface UserResource {
    @Get
    public User[] retrive();
}
