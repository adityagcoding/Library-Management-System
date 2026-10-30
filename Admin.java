package model;

/**
 * Model class representing an Admin user.
 */
public class Admin {
    private int adminId;
    private String username;
    private String password;

    /**
     * Default constructor.
     */
    public Admin() {
    }

    /**
     * Parameterized constructor.
     */
    public Admin(int adminId, String username, String password) {
        this.adminId = adminId;
        this.username = username;
        this.password = password;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId=" + adminId +
                ", username='" + username + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}
