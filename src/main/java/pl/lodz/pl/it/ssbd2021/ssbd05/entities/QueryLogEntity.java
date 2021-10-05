package pl.lodz.pl.it.ssbd2021.ssbd05.entities;

import lombok.Setter;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Setter
@Entity
@Table(name = "query_log", schema = "public")
public class QueryLogEntity extends AbstractEntity {
    private OffsetDateTime actionTimestamp;
    private String query;
    private String module;
    private String affectedTable;
    private UserEntity user;

    @Basic
    @Column(name = "action_timestamp", nullable = false)
    public OffsetDateTime getActionTimestamp() {
        return actionTimestamp;
    }

    @Basic
    @Column(name = "query", nullable = false, length = 512)
    public String getQuery() {
        return query;
    }

    @Basic
    @Column(name = "module", nullable = false, length = 64)
    public String getModule() {
        return module;
    }

    @Basic
    @Column(name = "affected_table", nullable = true, length = 64)
    public String getAffectedTable() {
        return affectedTable;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = true)
    public UserEntity getUser() {
        return user;
    }
}
