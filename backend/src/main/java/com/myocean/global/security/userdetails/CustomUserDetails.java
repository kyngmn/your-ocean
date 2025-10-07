package com.myocean.global.security.userdetails;

import com.myocean.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;

    private CustomUserDetails(User user) {
        this.user = user;
    }

    public static CustomUserDetails from(User user) {
        return new CustomUserDetails(user);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return null; // OAuth 사용자이므로 password 없음
    }

    @Override
    public String getUsername() {
        return user.getId().toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // AuthUtil에서 사용할 메서드
    public Integer getUserId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }
}