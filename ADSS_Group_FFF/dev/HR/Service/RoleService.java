package HR.Service;

import HR.DTO.RoleDTO;
import HR.DataAccess.*;
import HR.Domain.*;
import HR.Mapper.RoleMapper;
import HR.Presentation.PresentationUtils;
import Util.Database;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class RoleService {
    private static RoleService instance;
    private final RoleDAO roleDAO;

    private RoleService() {
        Connection conn = Database.getConnection();
        this.roleDAO = new RoleDAOImpl(conn);
    }

    public static synchronized RoleService getInstance() {
        if (instance == null) {
            instance = new RoleService();
        }
        return instance;
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
        return roleDAO.findAll().stream().map(RoleMapper::toDTO).collect(Collectors.toList());
    }

    public RoleDTO getRoleByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        Role r = roleDAO.findByName(name);
        return RoleMapper.toDTO(r);
    }
}
