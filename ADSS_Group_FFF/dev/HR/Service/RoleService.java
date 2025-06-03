package HR.Service;

import HR.DTO.RoleDTO;
import HR.DataAccess.RoleDAO;
import HR.Domain.Role;
import HR.Mapper.RoleMapper;

import java.util.List;
import java.util.stream.Collectors;

public class RoleService {

    private final RoleDAO roleDAO;

    public RoleService(RoleDAO roleDAO) {
        this.roleDAO = roleDAO;
    }

    public void addRole(RoleDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("RoleDTO must not be null");
        }
        Role r = RoleMapper.fromDTO(dto);
        roleDAO.insert(r);
    }

    public void removeRole(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Role name must not be null/empty");
        }
        roleDAO.delete(name);
    }

    public List<RoleDTO> getRoles() {
        return roleDAO.findAll().stream()
                .map(RoleMapper::toDTO)
                .collect(Collectors.toList());
    }

    public RoleDTO getRoleByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        Role r = roleDAO.findByName(name);
        return RoleMapper.toDTO(r);
    }
}
