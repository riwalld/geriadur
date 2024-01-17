package com.example.geriadur.service.user;

import com.example.geriadur.constants.UserRoleEnum;
import com.example.geriadur.domain.user.UserAccount;
import com.example.geriadur.dto.UserRegistrationDto;
import com.example.geriadur.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
/*
    public UserServiceImpl(UserRepository userRepository) {
        super();
        this.userRepository = userRepository;
    }*/

    @Override
    public void save(UserRegistrationDto registrationDto) {
        String role;
        switch (registrationDto.getRole()) {
            case ("U"):
                role = UserRoleEnum.ROLE_USER.toString();
                break;
            case ("A"):
                role = UserRoleEnum.ROLE_USER + "," + UserRoleEnum.ROLE_ADMIN;
                break;
            default:
                throw new IllegalArgumentException("Please choose an existing role");
        }
        UserAccount userAccount = new UserAccount(
                registrationDto.getFirstName(),
                registrationDto.getLastName(),
                registrationDto.getEmail(),
                registrationDto.getLanguage(),
                BCrypt.hashpw(registrationDto.getPassword(), BCrypt.gensalt()),
                role
        );
        userRepository.save(userAccount);
    }

    public static String getCurrentUserEmail() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return null;
    }

    public ResponseEntity saveScore(int sessionScore) {
        UserAccount userAccount =
                userRepository.findByEmail(getCurrentUserEmail()).get();
        int finalScore = userAccount.getScore() + sessionScore;
        userAccount.setScore(finalScore);
        userRepository.save(userAccount);
        return ResponseEntity.ok().body("Score of the user "+userAccount.getFirstName()+" has been updated");
    }

    public UserAccount getAccountInfo() {
        return userRepository.findByEmail(
                        getCurrentUserEmail()
                )
                .get();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (email == null) {
            throw new UsernameNotFoundException("Please enter an email");
        }
        Optional<UserAccount> opUser = userRepository.findByEmail(email);
        if (opUser.isPresent()) {
            UserAccount userAccount = opUser.get();
            GrantedAuthority authority = new SimpleGrantedAuthority(userAccount.getRole());
            return new org.springframework.security.core.userdetails.User(userAccount.getEmail(), userAccount.getPassword(), Arrays.asList(authority));
        } else throw new RuntimeException("Their is no account with the email:" + email);
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(List<String> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList());
    }

}
