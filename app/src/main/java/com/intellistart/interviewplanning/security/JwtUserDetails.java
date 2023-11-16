package com.intellistart.interviewplanning.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class JwtUserDetails implements UserDetails {

  private final String email;
  private String name;
  private final String password;
  private final Collection<? extends GrantedAuthority> authorities;
  private final Boolean enabled;
  private final Boolean accountNonExpired;
  private final Boolean accountNonLocked;
  private final boolean credentialsNonExpired;

  public JwtUserDetails(String email, String name, String password,
      Collection<? extends GrantedAuthority> authorities) {
    this(email, name, password, authorities, true, true, true, true);
  }

  public JwtUserDetails(String email, String name, String password,
      Collection<? extends GrantedAuthority> authorities, Boolean enabled,
      Boolean accountNonExpired,
      Boolean accountNonLocked, boolean credentialsNonExpired) {
    this.email = email;
    this.name = name;
    this.password = password;
    this.authorities = authorities;
    this.enabled = enabled;
    this.accountNonExpired = accountNonExpired;
    this.accountNonLocked = accountNonLocked;
    this.credentialsNonExpired = credentialsNonExpired;
  }

  public String getEmail() {
    return email;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return accountNonExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return accountNonLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return credentialsNonExpired;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }
}
