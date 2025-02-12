package com.BeeOranized.BeeOranized.Entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "ChefScrums")
@DiscriminatorValue("chefScrum")
public class ChefScrum extends User {

    public ChefScrum(String name, String email, String password, String city,
            Set<Role> roles) {

        super(name, email, password, city, roles);
        this.chefScrumFirstName = chefScrumFirstName;
        this.chefScrumLastName = chefScrumLastName;
        this.chefScrumBirthDay = chefScrumBirthDay;
        this.chefScrumGender = chefScrumGender;

    }

    @Column(name = "chefScrumFirstName")
    private String chefScrumFirstName;

    @Column(name = "chefScrumLastName")
    private String chefScrumLastName;

    @Column(name = "chefScrumBirthDay")
    private String chefScrumBirthDay;

    @Column(name = "chefScrumGender")
    private String chefScrumGender;

    public ChefScrum() {
        super();
    }
}