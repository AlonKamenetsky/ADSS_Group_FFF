package Transportation.Domain.Repositories;

import Transportation.DTO.TransportationTaskDTO;
import Transportation.DataAccess.DAO.SqliteTransportationTaskDAO;
import Transportation.DataAccess.DAO.SqliteTruckDAO;
import Transportation.DataAccess.DAO.TransportationTaskDAO;
import Transportation.DataAccess.DAO.TruckDAO;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class TransportationTaskRepositoryImpli implements TransportationTaskRepository {

    private TransportationTaskDAO taskDAO;
    public TransportationTaskRepositoryImpli() {
        taskDAO = new SqliteTransportationTaskDAO();
    }


    @Override
    public TransportationTaskDTO createTask(String taskDate, String departureTime, String sourceSiteAddress) throws ParseException, SQLException {
        Date parsedDate = new SimpleDateFormat("dd/MM/yyyy").parse(taskDate);
        LocalTime parsedTime = LocalTime.parse(departureTime, DateTimeFormatter.ofPattern("HH:mm"));

        TransportationTaskDTO newTask = new TransportationTaskDTO(
                null,
                parsedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                parsedTime,
                sourceSiteAddress,
                new ArrayList<>(),
                "",
                "",
                0
        );

        return taskDAO.insert(newTask);
    }


    @Override
    public void deleteTask(int taskId) throws SQLException {
        taskDAO.delete(taskId);

    }

    @Override
    public Optional<TransportationTaskDTO> findTask(int taskId) throws SQLException {
        return taskDAO.findById(taskId);
    }

    @Override
    public Optional<TransportationTaskDTO> findTaskByDateTimeAndSource(Date taskDate, LocalTime departureTime, String sourceSite) throws SQLException {
        return taskDAO.findByDateTimeAndSource(taskDate, departureTime, sourceSite);
    }

    @Override
    public List<TransportationTaskDTO> findAllTasks() throws SQLException {
        return taskDAO.findAll();
    }

    @Override
    public List<TransportationTaskDTO> findTaskBySourceAddress(String sourceAddress) throws SQLException {
        return taskDAO.findBySourceAddress(sourceAddress);
    }

    @Override
    public List<TransportationTaskDTO> findTaskByDriverId(String driverId) throws SQLException {
        return taskDAO.findByDriverId(driverId);
    }
}
