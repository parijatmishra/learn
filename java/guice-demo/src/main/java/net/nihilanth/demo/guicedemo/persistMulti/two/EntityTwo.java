package net.nihilanth.demo.guicedemo.persistMulti.two;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * A JPA entity
 */
@Entity
public class EntityTwo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    public EntityTwo(String name) {
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

        EntityTwo entityTwo = (EntityTwo) o;

        if (id != null ? !id.equals(entityTwo.id) : entityTwo.id != null) return false;
        return name != null ? name.equals(entityTwo.name) : entityTwo.name == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EntityTwo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
