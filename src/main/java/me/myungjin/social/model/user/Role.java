package me.myungjin.social.model.user;

public enum Role {

    USER("ROLE_USER");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    // TODO 없어도 되는 메소드인지?
    /*public static Role of(String name) {
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(name)) {
                return role;
            }
        }
        return null;
    }
    */
}
