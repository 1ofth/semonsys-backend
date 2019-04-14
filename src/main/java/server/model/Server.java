package server.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "server")
@Data
public class Server {
    private static final int PORT_DEFAULT = 12122;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "server_id_seq")
    @SequenceGenerator(name = "server_id_seq", sequenceName = "server_id_seq", allocationSize = 1)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_login")
    private User user;
    private String name;
    private String description;
    private String ip;
    // default values
    private Integer port;
    private Boolean activated;
    @Column(name = "act_data")
    private String actData;

    public Server() {
    }

    public Server(final User user, final String name,
                  final String description, final String ip) {
        this.user = user;
        this.name = name;
        this.description = description;
        this.ip = ip;

        // default values
        this.port = PORT_DEFAULT;
        this.activated = false;
        this.actData = "";
    }
}
