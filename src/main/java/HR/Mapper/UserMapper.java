// File: HR/Mapper/UserMapper.java
package HR.Mapper;

import HR.Domain.User;
import HR.Domain.Role;
import HR.DTO.UserDTO;
import HR.DTO.RoleDTO;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    public static UserDTO toDTO(User u) {
        if (u == null) return null;

        List<RoleDTO> roleDtos = u.getRoles().stream()
                .map(r -> new RoleDTO(r.getName()))
                .collect(Collectors.toList());

        UserDTO dto = new UserDTO(u.getId(), u.getName(), roleDtos);
        // Note: we intentionally do NOT include u.getPassword() in the DTO.
        return dto;
    }

    public static User fromDTO(UserDTO dto) {
        if (dto == null) return null;

        // Domain User constructor is:
        //     public User(String id, String name, String password, List<Role> roles)
        // We only have id, name, roles in the DTO—set password to null or empty.
        List<Role> roles = dto.getRoles().stream()
                .map(rdto -> new Role(rdto.getName()))
                .collect(Collectors.toList());

        return new User(
                dto.getId(),
                dto.getName(),
                null,         // no password in DTO; set to null or handle separately
                roles
        );
    }
}
