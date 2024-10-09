package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entities.Role;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //делаем обертку вокруг репозитория чтобы напрямую к нему не обращаться
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username); //достаем пользователя из базы по имени Optional<User>
        if (user == null) {
            throw new UsernameNotFoundException(String.format("user '%s' not found", username));
        }
        //Спрингу нужно знать только имя, пароль и право доступа. Этим заведует UserDetails
        //Наша задача нашего Юзера в БД привеcти к типу Юзера спрингова из UserDetails
        //мы возвращаем спрингового юзера и внутрь кладем нашего
        return new org.springframework.security.core.userdetails
                .User(user.getUsername(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));
    }

    //здесь подается список ролей из Юзера и мы должны преобразовать пачку ролей в пачку Authorities
    //нужно для метода выше
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
    }

    //--------------------DAO-----------------
    @Transactional
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public User findOne(long id) {
        Optional<User> foundUser = userRepository.findById(id);
        return foundUser.orElse(null);
    }

    @Transactional
    public void saveUser(User user) {
        //не забываем после апдейта юзера дать ему роль, иначе не сможет логиниться
        Role userRole = roleRepository.findByName("ROLE_USER");
        user.getRoles().add(userRole);
        userRepository.save(user);
    }

    @Transactional
    public void update(long id, User updatedUser) {
        updatedUser.setId(id);
        userRepository.save(updatedUser);
    }

    @Transactional
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

}
