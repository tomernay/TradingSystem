package Presentaion;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Welcome {

    @GetMapping("/")
    public String welcome() {
        return "redirect:/t.html";
    }

    @GetMapping("/yair")
    public String yair() {
        return "redirect:/yair.html";
    }


}
