package net.nihilanth.demo.guicedemo.persistMulti.one;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * A JPA Entity
 */
@Entity
public class EntityOne {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    protected EntityOne() {}

    public EntityOne(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntityOne entityOne = (EntityOne) o;

        if (id != null ? !id.equals(entityOne.id) : entityOne.id != null) return false;
        return name != null ? name.equals(entityOne.name) : entityOne.name == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EntityOne{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
