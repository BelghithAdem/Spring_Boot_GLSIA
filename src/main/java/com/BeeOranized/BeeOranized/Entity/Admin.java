package com.BeeOranized.BeeOranized.Entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "admins")
@DiscriminatorValue("admin")
public class Admin extends User {

    public Admin(String name, String email, String password, String city,
            Set<Role> roles) {

        super(name, email, password, city, roles);
        this.adminFirstName = adminFirstName;
        this.adminLastName = adminLastName;
        this.adminBirthDay = adminBirthDay;
        this.adminGender = adminGender;

    }

    @Column(name = "adminFirstName")
    private String adminFirstName;

    @Column(name = "adminLastName")
    private String adminLastName;

    @Column(name = "adminBirthDay")
    private String adminBirthDay;

    @Column(name = "adminGender")
    private String adminGender;

    public String getAdminFirstName() {
        return adminFirstName;
    }

    public void setAdminFirstName(String adminFirstName) {
        this.adminFirstName = adminFirstName;
    }

    public String getAdminLastName() {
        return adminLastName;
    }

    public void setAdminLastName(String adminLastName) {
        this.adminLastName = adminLastName;
    }

    public String getAdminBirthDay() {
        return adminBirthDay;
    }

    public void setAdminBirthDay(String adminBirthDay) {
        this.adminBirthDay = adminBirthDay;
    }

    public String getAdminGender() {
        return adminGender;
    }

    public void setAdminGender(String adminGender) {
        this.adminGender = adminGender;
    }

    public Admin() {
        super();
    }
}
