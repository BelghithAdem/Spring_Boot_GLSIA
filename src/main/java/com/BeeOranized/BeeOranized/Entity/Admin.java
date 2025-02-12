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


    public Admin() {
        super();
    }
}
