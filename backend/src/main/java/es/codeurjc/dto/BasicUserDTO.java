package es.codeurjc.dto;

public record BasicUserDTO (
    Long id,
    String realname,
    String username,
    Float level) {
}
