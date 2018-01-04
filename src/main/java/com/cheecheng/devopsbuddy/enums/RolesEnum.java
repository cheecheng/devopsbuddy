package com.cheecheng.devopsbuddy.enums;

public enum RolesEnum {

    // Spring security requires roles to be prefixed by "ROLE_"
    BASIC(1, "ROLE_BASIC"),
    PRO(2, "ROLE_PRO"),
    ADMIN(3, "ROLE_ADMIN");

    private final int id;
    private final String roleName;

    RolesEnum(int id, String roleName) {
        this.id = id;
        this.roleName = roleName;
    }

    public int getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }
}

/*
https://docs.oracle.com/javase/tutorial/java/javaOO/enum.html
Java requires that the constants be defined first, prior to any fields or methods.
Also, when there are fields and methods, the list of enum constants must end with a semicolon.
Note: The constructor for an enum type must be package-private or private access.
It automatically creates the constants that are defined at the beginning of the enum body.
You cannot invoke an enum constructor yourself.

https://stackoverflow.com/questions/3054247/how-to-define-properties-for-enum-items
Enums are constants and are immutable as such.
If you have fields in your enums, you should make them final and provide getters only.
 */