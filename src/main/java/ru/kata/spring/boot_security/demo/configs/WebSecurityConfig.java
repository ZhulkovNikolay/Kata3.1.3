package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import ru.kata.spring.boot_security.demo.services.UserService;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    // private final SuccessUserHandler successUserHandler;
    //  public WebSecurityConfig(SuccessUserHandler successUserHandler) {
    //      this.successUserHandler = successUserHandler;
    // }

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                //.antMatchers("/", "/index").permitAll()
                .antMatchers("/authenticated/**").authenticated()
                //роль и ауторити по сути одно и то же, разница лишь в префиксе, который дописываетс крытно Спринг
                .antMatchers("/only_for_admins/**").hasRole("ADMIN")
                .antMatchers("/read_profile/**").hasAuthority("READ_PROFILE")
                //.anyRequest().authenticated()
                .and()
                //если пользователь прошел аутентификацию, то мы должны выполнить ему преднастройку
                //.formLogin().successHandler(successUserHandler)
                .formLogin()
                .and()
                //.permitAll()
                //.and()
                //.httpBasic();
                .logout().logoutSuccessUrl("/");
        //  .permitAll();
    }


    //  @Bean
    //подключаемся к БД и от туда берем наших Юзеров
//    public JdbcUserDetailsManager userDetailsManager(DataSource dataSource) {
//        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
//        return jdbcUserDetailsManager;
//    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    //Данный метод проверяет существует ли такой пользователь или нет
    //и если да, то кладет его в секюрити контекст
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder()); //прогоняет через шифрование
        daoAuthenticationProvider.setUserDetailsService(userService); //предоставляет самих юзеров
        return daoAuthenticationProvider;
    }

}