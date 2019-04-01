package server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "user_lab")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @Column(name = "login", nullable = false)
    private String login;

    @Column(name = "password", nullable = false)
    private String password;

    @ElementCollection
    @CollectionTable(name = "tokens",
            joinColumns = @JoinColumn(name = "user_login"))
    private List<String> refreshTokens;

    private boolean isConfirmed;
}
