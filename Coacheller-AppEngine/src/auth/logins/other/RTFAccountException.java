package auth.logins.other;

import auth.logins.data.AuthProviderAccount;
import auth.logins.data.MasterAccount;

public class RTFAccountException extends Exception {

  private MasterAccount _originalOwner;
  private MasterAccount _newClaimant;
  private AuthProviderAccount _contestedAPAccount;

  public RTFAccountException(MasterAccount currentSessionLogin, MasterAccount ownerOfAddedAPAccount,
      AuthProviderAccount newAPAccountLogin) {
    _originalOwner = ownerOfAddedAPAccount;
    _newClaimant = currentSessionLogin;
    _contestedAPAccount = newAPAccountLogin;
  }
  
  public MasterAccount getOriginalOwner() {
    return _originalOwner;
  }
  
  public MasterAccount getNewClaimant() {
    return _newClaimant;
  }
  
  public AuthProviderAccount getContestedAPAccount() {
    return _contestedAPAccount;
  }
  
}
