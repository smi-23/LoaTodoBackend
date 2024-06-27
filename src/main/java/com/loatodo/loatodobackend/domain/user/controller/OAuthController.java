package com.loatodo.loatodobackend.domain.user.controller;

import com.loatodo.loatodobackend.domain.user.dto.SignupRequestDto;
import com.loatodo.loatodobackend.domain.user.entity.User;
import com.loatodo.loatodobackend.domain.user.service.UserService_temp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;
import java.util.Iterator;

@Controller
@RequiredArgsConstructor
@RequestMapping("/oauth-login")
public class OAuthController {
    private final UserService_temp userService;

    @GetMapping(value = {"", "/"})
    public String home(Model model) {

        model.addAttribute("loginType", "oauth-login");
        model.addAttribute("pageName", "oauth 로그인");

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();

        User loginUser = userService.getLoginUserByUsername(username);

        if (loginUser != null) {
            model.addAttribute("name", loginUser.getName());
        }

        return "home";
    }

    @GetMapping("/join")
    public String joinPage(Model model) {

        model.addAttribute("loginType", "oauth-login");
        model.addAttribute("pageName", "oauth 로그인");

        // 회원가입을 위해서 model 통해서 joinRequest 전달
        model.addAttribute("joinRequest", new SignupRequestDto());
        return "join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute SignupRequestDto joinRequest,
                       BindingResult bindingResult, Model model) {

        model.addAttribute("loginType", "oauth-login");
        model.addAttribute("pageName", "oauth 로그인");

        // ID 중복 여부 확인
        if (userService.checkLoginIdDuplicate(joinRequest.getUsername())) {
            bindingResult.addError(new FieldError("joinRequest", "loginId", "ID가 존재합니다."));
        }


        // 비밀번호 = 비밀번호 체크 여부 확인
        if (!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())) {
            bindingResult.addError(new FieldError("joinRequest", "passwordCheck", "비밀번호가 일치하지 않습니다."));
        }

        // 에러가 존재할 시 다시 join.html로 전송
        if (bindingResult.hasErrors()) {
            return "signup";
        }

        // 에러가 존재하지 않을 시 joinRequest 통해서 회원가입 완료
        userService.securitySignup(joinRequest);

        // 회원가입 시 홈 화면으로 이동
        return "redirect:/oauth-login";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {

        model.addAttribute("loginType", "oauth-login");
        model.addAttribute("pageName", "oauth 로그인");

        model.addAttribute("loginRequest", new SignupRequestDto());
        return "login";
    }



    @GetMapping("/info")
    public String memberInfo(Authentication auth, Model model) {
        model.addAttribute("loginType", "oauth-login");
        model.addAttribute("pageName", "oauth 로그인");

        User loginMember = userService.getLoginUserByUsername(auth.getName());

        model.addAttribute("user", loginMember);
        return "info";
    }
    @GetMapping("/admin")
    public String adminPage(Model model) {

        model.addAttribute("loginType", "oauth-login");
        model.addAttribute("pageName", "oauth 로그인");

        return "admin";
    }
}
