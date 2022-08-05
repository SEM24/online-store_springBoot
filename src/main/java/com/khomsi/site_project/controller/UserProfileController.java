package com.khomsi.site_project.controller;

import com.khomsi.site_project.entity.User;
import com.khomsi.site_project.entity.UserInfo;
import com.khomsi.site_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class UserProfileController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping({"/", ""})
    public String getUserInfo(Principal principal, Model model) {
        User user = userRepository.findByLogin(principal.getName());
        UserInfo userInfo = user.getUserInfo();
        model.addAttribute("userDetails", userInfo);
        model.addAttribute("user", user);

        return "/user/user-main";
    }

    @GetMapping("/edit")
    public String showEditPage(Principal principal, Model model) {
        User user = userRepository.findByLogin(principal.getName());
        UserInfo userInfo = user.getUserInfo();
        model.addAttribute("userDetails", userInfo);
        model.addAttribute("user", user);

        return "/user/user-edit";
    }

    @PostMapping("/edit")
    public String editUser(Principal principal, User user, BindingResult bindingResult) {
        User newUser = userRepository.findByLogin(principal.getName());

        if (!user.getPassword().equals("")) {
            if (!passwordEncoder.matches(user.getPassword(), newUser.getPassword())) {
                newUser.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                ObjectError error = new ObjectError("globalError",
                        "The password must not be the same.");
                bindingResult.addError(error);
                System.err.println(bindingResult.hasErrors());
                return "/user/user-edit";
            }
        }
        newUser.getUserInfo().setName(user.getUserInfo().getName());
        newUser.getUserInfo().setSurname(user.getUserInfo().getSurname());
        newUser.getUserInfo().setPhone(user.getUserInfo().getPhone());
        newUser.getUserInfo().setEmail(user.getUserInfo().getEmail());
        userRepository.save(newUser);

        return "redirect:/profile";
    }
}