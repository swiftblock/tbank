package cn.swiftchain.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BankController {

    @RequestMapping("/")
    public Object index() {
        return "index";
    }

    @RequestMapping("/help")
    public Object help() {
        return "help";
    }

}
