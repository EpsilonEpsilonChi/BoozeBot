package chi.epsilon.epsilon.boozebot.models;

public class User {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private boolean isEex = false;

    public User() {
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public boolean isEex() {
        return isEex;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEex(boolean eex) {
        isEex = eex;
    }

    private User(String firstName, String lastName, String username, String email, boolean isEEX) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.isEex = isEEX;
    }

    public static class UserBuilder {
        private String firstName;
        private String lastName;
        private String username;
        private String email;
        private boolean isEex = false;

        public UserBuilder() {}
        public UserBuilder firstName(String fn) {
            this.firstName = fn;
            return this;
        }

        public UserBuilder lastName(String ln) {
            this.lastName = ln;
            return this;
        }

        public UserBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder isEEX(boolean is_eex) {
            this.isEex = is_eex;
            return this;
        }

        public User build() {
            return new User(firstName, lastName, username, email, isEex);
        }
    }
}
