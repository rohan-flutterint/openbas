package io.openex.rest.user;

import io.openex.database.model.Token;
import io.openex.database.model.User;
import io.openex.database.repository.OrganizationRepository;
import io.openex.database.repository.TokenRepository;
import io.openex.database.repository.UserRepository;
import io.openex.rest.helper.RestBehavior;
import io.openex.rest.user.form.UpdateInfoInput;
import io.openex.rest.user.form.UpdateProfileInput;
import io.openex.rest.user.form.UpdatePasswordInput;
import io.openex.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;

import static io.openex.config.AppConfig.currentUser;
import static io.openex.database.model.User.ROLE_USER;
import static io.openex.database.specification.TokenSpecification.fromUser;
import static io.openex.helper.DatabaseHelper.updateRelationResolver;

@RestController
public class MeApi extends RestBehavior {

    private OrganizationRepository organizationRepository;
    private TokenRepository tokenRepository;
    private UserRepository userRepository;
    private UserService userService;

    @Autowired
    public void setOrganizationRepository(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setTokenRepository(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @RolesAllowed(ROLE_USER)
    @GetMapping("/api/me")
    public User me() {
        return userRepository.findById(currentUser().getId()).orElseThrow();
    }

    @RolesAllowed(ROLE_USER)
    @PutMapping("/api/me/profile")
    public User updateProfile(@Valid @RequestBody UpdateProfileInput input) {
        User currentUser = currentUser();
        User user = userRepository.findById(currentUser.getId()).orElseThrow();
        user.setUpdateAttributes(input);
        user.setOrganization(updateRelationResolver(input.getOrganizationId(), user.getOrganization(), organizationRepository));
        return userRepository.save(user);
    }

    @RolesAllowed(ROLE_USER)
    @PutMapping("/api/me/information")
    public User updateInformation(@Valid @RequestBody UpdateInfoInput input) {
        User currentUser = currentUser();
        User user = userRepository.findById(currentUser.getId()).orElseThrow();
        user.setUpdateAttributes(input);
        return userRepository.save(user);
    }

    @RolesAllowed(ROLE_USER)
    @PutMapping("/api/me/password")
    public User updatePassword(@Valid @RequestBody UpdatePasswordInput input) {
        User currentUser = currentUser();
        User user = userRepository.findById(currentUser.getId()).orElseThrow();
        user.setPassword(userService.encodeUserPassword(input.getPassword()));
        return userRepository.save(user);
    }

    @RolesAllowed(ROLE_USER)
    @GetMapping("/api/me/tokens")
    public List<Token> tokens() {
        return tokenRepository.findAll(fromUser(currentUser().getId()));
    }
}