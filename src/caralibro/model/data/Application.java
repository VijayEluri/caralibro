package caralibro.model.data;

/*
 * Models a Facebook Application.
 * To create one you need to have a Facebook account. 
 * Log in and go to Facebook's Developer Application, www.facebook.com/developers
 * Click "Allow" to let the Developer Application access your profile.
 * Click "Set Up New Application" to create yours.
 * To use your Application with Caralibro you need the "API Key", "Application Secret" and "Application ID".
 * Now go to "Edit Settings" on your Application page.
 * On the "Advanced" tab, Set your application as being of type "Desktop" and disable the "Sandbox Mode".
 * 
 * @author		Federico Pascual Mastellone (fmaste@gmail.com)
 * @author		Simon Aberg Cobo (sima.cobo@gmail.com)
 */
public class Application implements Source {
	private Long id = null;
	private String name = null;
	private String key = null;
	private String secret = null;
	private Boolean desktop = null;

	public Application() {
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
	
	public Boolean isDesktop() {
		return desktop;
	}
	
	public void setDesktop(Boolean desktop) {
		this.desktop = desktop;
	}
	
}
