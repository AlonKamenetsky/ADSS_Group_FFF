package SuppliersModule.DataLayer;

public abstract class DTO {
    protected DbController dbController;

    public DTO(DbController dbController) {
        this.dbController = dbController;
    }
}
