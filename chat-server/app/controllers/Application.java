package controllers;

import java.util.Date;

import javax.inject.Singleton;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

@Singleton
public class Application extends Controller {

    public static Result index() {
        return ok(Json.toJson(new Date()));
    }

}