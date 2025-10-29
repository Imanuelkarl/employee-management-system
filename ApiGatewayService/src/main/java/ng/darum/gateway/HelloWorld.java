package ng.darum.gateway;

import ng.darum.gateway.components.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorld {
    @Autowired
    JwtUtil jwtUtil ;
    @GetMapping
    public String welcome(){
        return "Hello World";
    }
    @GetMapping("/test")
    public String testAuth(Authentication auth, @RequestBody TokenUtil token) {
        System.out.println(auth.toString());
        System.out.println(jwtUtil.extractEmail(token.getToken()));
        System.out.println("Authorities: " + auth.getAuthorities());
        System.out.println("Final Check for token roles include: "+jwtUtil.validateToken(token.getToken()).get("role"));

        return "ok";
    }
}
