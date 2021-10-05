package pl.lodz.pl.it.ssbd2021.ssbd05.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.json.bind.annotation.JsonbTypeAdapter;
import java.util.Objects;

/**
 * Klasa abstrakcyjna bazowa obiektów transportowych do widoku
 */
@ToString
@AllArgsConstructor
public abstract class AbstractDTO {

    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    @JsonbTypeAdapter(VersionCryptJsonbAdapter.class)
    private Long version;

    public AbstractDTO() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractDTO that = (AbstractDTO) o;
        return id == that.id && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, version);
    }
}
