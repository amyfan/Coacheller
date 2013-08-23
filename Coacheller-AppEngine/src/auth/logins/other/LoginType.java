package auth.logins.other;

public enum LoginType {
  FACEBOOK("Facebook"), GOOGLE("Google"), TWITTER("Twitter");
  private String _typeName;

  LoginType(String typeName) {
    _typeName = typeName;
  }
  
  public String getName() {
    return _typeName;
  }
  
  public static LoginType fromString(String input) {
    for (LoginType type : LoginType.values()) {
      if (type.getName().equals(input)) {
        return type;
      }
    }
    return null;
  }
  
  public boolean equals(LoginType loginType) {
      return _typeName.equals(loginType.getName());
  }
}
