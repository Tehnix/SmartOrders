package kaist.restaurantownerapp.data;

public class ContactInfo {
    private String phone;
    private String mail;

    public ContactInfo(String p, String m){
        phone = p;
        mail = m;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
