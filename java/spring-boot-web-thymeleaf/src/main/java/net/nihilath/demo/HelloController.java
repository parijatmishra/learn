package net.nihilath.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by parijat on 4/4/16.
 */
@Controller
public class HelloController {

    @RequestMapping("/")
    public String index(
            Model model
    ) {
        model.addAttribute("pageTitle", "Home");
        model.addAttribute("text", "Some text");

        OtherBean bean = new OtherBean();
        model.addAttribute("bean", bean);

        return "hello";
    }
}

class SomeBean {
    private int id = 1;
    private String name = "Some name";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

class OtherBean {
    private String name = "Nicola Tesla";
    private SomeBean someBean = new SomeBean();
    private Date now = new Date();
    private List<String> address = new ArrayList<>(3);
    {
        address.add("First Road");
        address.add("City");
        address.add("Country");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SomeBean getSomeBean() {
        return someBean;
    }

    public void setSomeBean(SomeBean someBean) {
        this.someBean = someBean;
    }

    public Date getNow() {
        return now;
    }

    public void setNow(Date now) {
        this.now = now;
    }

    public List<String> getAddress() {
        return address;
    }

    public void setAddress(List<String> address) {
        this.address = address;
    }

    public String sayHello(String to) {
        return this.name + " says hello to " + to;
    }
}
