package server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "t_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @Column(name = "login", nullable = false)
    private String login;

    @Column(name = "password", nullable = false)
    private String password;

    private String email;

    @ElementCollection
    @CollectionTable(name = "tokens",
            joinColumns = @JoinColumn(name = "user_login"))
    private List<String> refreshTokens;

    private Boolean verified;

    private String verificationToken;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
