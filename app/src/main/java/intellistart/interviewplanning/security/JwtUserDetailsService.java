package intellistart.interviewplanning.security;

import intellistart.interviewplanning.model.user.User;
import intellistart.interviewplanning.model.user.UserRepository;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  public UserDetails loadUserByEmailAndName(String email, String name) {

    JwtUserDetails jwtUserDetails = (JwtUserDetails) loadUserByUsername(email);
    jwtUserDetails.setName(name);

    return jwtUserDetails;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    User user = userRepository.findByEmail(email);

    Set<GrantedAuthority> authorities = new HashSet<>();

    if (user != null) {
      authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    } else {
      authorities.add(new SimpleGrantedAuthority("ROLE_CANDIDATE"));
    }

    return new JwtUserDetails(email, null, passwordEncoder().encode(email), authorities);
  }
}