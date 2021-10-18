package pl.lodz.pl.it.ssbd2021.ssbd05.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Setter
@Getter
@MappedSuperclass
public abstract class AbstractEntity {
    @Setter(AccessLevel.PROTECTED)
    private long id;
    private Long version;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public long getId() {
        return id;
    }

    @Basic
    @Version
    @Column(name = "version", nullable = false, columnDefinition = "BIGINT default 1")
    public Long getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        AbstractEntity other = (AbstractEntity) obj;
        return this.getId() == other.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
