package model;

/**
 * Model class representing a Library Member.
 */
public class Member {
    private int memberId;
    private String name;
    private String contact;
    private String email;
    private String membershipDate;

    /**
     * Default constructor.
     */
    public Member() {
    }

    /**
     * Parameterized constructor.
     */
    public Member(int memberId, String name, String contact, String email, String membershipDate) {
        this.memberId = memberId;
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.membershipDate = membershipDate;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMembershipDate() {
        return membershipDate;
    }

    public void setMembershipDate(String membershipDate) {
        this.membershipDate = membershipDate;
    }

    @Override
    public String toString() {
        return "Member{" +
                "memberId=" + memberId +
                ", name='" + name + '\'' +
                ", contact='" + contact + '\'' +
                ", email='" + email + '\'' +
                ", membershipDate='" + membershipDate + '\'' +
                '}';
    }
}
