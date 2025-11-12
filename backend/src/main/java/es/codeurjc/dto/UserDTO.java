package es.codeurjc.dto;

import java.time.LocalDateTime;
import java.util.List;

public record UserDTO (
    Long id,
    String realname,
    String username,
	String email,
    String password,
    LocalDateTime birthDate,
    Boolean gender,
    String description,
    Float level,
    PlayerStatsDTO stats,
    List<String> roles){
        public UserDTO(Long id, String realname, String username, String email, String password,
                   LocalDateTime birthDate, Boolean gender, String description,
                   Float level, List<String> roles) {
        this(id, realname, username, email, password, birthDate, gender, description, level,null,roles);
    }
}
