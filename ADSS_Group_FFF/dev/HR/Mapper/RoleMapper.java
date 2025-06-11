package HR.Mapper;

import HR.Domain.Role;
import HR.DTO.RoleDTO;

public class RoleMapper {
    public static RoleDTO toDTO(Role r) {
        if (r == null) return null;
        return new RoleDTO(r.getName());
    }

    public static Role fromDTO(RoleDTO dto) {
        if (dto == null) return null;
        // Domain Role has only a single‐arg constructor: Role(String name)
        return new Role(dto.getName());
    }
}
