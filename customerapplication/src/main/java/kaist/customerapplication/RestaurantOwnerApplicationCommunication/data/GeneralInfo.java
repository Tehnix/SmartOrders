package kaist.customerapplication.RestaurantOwnerApplicationCommunication.data;


public class GeneralInfo {
    private String name;
    private String street;
    private String city;
    private String description;
    private ContactInfo contact;

    public GeneralInfo(String n, String s, String c, String d, ContactInfo co){
        name = n;
        street = s;
        city = c;
        description = d;
        contact = co;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ContactInfo getContact() {
        return contact;
    }

    public void setContact(ContactInfo contact) {
        this.contact = contact;
    }
}