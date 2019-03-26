package shared;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Time;

@Data
@NoArgsConstructor
@Entity
@Table
public class TopData implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private long id;
    private String ip = "";
    private short pid;
    private double cpu;
    private double mem;
    private Time time;
}
