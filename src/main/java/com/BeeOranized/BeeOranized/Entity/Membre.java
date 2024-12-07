package com.BeeOranized.BeeOranized.Entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "Members")
@DiscriminatorValue("membre")
public class Membre extends User {

    public Membre(String name, String email, String password, String city,
            Set<Role> roles) {

        super(name, email, password, city, roles);
        this.membreFirstName = membreFirstName;
        this.membreLastName = membreLastName;
        this.membreBirthDay = membreBirthDay;
        this.membreGender = membreGender;

    }

    @Column(name = "membreFirstName")
    private String membreFirstName;

    @Column(name = "membreLastName")
    private String membreLastName;

    @Column(name = "membreBirthDay")
    private String membreBirthDay;

    @Column(name = "membreGender")
    private String membreGender;

    public String getMembreFirstName() {
        return membreFirstName;
    }

    public void setMembreFirstName(String membreFirstName) {
        this.membreFirstName = membreFirstName;
    }

    public String getMembreLastName() {
        return membreLastName;
    }

    public void setMembreLastName(String membreLastName) {
        this.membreLastName = membreLastName;
    }

    public String getMembreBirthDay() {
        return membreBirthDay;
    }

    public void setMembreBirthDay(String membreBirthDay) {
        this.membreBirthDay = membreBirthDay;
    }

    public String getMembreGender() {
        return membreGender;
    }

    public void setMembreGender(String membreGender) {
        this.membreGender = membreGender;
    }

    public Membre() {
        super();
    }
}