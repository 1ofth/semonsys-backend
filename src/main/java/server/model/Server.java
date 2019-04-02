package server.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "server")
@Data
public class Server {
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
    private Integer port;
    private Boolean activated;
    private String act_data;

    public Server() {
    }
    public Server(User user, String name, String description, String ip) {
        this.user = user;
        this.name = name;
        this.description = description;
        this.ip = ip;

        // default values
        this.port = 12122;
        this.activated = false;
        this.act_data = "";
    }
}
