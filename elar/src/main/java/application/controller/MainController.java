package application.controller;

import application.security.userPasswords.PasswordManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timofey on 12.10.2016.
 */
@Controller
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);
    @Autowired
    PasswordManager passwordManager;

    @RequestMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login";
    }

    @RequestMapping("/password-error")
    public String passwordError(Model model) {
        model.addAttribute("passwordError", true);
        return "login";
    }

    @RequestMapping("/hello")
    public String hello() {
        return "hello";
    }

    @RequestMapping(value="/enter", method= RequestMethod.POST)
    public String enter(@RequestParam("username") String username, @RequestParam("password") String password) {
        if (!passwordManager.isUserExist(username)) {
            log.info("Попытка пользователя [" + username + "] авторизоваться. Пользователь не найден. ");
            logout();
            return "redirect:/login-error";
        } else if (!passwordManager.isPasswordCorrect(username, password)) {
            log.info("Попытка пользователя [" + username + "] авторизоваться. Введён неверный пароль. ");
            logout();
            return "redirect:/password-error";
        } else {
            authUser(username);
            return "redirect:/hello";
        }
    }

    private void authUser(String username) {
        List<GrantedAuthority> grantedAuths = new ArrayList<>();
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, grantedAuths);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("Пользователь [" + username + "] вошёл в систему. ");
    }

    private void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
