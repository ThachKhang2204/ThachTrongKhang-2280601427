package Nhom5.ThachTrongKhang.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import Nhom5.ThachTrongKhang.entities.User;
import Nhom5.ThachTrongKhang.repositories.IUserRepository;
import Nhom5.ThachTrongKhang.repositories.IRoleRepository;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = { Exception.class, Throwable.class })
    public void save(@NotNull User user) {
        user.setPassword(new BCryptPasswordEncoder()
                .encode(user.getPassword()));
        userRepository.save(user);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = { Exception.class, Throwable.class })
    public void setDefaultRole(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.getRoles().add(roleRepository.findRoleById(Nhom5.ThachTrongKhang.constants.Role.USER.value));
            userRepository.save(user);
        });
    }

    public Optional<User> findByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public void saveOauthUser(String email, @NotNull String name) {
        if (userRepository.findByEmail(email).isPresent())
            return;
        var user = new User();
        user.setUsername(email);
        user.setEmail(email);
        user.setPassword(new BCryptPasswordEncoder().encode(name));
        user.setProvider("GOOGLE");
        userRepository.save(user);
    }
}