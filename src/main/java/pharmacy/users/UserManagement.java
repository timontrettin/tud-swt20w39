package pharmacy.users;

import org.salespointframework.useraccount.Password.UnencryptedPassword;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManagement;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@Transactional
public class UserManagement {

	public static final Role CUSTOMER_ROLE = Role.of("CUSTOMER");

	private final UserRepository users;
	private final UserAccountManagement userAccounts;

	UserManagement(UserRepository users, UserAccountManagement userAccounts) {

		Assert.notNull(users, "UserRepository must not be null!");
		Assert.notNull(userAccounts, "UserAccountManagement must not be null!");

		this.users = users;
		this.userAccounts = userAccounts;
	}

	public User createUser(RegistrationForm form) {

		Assert.notNull(form, "Registration form must not be null!");

		var password = UnencryptedPassword.of(form.getPassword());
		var userAccount = userAccounts.create(form.getName(), password, CUSTOMER_ROLE);
		
		return users.save(new User(userAccount, form.getAddress()));
	}

	public String changePassword(PasswordForm form) {

		Assert.notNull(form, "Registration form must not be null!");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		var user = userAccounts.findByUsername(auth.getName());
		userAccounts.changePassword(user.get(), UnencryptedPassword.of(form.getNewPassword()));
		return "password changed";
	}

	public Streamable<User> findAll() {
		return users.findAll();
	}
}