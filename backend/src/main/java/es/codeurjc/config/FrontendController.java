package es.codeurjc.config;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class FrontendController {
     @RequestMapping(
            method = RequestMethod.GET,
            value = {"/{path:[^\\.]*}"})
    public String forward() {
        return "forward:/index.html";
    }
}