package de.otto.hmac.repository;

import jdk.nashorn.internal.ir.annotations.Immutable;

import java.util.Set;

@Immutable
public class UserCredentials {

    private final String user;
    private final String password;
    private final Set<String> roles;

    private UserCredentials(final String user,
                            final String password,
                            final Set<String> roles) {
        this.user = user;
        this.password = password;
        this.roles = roles;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserCredentials that = (UserCredentials) o;

        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        return !(roles != null ? !roles.equals(that.roles) : that.roles != null);

    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (roles != null ? roles.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserCredentials{" +
                "user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", roles=" + roles +
                '}';
    }
}
