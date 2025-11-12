package es.codeurjc.dto;


import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import es.codeurjc.model.User;


@Mapper(componentModel = "spring")
public interface UserMapper {

    public  UserDTO toDTO(User user);


    BasicUserDTO map(User user);

    List<UserDTO> toDTOs(Collection<User> users);

    User toDomain(UserDTO userDTO);

}
