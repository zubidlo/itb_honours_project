package controllers;

import models.User;
import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import static play.data.Form.form;

public class Application extends Controller {

    public Result index() {
        String email = ctx().session().get("email");
        if (email != null) {
            User user = User.findByEmail(email);
            if (user != null && user.validated) return redirect(routes.Dashboard.index());
            session().clear();
        }
        return ok(index.render(form(Register.class), form(Login.class)));
    }

    public static class Login {

        @Constraints.Required
        public String email;
        @Constraints.Required
        public String password;

        public String validate() {
            User user = User.authenticate(email, password);
            if (user == null) return Messages.get("invalid.user.or.password");
            else if (!user.validated) return Messages.get("account.not.validated.check.mail");
            return null;
        }
    }

    public static class Register {

        @Constraints.Required
        public String email;

        @Constraints.Required
        public String fullname;

        @Constraints.Required
        public String inputPassword;

        public String validate() {
            if (isBlank(email)) return "Email is required";
            if (isBlank(fullname)) return "Full name is required";
            if (isBlank(inputPassword)) return "Password is required";
            return null;
        }

        private boolean isBlank(String input) {
            return input == null ||
                    input.isEmpty() ||
                    input.trim().isEmpty();
        }
    }

    public Result authenticate() {
        Form<Login> loginForm = form(Login.class).bindFromRequest();
        Form<Register> registerForm = form(Register.class);
        if (loginForm.hasErrors()) return badRequest(index.render(registerForm, loginForm));
        else {
            session("email", loginForm.get().email);
            return redirect(routes.Dashboard.index());
        }
    }

    public Result logout() {
        session().clear();
        flash("success", Messages.get("youve.been.logged.out"));
        return redirect(routes.Application.index());
    }
}