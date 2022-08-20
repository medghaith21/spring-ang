package margoumi.com.margoumi.service;

import margoumi.com.margoumi.exceptions.AuthorizationException;
import margoumi.com.margoumi.exceptions.ObjectNotFoundException;
import margoumi.com.margoumi.models.Role;
import margoumi.com.margoumi.models.User;
import margoumi.com.margoumi.repository.UserRepository;
import margoumi.com.margoumi.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    @Transactional
    public void makeAdmin(String email){
        userRepository.updateUserRole(email, Role.ADMIN);
    }

    public static UserPrincipal clientAuthenticated() {
        try {

            return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        } catch (Exception e) {
            return null;
        }
    }

    public User findById(long id)  {

        UserPrincipal user = UserService.clientAuthenticated();

        if (user == null || !user.getId().equals(id)) {
            throw new AuthorizationException();
        }
        Optional<User> obj = userRepository.findById(id);

        try {
            return obj.get();
        } catch (NoSuchElementException e) {
            throw new ObjectNotFoundException();
        }

    }

    @Transactional
    public User update(User obj) throws Exception {
        UserPrincipal user = UserService.clientAuthenticated();

        User cli = findById(user.getId());

        if (user == null || !user.getId().equals(cli.getId())) {
            throw new AuthorizationException();
        }

        cli.setEmail(obj.getEmail());
        cli.setFname(obj.getFname());
        cli.setPassword(passwordEncoder.encode(obj.getPassword()));
        cli.setLname(obj.getLname());
        cli.setAdress(obj.getAdress());
        cli.setPhone(obj.getPhone());

        if (userRepository.findByEmail(cli.getEmail()) == null) {
            try {
                return userRepository.save(cli);
            } catch (Exception e) {
                throw new Exception();
            }
        }
        return cli;
    }

}
