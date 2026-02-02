package Nhom5.ThachTrongKhang.controllers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
@RequestMapping("/")

public class HomeController {
    @GetMapping
    public String home() {
        return "home/index"; // Trả về tên của view (home.html)
    }
    @GetMapping("/contact")
    public String contact() {
        return "home/contact"; // Trả về tên của view (contact.html)
    }
}
