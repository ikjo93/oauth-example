package ikjo.oauthexample.controller;

import ikjo.oauthexample.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oAuthService;

    @GetMapping("/github-login")
    public void login(@RequestParam String code) {
        String email = oAuthService.authorizeForThirdParty(code);

        // Next step is your turn!!
    }
}
