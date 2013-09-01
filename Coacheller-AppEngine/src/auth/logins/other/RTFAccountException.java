package auth.logins.other;

import com.ratethisfest.server.domain.AppUser;

import auth.logins.data.AuthProviderAccount;

public class RTFAccountException extends Exception {

  private AppUser _originalOwner;
  private AppUser _newClaimant;
  private AuthProviderAccount _contestedAPAccount;

  public RTFAccountException(AppUser currentSessionLogin, AppUser ownerOfAddedAPAccount,
      AuthProviderAccount newAPAccountLogin) {
    _originalOwner = ownerOfAddedAPAccount;
    _newClaimant = currentSessionLogin;
    _contestedAPAccount = newAPAccountLogin;
  }
  
  public AppUser getOriginalOwner() {
    return _originalOwner;
  }
  
  public AppUser getNewClaimant() {
    return _newClaimant;
  }
  
  public AuthProviderAccount getContestedAPAccount() {
    return _contestedAPAccount;
  }
  
}
