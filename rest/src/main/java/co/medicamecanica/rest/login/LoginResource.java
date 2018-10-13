package co.medicamecanica.rest.login;

import org.restlet.resource.Get;

interface LoginResource {
    @Get
    public Login login();

}
