package pharmacy.users;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

class CustomerForm {

	@NotEmpty(message = "{RegistrationForm.name.NotEmpty}")
	private final String name;

	@NotEmpty(message = "{RegistrationForm.email.NotEmpty}")
	private final String email;

	@NotEmpty(message = "{RegistrationForm.password.NotEmpty}")
	@NotEmpty(message = "{PasswordForm.newPassword.NotEmpty}")
	@Size(min = 8, max = 128, message = "{PasswordForm.newPassword.Size}")
	@Pattern(regexp="^(?=.*[a-z]).+$", message = "{PasswordForm.newPassword.Lower}")
	@Pattern(regexp="^(?=.*[A-Z]).+$", message = "{PasswordForm.newPassword.Upper}")
	@Pattern(regexp="^(?=.*[-+_!@#$%^&*.,?]).+$", message = "{PasswordForm.newPassword.Special}")
	@Pattern(regexp="^[\\S]+$", message = "{PasswordForm.newPassword.Space}")
	private final String password;
	
	@NotEmpty(message = "{DeliveryForm.name.NotEmpty}")
	private final String street;

	@NotEmpty(message = "{DeliveryForm.email.NotEmpty}")
	private final String houseNumber;

	@NotNull(message = "{DeliveryForm.email.NotEmpty}")
	private final Number postCode;

	@NotEmpty(message = "{DeliveryForm.email.NotEmpty}")
	private final String city;
	
	private final Boolean privateInsurance;

	public CustomerForm(String name, String email, String password, String street, String houseNumber, Number postCode, String city, Boolean privateInsurance) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.street = street;
		this.houseNumber = houseNumber;
		this.postCode = postCode;
		this.city = city;
		this.privateInsurance = privateInsurance;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public String getStreet() {
		return street;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public Number getPostCode() {
		return postCode;
	}

	public String getCity() {
		return city;
	}

	public Boolean getPrivateInsurance() {
		return privateInsurance;
	}
}