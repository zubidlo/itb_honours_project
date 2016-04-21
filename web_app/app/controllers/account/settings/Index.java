package controllers.account.settings;

import controllers.Secured;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

public class Index extends Controller {

	@Security.Authenticated(Secured.class)
    public Result index() {
        return new Password().index();
    }
}
